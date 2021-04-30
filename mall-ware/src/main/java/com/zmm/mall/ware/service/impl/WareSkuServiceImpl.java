package com.zmm.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.enums.OrderStatusEnum;
import com.zmm.common.exception.CustomRunTimeException;
import com.zmm.common.exception.NoStockException;
import com.zmm.common.to.mq.OrderTo;
import com.zmm.common.to.mq.StockDetailTo;
import com.zmm.common.to.mq.StockLockedTo;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.common.utils.R;
import com.zmm.mall.ware.dao.WareSkuDao;
import com.zmm.mall.ware.entity.WareOrderTaskDetailEntity;
import com.zmm.mall.ware.entity.WareOrderTaskEntity;
import com.zmm.mall.ware.entity.WareSkuEntity;
import com.zmm.mall.ware.feign.OrderFeignService;
import com.zmm.mall.ware.feign.ProductFeignService;
import com.zmm.mall.ware.service.WareOrderTaskDetailService;
import com.zmm.mall.ware.service.WareOrderTaskService;
import com.zmm.mall.ware.service.WareSkuService;
import com.zmm.mall.ware.vo.OrderItemVo;
import com.zmm.mall.ware.vo.OrderVo;
import com.zmm.mall.ware.vo.SkuHasStockVo;
import com.zmm.mall.ware.vo.SkuWareHasStock;
import com.zmm.mall.ware.vo.WareSkuLockVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品库存
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 15:20:08
 */
