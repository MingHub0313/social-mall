package com.zmm.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Name SkuReductionTo
 * @Author 900045
 * @Created by 2020/9/3 0003
 */
@Data
public class SkuReductionTo {

	private Long skuId;
	private int fullCount;
	private BigDecimal discount;
	private int countStatus;
	private BigDecimal fullPrice;
	private BigDecimal reducePrice;
	private int priceStatus;
	private List<MemberPrice> memberPrice;
}
