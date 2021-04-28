package com.zmm.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Name FareVo
 * @Author Administrator
 * @Date By 2021-03-11 21:32:43
 */
@Data
public class FareVo{

    /**
     * 收取地址
     */
    private MemberAddressVo memberAddressVo;

    /**
     * 运费价格
     */
    private BigDecimal fare;
}
