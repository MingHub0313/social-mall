package com.zmm.mall.product.vo;

import com.zmm.common.to.MemberPrice;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Name Skus
 * @Author 900045
 * @Created by 2020/9/3 0003
 */
@Data
public class Skus {

	private List<Attr> attr;
	private String skuName;
	private BigDecimal price;
	private String skuTitle;
	private String skuSubtitle;
	private List<Images> images;
	private List<String> descar;
	private int fullCount;
	private BigDecimal discount;
	private int countStatus;
	private BigDecimal fullPrice;
	private BigDecimal reducePrice;
	private int priceStatus;
	private List<MemberPrice> memberPrice;
}
