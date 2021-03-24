package com.zmm.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.order.entity.OrderEntity;
import com.zmm.mall.order.vo.OrderConfirmVo;
import com.zmm.mall.order.vo.OrderSubmitVo;
import com.zmm.mall.order.vo.SubmitOrderResponseVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:50:05
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认页返回需要的数据
     * @description:
     * @author: Administrator
     * @date: 2021-03-08 22:10:54
     * @return: com.zmm.mall.order.vo.OrderConfirmVo
     **/
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 下单操作
     * @description:
     * @author: Administrator
     * @date: 2021-03-11 20:52:48
     * @param orderSubmitVo: 
     * @return: com.zmm.mall.order.vo.SubmitOrderResponseVo
     **/
    SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);

    /**
     * 根据订单号获取订单
     * @author: 900045
     * @date: 2021-03-24 13:45:27
     * @throws 
     * @param orderSn: 
     * @return: com.zmm.mall.order.entity.OrderEntity
     **/
	OrderEntity getOrderByOrderSn(String orderSn);

	/**
	 * 使用延迟队列 定时关闭订单
	 * @author: 900045
	 * @date: 2021-03-24 14:43:14
	 * @throws 
	 * @param orderEntity: 
	 * @return: void
	 **/
	void closeOrder(OrderEntity orderEntity);
}

