package com.zmm.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Name SpuBoundTo
 * @Author 900045
 * @Created by 2020/9/3 0003
 */
@Data
public class SpuBoundTo {

	private Long spuId;
	private BigDecimal buyBounds;
	private BigDecimal growBounds;

}
