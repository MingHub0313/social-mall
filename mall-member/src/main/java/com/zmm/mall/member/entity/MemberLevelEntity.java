package com.zmm.mall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;

import lombok.Data;

/**
 * 会员等级
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
@Data
@TableName("ums_member_level")
public class MemberLevelEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * $column.comments
	 */
	@TableId
	private Long id;
	/**
	 * $column.comments
	 */
	private String name;
	/**
	 * $column.comments
	 */
	private Integer growthPoint;
	/**
	 * $column.comments
	 */
	private Integer defaultStatus;
	/**
	 * $column.comments
	 */
	private BigDecimal freeFreightPoint;
	/**
	 * $column.comments
	 */
	private Integer commentGrowthPoint;
	/**
	 * $column.comments
	 */
	private Integer privilegeFreeFreight;
	/**
	 * $column.comments
	 */
	private Integer privilegeMemberPrice;
	/**
	 * $column.comments
	 */
	private Integer privilegeBirthday;

	/**
	 * $column.comments
	 */
	private String note;

}
