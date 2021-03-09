package com.zmm.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.order.entity.OrderEntity;
import com.zmm.mall.order.vo.OrderConfirmVo;

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
}

