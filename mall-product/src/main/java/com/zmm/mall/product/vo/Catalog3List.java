package com.zmm.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Name Catalog3List
 * @Author 900045
 * @Created by 2020/9/27 0027
 * 三级分类VO
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catalog3List {

	private String catalog2Id;
	private String id;
	private String name;
}
