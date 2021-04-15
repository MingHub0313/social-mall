package com.zmm.mall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.enums.OrderStatusEnum;
import com.zmm.common.exception.NoStockException;
import com.zmm.common.to.mq.OrderTo;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.common.utils.R;
import com.zmm.common.utils.redis.RedisUtil;
import com.zmm.common.utils.redis.key.OrderKey;
import com.zmm.common.utils.redis.key.RedisKey;
import com.zmm.common.vo.MemberRespVo;
import com.zmm.mall.order.dao.OrderDao;
import com.zmm.mall.order.entity.OrderEntity;
import com.zmm.mall.order.entity.OrderItemEntity;
import com.zmm.mall.order.entity.PaymentInfoEntity;
import com.zmm.mall.order.feign.CartFeignService;
import com.zmm.mall.order.feign.MemberFeignService;
import com.zmm.mall.order.feign.ProductFeignService;
import com.zmm.mall.order.feign.WareFeignService;
import com.zmm.mall.order.interceptor.LoginUserInterceptor;
import com.zmm.mall.order.service.OrderItemService;
import com.zmm.mall.order.service.OrderService;
import com.zmm.mall.order.service.PaymentInfoService;
import com.zmm.mall.order.to.OrderCreatTo;
import com.zmm.mall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> submitVoThreadLocal = new ThreadLocal<>();


    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private OrderItemService orderItemService;

    @Resource
    private MemberFeignService memberFeignService;

    @Resource
    private CartFeignService cartFeignService;

    @Resource
    private ThreadPoolExecutor executor;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WareFeignService wareFeignService;

    @Resource
    private ProductFeignService productFeignService;
    
    @Resource
    private PaymentInfoService paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        // 让所有的线程都共享 这个 requestAttributes RequestContextHolder 中的线程的数据都不一样
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 使用异步 feign 会丢失上下文
        CompletableFuture<Void> futureAddressVoList = CompletableFuture.runAsync(() -> {
            // 1.远程查询所有的收货地址列表
            // 每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> memberAddressVoList = memberFeignService.getAddress(memberRespVo.getId());
            orderConfirmVo.setMemberAddressVoList(memberAddressVoList);
        }, executor);


        CompletableFuture<Void> futureItemVoList = CompletableFuture.runAsync(() -> {
            // 2.远程查询购物车所有选中的购物项
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> orderItemVoList = cartFeignService.getCartItemByUser();
            // feign 远程调用会丢失请求头的 ===> 解决方案:加上 feign 远程调用的请求拦截器
            // feign 在远程调用之前要构造请求,调用很多的拦截器 RequestInterceptor interceptor : requestInterceptors
            orderConfirmVo.setOrderItemVoList(orderItemVoList);
        }, executor).thenRunAsync(()->{
            // 上一步执行完之后 去库存服务查询 库存信息
            List<OrderItemVo> orderItemVoList = orderConfirmVo.getOrderItemVoList();
            List<Long> skuIdList = orderItemVoList.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R result = wareFeignService.getSkuHasStock(skuIdList);
            // 获取每一个 skuId 的库存信息
            List<SkuStockVo> stockVoList = result.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (!CollectionUtils.isEmpty(stockVoList)){
                Map<Long,Boolean> stocks = stockVoList.stream().collect(
                        Collectors.toMap(SkuStockVo::getSkuId,SkuStockVo::getHasStock));
                orderConfirmVo.setStocks(stocks);
            }
        },executor);


        // 3.查询用户积分
        orderConfirmVo.setIntegration(memberRespVo.getIntegration());

        CompletableFuture.allOf(futureAddressVoList,futureItemVoList).get();

        // 4.其他数据自动计算

        // 5.防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisUtil.set(OrderKey.USER_ORDER_TOKEN.setSuffix(memberRespVo.getId()),token,30, TimeUnit.MINUTES);
        orderConfirmVo.setOrderToken(token);
        return orderConfirmVo;
    }

    /**
     * 下单 高并发场景 @GlobalTransaction 高并发中 不考虑使用 AT/TCC
     * 一般使用 消息的模式 ===> 可靠消息+最终一致性
     * @description:
     * @author: Administrator
     * @date: 2021-03-18 22:01:19
     * @param orderSubmitVo: 
     * @return: com.zmm.mall.order.vo.SubmitOrderResponseVo
     **/

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.MANDATORY)
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        submitVoThreadLocal.set(orderSubmitVo);
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        response.setCode(0);

        OrderKey orderKey = OrderKey.USER_ORDER_TOKEN;

        // 1.验令牌 【令牌的对比 和 删除 必须保证原子性】 ==> lua 脚本
        String orderToken = orderSubmitVo.getOrderToken();
        RedisKey redisKey = orderKey.setSuffix(memberRespVo.getId());

        // 一般的处理操作
        //generallyHandle()

        // Lua 脚本 返回 0 或者 1  0: 令牌验证失败 1: 删除成功(都能删除删除比存在)
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        Long result = redisUtil.execute(script,redisKey,orderToken);
        if (result == 0){
            // 令牌验证失败
            response.setCode(1);
            return response;
        }

        // 2.创建订单
        OrderCreatTo orderCreatTo = createOrder(memberRespVo.getId());

        // 3.验价格
        BigDecimal payAmount = orderCreatTo.getOrderEntity().getPayAmount();
        BigDecimal payPrice = orderSubmitVo.getPayPrice();
        // 差价小于0.01
        if ( Math.abs(payAmount.subtract(payPrice).doubleValue()) >= 0.01 ){
            // 金额对比失败
            response.setCode(2);
            return response;
        }

        // TODO 4.保存订单 出现异常 订单回滚 √
        saveOrder(orderCreatTo);


        // 5.锁库存 ... 如果有异常回滚订单数据
        // 5.1订单号 5.2所有订单项(skuId,skuName,num)
        WareSkuLockVo skuLockVo = new WareSkuLockVo();
        skuLockVo.setOrderSn(orderCreatTo.getOrderEntity().getOrderSn());
        List<OrderItemVo> orderItemVos = orderCreatTo.getOrderItemEntities().stream().map(item -> {
            OrderItemVo itemVo = new OrderItemVo();
            itemVo.setSkuId(item.getSkuId());
            itemVo.setCount(item.getSkuQuantity());
            itemVo.setTitle(item.getSkuName());
            return itemVo;
        }).collect(Collectors.toList());
        skuLockVo.setLocks(orderItemVos);
        /**
         * Transactional --> 在分布式系统,只能控制住自己的回滚 控制不了其他服务的回滚
         * 本地事务 出现的问题
         * 由异常导致的事务问题:
         * QUESTION 1 本身远程锁库存成功 但是因为网络原因 导致出现异常 从而影响 订单创建回滚                 ==>库存锁成功 但是订单回滚了   (远程服务假失败 真是的库存锁成功)
         * QUESTION 2 本身远程锁库存成功 但是因为调用其他服务出现异常 已执行的远程请求,肯定不能回滚            ==>库存锁成功 但是订单回滚了
         * 应该使用 分布式事务 ===> 最大原因 网络问题 + 分布式机器
         */
        // TODO 6.远程锁库存 ==> 远程执行失败 需要将上面执行的操作全部回滚 (需要抛出去才能@Transactional 生效) QUESTION 1 [可能会超时 --> 会抛出异常(读取超时),但是库存锁成功了 但因为(假)异常 订单回滚了]
        R r = wareFeignService.orderLockStock(skuLockVo);
        if (r.getCode() == 0){
            // 锁定成功
            response.setOrderEntity(orderCreatTo.getOrderEntity());
            // TODO 7.远程扣减积分 如果出现异常 -->进行回滚  -->订单可以回滚   √ 但是 远程的锁库存服务无法回滚 QUESTION 2 [订单回滚,库存不回滚]
            // int i = 10/0;
            // TODO 订单创建成功 发送消息给 MQ
            rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",orderCreatTo.getOrderEntity());
        } else {
            // 锁定失败 ---> 为了保证高并发 库存系统自己会难回滚 1.可以发消息给库存服务 / 2.库存服务本身也可以使用自动解锁模式 (消息队列)
            // TODO 如何库存出现异常 1.远程的库存服务会自动回滚 2.然后保存订单也会回滚 (异常机制-- 感知异常进行回滚)
            String msg = (String)r.get("msg");
            throw new NoStockException(3L);
            //response.setCode(3)
        }
        return response;
    }

    /**
     * 一般处理的方法
     */
    private void generallyHandle(){
        /*if (ObjectUtils.isEmpty(o)){
            // 缓存中不存在
            return null
        } else {
            String redisToken = (String)o
            if (orderToken.equals(redisToken)){

            }
        }

        if (!ObjectUtils.isEmpty(o) && orderToken.equals(o)){
            // 还是防不住
            //令牌验证通过
        } else {
            // 缓存中不存在
            return null
        }
         */
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return orderEntity;
    }

    @Override
    public void closeOrder(OrderEntity orderEntity) {
        // 1.查询当前订单的最新状态
        OrderEntity currentOrder = this.getById(orderEntity.getId());
        if (currentOrder.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())){
            // 2.进行关单
            OrderEntity updateOrder = new OrderEntity();
            updateOrder.setId(orderEntity.getId());
            updateOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
            updateOrder.setModifyTime(new Date());
            this.updateById(updateOrder);
            // 订单解锁成功后  再发一个消息告诉 mq
            //  释放订单 的消息 以 order-event-exchange 发出 同时 order-event-exchange 绑定了 库存解锁的队列
            //  还要再解锁监听这个消息
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(currentOrder,orderTo);
            /**
             * 防止消息丢失: -- 分布式事务最终一致性就是防止消息丢失
             * 
             * 1.消息丢失 
             *      解决方案:   数据库做好日志记录(消息记录)
             * 2.消息抵达Broker Broker要将消息写入磁盘(持久化)才算持久化成功 如果Broker尚未持久化完成 发生宕机
             *      解决方案:   消息确认机制 -- 可靠抵达 (生产者的确认模式)
             * 3.自动ACK的状态下.消费者收到消息,但没来得及处理 服务器宕机了
             *      解决方案:   一定开启手动 ACK ,消费成功才移除,失败或者没来得及处理就 noACK 并重新加入队列
             * 
             */

            /**
             * 防止消息重复:
             *      1).消息消费成功,事物已经提交,ack 时 机器宕机.导致没有ack 成功.
             *      
             *      2).消息消费成功,由于重试机制,自动又将消息发送出去
             *      
             *      3).成功消费,ack宕机 消息由 unAck 状态变成ready ,Broker 又重新发送. 
             *      
             *    处理方案:
             *              a.消费者的业务消息接口应该设计为幂等性的.比如扣库存有工作单的状态标志
             *              b.使用防重表(redis/mysql), 发送消息每一个都有业务的唯一标识,处理过就不用处理
             *              c.rabbitmq 的每一个消息都有 redelivered 字段,可以获取是否被重新投递过来的而不是第一次投递.
             */


            /**
             * 消息积压:
             *  1).消费者宕机积压
             *  2).消费者消费能力不足积压
             *  3).发送者发送流量太大
             *  
             *  处理方案:
             *              a.上线更多的消费者,进行正常消费
             *              b.上线专门的队列消费服务,将消息先批量取出来,记录数据库 ,离线慢慢处理.
             */
            try {
                // TODO 保证消息一定会发送出去 / 每一个消息都可以做好日志记录 (给数据库保存每一个消息的详情信息)
                // TODO 定期扫描数据库将失败的消息再发送一遍
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
            } catch (Exception e){
                // 出现问题 将没有发送成功的消息进行重试发送
                //while(true)
            }
            
        }
    }

    private void saveOrder(OrderCreatTo orderCreatTo) {
        OrderEntity orderEntity = orderCreatTo.getOrderEntity();
        // 设置值
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);
        List<OrderItemEntity> orderItemEntities = orderCreatTo.getOrderItemEntities();
        // 批量插入数据库
        orderItemService.saveBatch(orderItemEntities);
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        OrderEntity orderByOrderSn = this.getOrderByOrderSn(orderSn);
        OrderItemEntity itemEntity = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn)).get(0);
        PayVo payVo = new PayVo();
        payVo.setBody(itemEntity.getSkuAttrsVals());
        payVo.setSubject(itemEntity.getSkuName());
        payVo.setOut_trade_no(orderByOrderSn.getOrderSn());
        payVo.setTotal_amount(orderByOrderSn.getPayAmount().setScale(2, RoundingMode.UP).toString());
        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",memberRespVo.getId()).orderByDesc("id")
        );
        List<OrderEntity> order_sn = page.getRecords().stream().map(orderEntity -> {
            List<OrderItemEntity> orderItemEntityList = orderItemService.list(
                    new QueryWrapper<OrderItemEntity>().eq("order_sn", orderEntity.getOrderSn()));
            orderEntity.setItemEntityList(orderItemEntityList);
            return orderEntity;
        }).collect(Collectors.toList());
        page.setRecords(order_sn);
        return new PageUtils(page);
    }

    @Override
    public String handlePayResult(PayAsyncVo vo) {
        
        // 1.保存交易流水
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setAlipayTradeNo(vo.getTrade_no());
        paymentInfoEntity.setOrderSn(vo.getOut_trade_no());
        paymentInfoEntity.setPaymentStatus(vo.getTrade_status());
        paymentInfoEntity.setCallbackTime(vo.getNotify_time());
        paymentInfoService.save(paymentInfoEntity);
        // 2.修改订单状态
        if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")){
            // 支付成功状态
            String orderSn = vo.getOut_trade_no();
            this.baseMapper.updateOrderPayFinishedStatus(orderSn,OrderStatusEnum.PAYED.getCode());
        }
        return "success";
    }

    /**
     * 创建订单
     * @description:
     * @author: Administrator
     * @date: 2021-03-11 22:11:02
     * @return: com.zmm.mall.order.to.OrderCreatTo
     **/
    private OrderCreatTo createOrder(Long userId){
        OrderCreatTo orderCreatTo = new OrderCreatTo();

        OrderEntity orderEntity = buildOrder(userId);


        // 2.获取到所有的的订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderEntity.getOrderSn());

        // 3.计算价格、积分相关信息
        computePrice(orderEntity,orderItemEntities);

        orderCreatTo.setOrderEntity(orderEntity);
        orderCreatTo.setOrderItemEntities(orderItemEntities);
        return orderCreatTo;
    }

    /**
     * 校验价格
     * @description:
     * @author: Administrator
     * @date: 2021-03-11 22:12:33
     * @param orderEntity: 
     * @param orderItemEntities: 
     * @return: void
     **/
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        // 1.订单总额 将 OrderItemEntity 对象中的 SkuPrice 取出来 map为 BigDecimal 然后 使用reduce聚合函数,实现累加器

        /**
        BigDecimal bigDecimal =  orderItemEntities.stream().reduce(BigDecimal.ZERO,(x,y)->{
            return x.add(y.getSkuPrice().multiply(new BigDecimal(y.getSkuQuantity().toString())))
        },BigDecimal::add)
        */

        // 订单总金额 叠加每一个订单项的总额信息
        BigDecimal total = orderItemEntities.stream().map(OrderItemEntity::getRealAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        orderEntity.setTotalAmount(total);
        // 应付总额 订单总额 + 运费
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        // 运费金额 (已经赋值过)
        //orderEntity.setFreightAmount()
        // 优惠券的总金额
        BigDecimal couponTotal = orderItemEntities.stream().map(OrderItemEntity::getCouponAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 积分优惠的总金额
        BigDecimal integrationTotal = orderItemEntities.stream().map(OrderItemEntity::getIntegrationAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 打折优惠的总金额
        BigDecimal promotionTotal = orderItemEntities.stream().map(OrderItemEntity::getPromotionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        orderEntity.setCouponAmount(couponTotal);
        orderEntity.setPromotionAmount(promotionTotal);
        orderEntity.setIntegrationAmount(integrationTotal);

        // 获得的 积分 和 成长值

        //int sum = orderItemEntities.stream().mapToInt(OrderItemEntity::getGiftIntegration).sum()


        // 总积分
        Integer giftIntegration = orderItemEntities.stream().map(OrderItemEntity::getGiftIntegration).reduce(Integer::sum).orElse(0);
        // 总成长值
        Integer gftGrowth = orderItemEntities.stream().map(OrderItemEntity::getGiftGrowth).reduce(Integer::sum).orElse(0);

        orderEntity.setIntegration(giftIntegration);
        orderEntity.setGrowth(gftGrowth);

        // 未删除
        orderEntity.setDeleteStatus(0);

    }

    /**
     * 构建订单
     * @description:
     * @author: Administrator
     * @date: 2021-03-11 21:46:23
     
     * @return: com.zmm.mall.order.entity.OrderEntity
     **/
    private OrderEntity buildOrder(Long userId) {
        OrderEntity orderEntity = new OrderEntity();
        // 1.生成一个订单号
        String orderSn = IdWorker.getTimeId();
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberId(userId);
        // 2.获取收货地址信息
        OrderSubmitVo orderSubmitVo = submitVoThreadLocal.get();
        R result = wareFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareVo = result.getData(new TypeReference<FareVo>() {
        });
        // 设置与运费信息
        orderEntity.setFreightAmount(fareVo.getFare());
        // 设置收货人信息
        orderEntity.setReceiverCity(fareVo.getAddress().getCity());
        orderEntity.setReceiverDetailAddress(fareVo.getAddress().getDetailAddress());
        orderEntity.setReceiverName(fareVo.getAddress().getName());
        orderEntity.setReceiverPhone(fareVo.getAddress().getPhone());
        orderEntity.setReceiverPostCode(fareVo.getAddress().getPostCode());
        orderEntity.setReceiverProvince(fareVo.getAddress().getProvince());
        orderEntity.setReceiverRegion(fareVo.getAddress().getRegion());

        // 设置订单的相关状态
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        // 自动设置确认收货时间 7天 默认
        orderEntity.setAutoConfirmDay(7);


        return orderEntity;
    }

    /**
     * 构建所有订单项数据
     * @description:
     * @author: Administrator
     * @date: 2021-03-11 21:44:17
     * @param orderSn: 订单号
     * @return: com.zmm.mall.order.entity.OrderItemEntity
     **/
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        // 最终确认每个购物项的价格
        List<OrderItemVo> orderItemVoList = cartFeignService.getCartItemByUser();
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderItemVoList)){

            orderItemEntityList = orderItemVoList.stream().map(cartItem -> {
                OrderItemEntity orderItemEntity = buildOrderItem(cartItem);
                orderItemEntity.setOrderSn(orderSn);

                return orderItemEntity;
            }).collect(Collectors.toList());
        }
        return orderItemEntityList;
    }

    /**
     * 构建某一个订单项数据
     * @description:
     * @author: Administrator
     * @date: 2021-03-11 21:48:16
     * @param cartItem: 
     * @return: com.zmm.mall.order.entity.OrderItemEntity
     **/
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        // 1.订单信息 : 订单号 √
        Long skuId = cartItem.getSkuId();
        // 2.商品的 SPU 信息
        R result = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo spuInfoVo = result.getData(new TypeReference<SpuInfoVo>() {
        });
        orderItemEntity.setSpuId(spuInfoVo.getId());
        orderItemEntity.setSpuBrand(spuInfoVo.getBrandId().toString());
        orderItemEntity.setSpuName(spuInfoVo.getSpuName());
        //orderItemEntity.setSpuPic()
        orderItemEntity.setCategoryId(spuInfoVo.getCatalogId());

        // 3.商品的 SKU 信息 √
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(cartItem.getTitle());
        orderItemEntity.setSkuPic(cartItem.getImage());
        orderItemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttr);
        orderItemEntity.setSkuQuantity(cartItem.getCount());

        // 4.优惠信息[不做]


        // 5.积分信息 √
        orderItemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        orderItemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        //orderItemEntity.setGiftGrowth(cartItem.getPrice().intValue())
        //orderItemEntity.setGiftIntegration(cartItem.getPrice().intValue())

        // 6.订单项的价格
        orderItemEntity.setIntegrationAmount(new BigDecimal("0.0"));
        orderItemEntity.setPromotionAmount(new BigDecimal("0.0"));
        orderItemEntity.setCouponAmount(new BigDecimal("0.0"));
        orderItemEntity.setRealAmount(new BigDecimal("0.0"));
        // 商品的总金额
        BigDecimal origin = cartItem.getPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        // 当前订单项的实际金额 = 总额 - 各种优惠
        BigDecimal subtract = origin.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(subtract);
        return orderItemEntity;
    }
}
