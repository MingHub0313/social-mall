package com.zmm.mall.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 封装订单提交的数据
 * @Description:
 * @Name OrderSubmitVo
 * @Author Administrator
 * @Date By 2021-03-11 20:40:52
 */
@Data
public class OrderSubmitVo implements Serializable {

    /**
     * 收货地址的id
     */
    private Long addrId;

    /**
     * 支付方式
     */
    private Integer payType ;

    // 无需提交需要购买的商品 后台自己从购物车获取
    // 优惠 发票

    /**
     * 防重令牌
     */
    private String orderToken;

    /**
     * 应付价格 (验价)
     */
    private BigDecimal payPrice;


    // 用户相关信息 直接去 session 中获取 登陆的用户

    /**
     * 订单备注
     */
    private String note;
}
