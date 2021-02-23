package com.zmm.mall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author 900045
 * @description:
 * @name SpuItemAttrGroupVo
 * @date By 2021-02-23 16:42:17
 */
@ToString
@Data
public class SpuItemAttrGroupVo {
	private String groupName;

	private List<Attr> attrs;
}
