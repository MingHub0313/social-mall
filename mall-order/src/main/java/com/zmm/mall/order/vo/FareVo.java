package com.zmm.mall.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description:
 * @Name FareVo
 * @Author Administrator
 * @Date By 2021-03-11 21:32:43
 */
@Data
public class FareVo implements Serializable {

    /**
     * 收取地址
     */
    private MemberAddressVo address;

    /**
     * 运费价格
     */
    private BigDecimal fare;
}
