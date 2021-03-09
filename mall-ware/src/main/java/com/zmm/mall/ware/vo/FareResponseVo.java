package com.zmm.mall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 运费返回信息
 * @Description:
 * @Name FareResponseVo
 * @Author Administrator
 * @Date By 2021-03-09 21:56:50
 */
@Data
public class FareResponseVo {

    private MemberAddressVo memberAddressVo;

    private BigDecimal fare;
}
