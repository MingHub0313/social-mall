package com.zmm.mall.product.vo;

import lombok.Data;

/**
 * @Name AttrRespVo
 * @Author 900045
 * @Created by 2020/9/1 0001
 */
@Data
public class AttrRespVo extends AttrVo{

	/**
	 * 			"catelogName": "手机/数码/手机", //所属分类名字
	 * 			"groupName": "主体", //所属分组名字
	 */
	private String catelogName;
	private String groupName;

	private Long[] catelogPath;
}
