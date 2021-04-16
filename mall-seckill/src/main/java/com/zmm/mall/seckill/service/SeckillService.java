package com.zmm.mall.seckill.service;

/**
 * @author 900045
 * @description:
 * @name SeckillService
 * @date By 2021-04-16 15:56:06
 */
public interface SeckillService {
	/**
	 * 上架最近3天参与秒杀的活动商品
	 * @author: 900045
	 * @date: 2021-04-16 15:57:00
	 * @throws 
	 * @return: void
	 **/
	void uploadSeckillSkuLatest3Days();
	
}
