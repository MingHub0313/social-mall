package com.zmm.mall.seckill.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 900045
 * @description:
 * @name SkuInfoVo
 * @date By 2021-04-16 16:50:55
 */
@Data
public class SkuInfoVo {

	private Long skuId;
	/**
	 * $column.comments
	 */
	private Long spuId;
	/**
	 * $column.comments
	 */
	private String skuName;
	/**
	 * $column.comments
	 */
	private String skuDesc;
	/**
	 * $column.comments
	 */
	private Long catalogId;
	/**
	 * $column.comments
	 */
	private Long brandId;
	/**
	 * $column.comments
	 */
	private String skuDefaultImg;
	/**
	 * $column.comments
	 */
	private String skuTitle;
	/**
	 * $column.comments
	 */
	private String skuSubtitle;
	/**
	 * $column.comments
	 */
	private BigDecimal price;
	/**
	 * $column.comments
	 */
	private Long saleCount;
}
