package com.zmm.mall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Name PurchaseDoneVo
 * @Author 900045
 * @Created by 2020/9/4 0004
 */
@Data
public class PurchaseDoneVo {

	/**
	 * 采购单id
	 */
	@NotNull
	private Long id;

	private List<PurchaseItemDoneVo> items;
}
