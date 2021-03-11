package com.zmm.mall.order.to;

import com.zmm.mall.order.entity.OrderEntity;
import com.zmm.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Name OrderCreatTo
 * @Author Administrator
 * @Date By 2021-03-11 21:17:21
 */
@Data
public class OrderCreatTo implements Serializable {

    /**
     * 订单对象
     */
    private OrderEntity orderEntity;

    /**
     * 购物项明细
     */
    private List<OrderItemEntity> orderItemEntities;

    /**
     * 应付价格
     */
    private BigDecimal payPrice;


    /**
     * 运费
     */
    private BigDecimal fare;
}
