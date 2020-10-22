package com.zmm.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Name Catalog2Vo
 * @Author 900045
 * @Created by 2020/9/27 0027
 * 二级分类VO
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catalog2Vo {

	/**
	 * 一级父分类ID
	 */
	private String catalog1Id;
	/**
	 * 三级子分类
	 */
	private List<Cat3log3Vo> Catalog3List;
	private String id;
	private String name;

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class Cat3log3Vo{

		private String catalog2Id;

		private String id;

		private String name;
	}

}
