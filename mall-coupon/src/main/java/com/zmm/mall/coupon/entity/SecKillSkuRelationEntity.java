package com.zmm.mall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 秒杀活动商品关联
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
@Data
@TableName("sms_seckill_sku_relation")
public class SecKillSkuRelationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * $column.comments
	 */
	@TableId
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

}
