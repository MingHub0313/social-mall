package com.zmm.mall.order.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
     * 总件数 如果有 getCount 就相当于有 count这个属性
     */
    private Integer count;

    /**
     * 应付金额
     */
    BigDecimal payPrice;

    /**
     * long --> skuId
     * boolean --> 是否有库存
     */
    Map<Long,Boolean> stocks;

    /**
     * 订单防重复提交
     */
    private String orderToken;

    public Integer getCount(){
        Integer i = 0;
        if (!CollectionUtils.isEmpty(orderItemVoList)) {
            for (OrderItemVo orderItemVo:orderItemVoList) {
                i += orderItemVo.getCount();
            }
        }
        return i;
    }


    /**
     * 总金额
     * @description:
     * @author: Administrator
     * @date: 2021-03-09 21:08:02
     * @return: java.math.BigDecimal
     **/
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

    /**
     * 应付金额
     * @description:
     * @author: Administrator
     * @date: 2021-03-09 21:07:48
     * @return: java.math.BigDecimal
     **/
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
