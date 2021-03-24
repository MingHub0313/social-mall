package com.zmm.common.to.mq;

import lombok.Data;

/**
 * @author 900045
 * @description:
 * @name StockLockedTo
 * @date By 2021-03-24 11:26:46
 */
@Data
public class StockLockedTo {

	/**
	 * 库存工作单的id
	 */
	private Long taskId;

	/**
	 * 工作单详情
	 */
	private StockDetailTo stockDetail;
}
