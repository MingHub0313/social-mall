package com.zmm.mall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 900045
 * @description:
 * @name SeckillInfoVo
 * @date By 2021-04-19 16:15:05
 */
@Data
public class SeckillInfoVo {

	private Long id;
	/**
	 * $column.comments
	 */
	private Long promotionId;
	/**
	 * $column.comments
	 */
	private Long promotionSessionId;
	/**
	 * $column.comments
	 */
	private Long skuId;
	/**
	 * $column.comments
	 */
	private BigDecimal secKillPrice;
	/**
	 * $column.comments
	 */
	private Integer secKillCount;
	/**
	 * $column.comments
	 */
	private Integer secKillLimit;
	/**
	 * $column.comments
	 */
	private Integer secKillSort;

	/**
	 * 当前商品秒杀的开始时间
	 */
	private Long startTime;

	/**
	 * 当前商品秒杀的结束时间
	 */
	private Long endTime;

	/**
	 * 商品秒杀随机码
	 */
	private String randomCode;
}
