package com.zmm.mall.ware.vo;

import lombok.Data;

/**
 * @Name PurchaseItemDoneVo
 * @Author 900045
 * @Created by 2020/9/4 0004
 */
@Data
public class PurchaseItemDoneVo {

	/**
	 * {itemId:1,status:4,reason:""}
	 */
	private Long itemId;
	private Integer status;
	private String reason;
}
