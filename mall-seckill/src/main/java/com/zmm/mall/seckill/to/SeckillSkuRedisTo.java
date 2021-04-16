package com.zmm.mall.seckill.to;

import com.zmm.mall.seckill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 900045
 * @description:
 * @name SeckillSkuRedisTo
 * @date By 2021-04-16 16:48:57
 */
@Data
public class SeckillSkuRedisTo {

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
	private BigDecimal secKillCount;
	/**
	 * $column.comments
	 */
	private BigDecimal secKillLimit;
	/**
	 * $column.comments
	 */
	private Integer secKillSort;
	
	//sku 详情信息
	
	private SkuInfoVo skuInfoVo;

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
