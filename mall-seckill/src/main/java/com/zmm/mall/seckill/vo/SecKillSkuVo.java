package com.zmm.mall.seckill.vo;

import lombok.Data;

/**
 * @author 900045
 * @description:
 * @name SecKillSkuVo
 * @date By 2021-04-16 16:27:04
 */
@Data
public class SecKillSkuVo {

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
	private Integer secKillPrice;
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

}
