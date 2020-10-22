package com.zmm.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Name MergeVo
 * @Author 900045
 * @Created by 2020/9/4 0004
 */
@Data
public class MergeVo {

	/**
	 * 整单id
	 */
	private Long purchaseId;
	/**
	 * [1,2,3,4] //合并项集合
	 */
	private List<Long> items;
}
