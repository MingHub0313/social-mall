package com.zmm.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 900045
 * @description:
 * @name SeckillOrderTo
 * @date By 2021-04-19 17:41:44
 */
@Data
public class SeckillOrderTo {

	/**
	 * 订单号
	 */
	private String orderSn;
	
	/**
	 * 秒杀场次ID
	 */
	private Long promotionSessionId;
	/**
	 * 商品ID
	 */
	private Long skuId;
	/**
	 * 秒杀价格
	 */
	private BigDecimal secKillPrice;
	/**
	 * 购物数量
	 */
	private Integer num;

	/**
	 * 会员Id
	 */
	private Long memberId;
}
