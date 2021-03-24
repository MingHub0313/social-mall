package com.zmm.common.to.mq;

import lombok.Data;

/**
 * @author 900045
 * @description:
 * @name StockDetailTo
 * @date By 2021-03-24 11:34:27
 */
@Data
public class StockDetailTo {

	private Long id;
	/**
	 * $column.comments
	 */
	private Long skuId;
	/**
	 * $column.comments
	 */
	private String skuName;
	/**
	 * $column.comments
	 */
	private Integer skuNum;
	/**
	 * $column.comments
	 */
	private Long taskId;
	/**
	 * $column.comments
	 */
	private Long wareId;
	/**
	 * $column.comments
	 */
	private Integer lockStatus;
}