@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    private WareSkuDao wareSkuDao;
    
    @Resource
    private ProductFeignService productFeignService;
    
    @Resource
    private OrderFeignService orderFeignService;
    
    @Resource
    private WareOrderTaskService wareOrderTaskService;
    
    @Resource
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    
    @Resource
    private RabbitTemplate rabbitTemplate;


    @Override
    public void unLockStock(StockLockedTo to) {
        // 方法只要一切正常运行 就是库存解锁成功
        log.error("收集到解锁库存的消息");
        //库存工作单的id
        Long taskId = to.getTaskId();
        StockDetailTo stockDetail = to.getStockDetail();
        Long detailId = stockDetail.getId();
        // 解锁
        // 1.查询数据库关于这个订单的锁定库存信息
        // 有: 证明 库存时锁定成了 但是 到底要不要解锁 --> 还得查询订单详情
        //      1.没有这个订单 --> 必须解锁
        //      2.有这个订单 --> 不是解锁库存  --> 还得查询订单状态
        //              订单状态:
        //                          1.已取消:解锁库存
        //                          2.没取消:不能解锁
        //              
        // 没有:库存锁定失败了 库存回滚了. 这种情况无需解锁
        WareOrderTaskDetailEntity orderTaskDetailEntity = wareOrderTaskDetailService.getById(detailId);
        if (ObjectUtils.isEmpty(orderTaskDetailEntity)){
            // 无需解锁 
            //channel.basicAck(message.getMessageProperties().getDeliveryTag(),false)
        } else {
            // 解锁
            WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getById(taskId);
            // 根据订单号查询订单的状态
            String orderSn = orderTaskEntity.getOrderSn();
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0){
                OrderVo orderVo = r.getData(new TypeReference<OrderVo>(){});
                if (ObjectUtils.isEmpty(orderVo) || orderVo.getStatus().equals(OrderStatusEnum.CANCELED.getCode())){
                    // 订单不存在 才可以解锁库存
                    // 订单已经被取消了 ,才可以解锁库存
                    if (orderTaskDetailEntity.getLockStatus() == 1) {
                        // 当前库存工作单详情 状态为 1(未解锁) 才可以解锁
                        unLockStock(stockDetail.getSkuId(), stockDetail.getTaskId(), stockDetail.getSkuNum(), detailId);
                    }
                    // channel.basicAck(message.getMessageProperties().getDeliveryTag(),false)
                }
            } else {
                // 消息拒绝以后重新放到队列里面,让别继续消费
                // channel.basicReject(message.getMessageProperties().getDeliveryTag(),true)
                throw new CustomRunTimeException(ResultCode.REMOTE_SERVICE_FAIL);
            }
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unLockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        // 1.查询当前订单的最新状态
        // R r = orderFeignService.getOrderStatus(orderSn)
        // 2.解锁库存 ==> 查询最新库存的状态 防止重复解锁库存
        WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        Long taskId = wareOrderTaskEntity.getId();
        // 3.再根据 库存工作单的 id ==> 查询未解锁过的(即状态为 1)的记录 进行解锁
        List<WareOrderTaskDetailEntity> detailEntityList = wareOrderTaskDetailService.list(
                new QueryWrapper<WareOrderTaskDetailEntity>()
                        .eq("task_id", taskId).eq("lock_status", 1));

        //Long skuId,Long wareId,Integer num,Long taskDetailId
        for (WareOrderTaskDetailEntity taskDetailEntity : detailEntityList){
            unLockStock(taskDetailEntity.getSkuId(),taskDetailEntity.getWareId(),taskDetailEntity.getSkuNum(),taskDetailEntity.getId());
        }
    }

    /**
     * 解锁库存的方法
     * @author: 900045
     * @date: 2021-03-24 13:54:41
     * @throws 
     * @param skuId: 
     * @param wareId: 
     * @param num: 
     * @param taskDetailId: 
     * @return: void
     **/
    private void unLockStock(Long skuId,Long wareId,Integer num,Long taskDetailId){
        // 1.库存解锁 wms_ware_sku 表 减回原来的数量 
        wareSkuDao.unLockStock(skuId,wareId,num);
        // 2.更新库存工作单的状态
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
        wareOrderTaskDetailEntity.setId(taskDetailId);
        // 状态变为已解锁
        wareOrderTaskDetailEntity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);
    }
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId: 1
         * wareId: 2
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }

        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> skuHasStockVoList = skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            //查询当前 sku总 库存量
            Long count = baseMapper.getSkuStock(skuId);
            skuHasStockVo.setSkuId(skuId);
            skuHasStockVo.setHasStock(count == null?false:count>0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
        return skuHasStockVoList;
    }


    /**
     * (rollbackFor = NoStockException.class)
     * 默认只要是运行时异常都会回滚
     * 
     * 
     * 库存解锁的场景:
     *  1).下订单成功,订单过期没有支付被系统自动取消、被用户手动取消 ===>都要解锁库存
     *  2).下订单成功,库存锁定成功,其它的业务调用失败,导致订单回滚.之前锁定的库存也要解锁 (不使用seata(分布式事务太慢了))    
     * @param vo:
     * @return
     */
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public boolean orderLockStock(WareSkuLockVo vo) {

        /**
         * 保存库存工作单的详情
         * 追溯
         */
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderId(vo.getId());
        wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(wareOrderTaskEntity);
        // 1.按照下单的收货地址, 找到一个就近仓库, 锁定库存
        // 1.1找到每个商品在哪个库存有
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> skuWareHasStocks = locks.stream().map(item -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            // 查询指定的商品的仓库地址Id
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            skuWareHasStock.setSkuId(skuId);
            skuWareHasStock.setNum(item.getCount());
            skuWareHasStock.setWareId(wareIds);
            return skuWareHasStock;
        }).collect(Collectors.toList());

        // 2.锁定库存
        for (SkuWareHasStock skuWareHasStock:skuWareHasStocks) {
            boolean skuStocked = false;
            Long skuId = skuWareHasStock.getSkuId();
            List<Long> wareIds = skuWareHasStock.getWareId();
            if (CollectionUtils.isEmpty(wareIds)){
                // 没有任何仓库有这个商品
                throw new NoStockException(skuId);
            }

            // 1.如果每一个商品都锁定成功,将当前商品锁定了几件的工作单记录发送给 MQ
            // 2.锁定失败,前面保存的工作单信息就回滚了 (但是消息已经发送出去了) -- 即使要解锁记录,由于去数据库查不到 id ,所以就不用解锁
            // 3.不合理 
            for (Long wareId:wareIds) {
                // 几行受影响
                Integer count = wareSkuDao.lockSkuStock(skuId,wareId,skuWareHasStock.getNum());
                if (count > 0 ){
                    skuStocked = true;
                    // 这里跳出相当于 当前的 skuId 锁成功了 ,开始锁定下一个 skuId
                    // 锁定成功 发消息给 mq 
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null,skuId,"",skuWareHasStock.getNum(),
                            wareOrderTaskEntity.getId(),wareId,
                            1);
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setTaskId(wareOrderTaskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity,stockDetailTo);
                    // 只记录 id 不行,防止前面的数据回滚以后找不到数据
                    //    商品号 - 数量 - 库存
                    // 1: 1 - 2 - 1 2: 2 - 1 - 2  3: 3 - 1 - 1 (失败)
                    stockLockedTo.setStockDetail(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",stockLockedTo);
                    break;
                } else {
                    // 当前仓库锁失败 重试下一个仓库
                }
            }
            if (skuStocked == false) {
                // 当前商品所有库存都没有锁成功
                throw new NoStockException(skuId);
            }

        }

        // 没有执行异常就相当于锁定成功
        return true;
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判断如果还没有这个库存记录新增
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(entities == null || entities.size() == 0){
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字，如果失败，整个事务无需回滚
            //1、自己catch异常
            //TODO 还可以用什么办法让异常出现以后不回滚？高级
            try {
                R info = productFeignService.info(skuId);
                Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");

                if(info.getCode() == 0){
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){

            }


            wareSkuDao.insert(skuEntity);
        }else{
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }

    }
}
