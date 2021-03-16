package com.zmm.mall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * 库存锁定vo
 * @Description:
 * @Name WareSkuLockVo
 * @Author Administrator
 * @Date By 2021-03-16 20:01:49
 */
@Data
public class WareSkuLockVo {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 需要锁住的所有库存信息
     */
    private List<OrderItemVo> locks;
}
