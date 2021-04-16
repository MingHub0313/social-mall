package com.zmm.mall.seckill.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 900045
 * @description:
 * @name SeckillSessionWithSku
 * @date By 2021-04-16 16:25:56
 */
@Data
public class SeckillSessionWithSku {

	private Long id;
	/**
	 * $column.comments
	 */
	private String name;
	/**
	 * $column.comments
	 */
	private Date startTime;
	/**
	 * $column.comments
	 */
	private Date endTime;
	/**
	 * $column.comments
	 */
	private Integer status;
	/**
	 * $column.comments
	 */
	private Date createTime;

	@TableField(exist = false)
	private List<SecKillSkuVo> relationSkuList;
}
