package com.zmm.mall.order.vo;

import lombok.Data;

/**
 * @author 900045
 * @description:
 * @name PayVo
 * @date By 2021-04-15 14:48:36
 */
@Data
public class PayVo {

	/**
	 * 商户订单号 必填
	 */
	private String out_trade_no;
	/**
	 * 订单名称 必填
	 */
	private String subject;
	/**
	 * 付款金额 必填
	 */
	private String total_amount;
	/**
	 * 商品描述 可空
	 */
	private String body;
}
