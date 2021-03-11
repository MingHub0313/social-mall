package com.zmm.mall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.enums.OrderStatusEnum;
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
import com.zmm.mall.order.feign.CartFeignService;
import com.zmm.mall.order.feign.MemberFeignService;
import com.zmm.mall.order.feign.ProductFeignService;
import com.zmm.mall.order.feign.WareFeignService;
import com.zmm.mall.order.interceptor.LoginUserInterceptor;
import com.zmm.mall.order.service.OrderService;
import com.zmm.mall.order.to.OrderCreatTo;
import com.zmm.mall.order.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> submitVoThreadLocal = new ThreadLocal<>();

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


    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        submitVoThreadLocal.set(orderSubmitVo);
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        OrderKey orderKey = OrderKey.USER_ORDER_TOKEN;

        // 1.验令牌 【令牌的对比 和 删除 必须保证原子性】 ==> lua 脚本
        String orderToken = orderSubmitVo.getOrderToken();
        RedisKey redisKey = orderKey.setSuffix(memberRespVo.getId());

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

        // Lua 脚本 返回 0 或者 1  0: 令牌验证失败 1: 删除成功(都能删除删除比存在)
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        Long result = redisUtil.execute(script,redisKey,orderToken);
        if (result == 0){
            // 令牌验证失败
            response.setCode(0);
            return response;
        }

        // 2.创建订单
        OrderCreatTo orderCreatTo = createOrder();

        // 3.验价格
        BigDecimal payAmount = orderCreatTo.getOrderEntity().getPayAmount();
        BigDecimal payPrice = orderSubmitVo.getPayPrice();
        // 差价小于0.01
        if ( Math.abs(payAmount.subtract(payPrice).doubleValue()) >= 0.01 ){
            // 金额对比失败
            response.setCode(2);
            return response;
        }


        // 4.锁库存 ...
        return null;
    }




    /**
     * 创建订单
     * @description:
     * @author: Administrator
     * @date: 2021-03-11 22:11:02
     * @return: com.zmm.mall.order.to.OrderCreatTo
     **/
    private OrderCreatTo createOrder(){
        OrderCreatTo orderCreatTo = new OrderCreatTo();

        OrderEntity orderEntity = buildOrder();
        orderCreatTo.setOrderEntity(orderEntity);

        // 2.获取到所有的的订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderEntity.getOrderSn());
        orderCreatTo.setOrderItemEntities(orderItemEntities);
        // 3.计算价格、积分相关信息
        computePrice(orderEntity,orderItemEntities);
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
            return x.add(y.getSkuPrice().multiply(new BigDecimal(y.getSkuQuantity().toString())));
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
    private OrderEntity buildOrder() {
        OrderEntity orderEntity = new OrderEntity();
        // 1.生成一个订单号
        String orderSn = IdWorker.getTimeId();
        orderEntity.setOrderSn(orderSn);
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
        //orderItemEntity.setGiftGrowth(cartItem.getPrice().intValue());
        //orderItemEntity.setGiftIntegration(cartItem.getPrice().intValue());

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