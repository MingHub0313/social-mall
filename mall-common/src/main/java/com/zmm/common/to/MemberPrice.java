package com.zmm.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Name MemberPrice
 * @Author 900045
 * @Created by 2020/9/3 0003
 */
@Data
public class MemberPrice {

	private Long id;
	private String name;
	private BigDecimal price;

}
