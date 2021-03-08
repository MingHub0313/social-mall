package com.zmm.mall.order.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认需要的数据模型
 * @Description:
 * @Name OrderConfirmVo
 * @Author Administrator
 * @Date By 2021-03-08 21:58:37
 */
@Data
public class OrderConfirmVo {

    /**
     * 收货地址列表 ums_member_receive_address 表
     */
    List<MemberAddressVo> memberAddressVoList;

    /**
     * 所有选中的购物项
     */
    List<OrderItemVo> orderItemVoList;


    /**
     * 会员积分信息
     */
    private Integer integration;

    // 1.发票信息...(历史发票)

    // 2.优惠券信息

    /**
     * 订单总额
     */
    BigDecimal total;

    /**
     * 应付金额
     */
    BigDecimal payPrice;

    /**
     * 订单防重复提交
     */
    private String orderToken;


    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (!CollectionUtils.isEmpty(orderItemVoList)) {
            for (OrderItemVo orderItemVo:orderItemVoList) {
                BigDecimal multiply = orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount().toString()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

    public BigDecimal getPayPrice() {
        BigDecimal paySum = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(orderItemVoList)) {
            for (OrderItemVo orderItemVo:orderItemVoList) {
                BigDecimal multiply = orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount().toString()));
                paySum = paySum.add(multiply);
            }
        }
        return paySum;
    }
}
