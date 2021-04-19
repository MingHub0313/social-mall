package com.zmm.mall.seckill.service;

import com.zmm.mall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

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

	/**
	 * 当前时间可以参与秒杀商品信息
	 * @author: 900045
	 * @date: 2021-04-19 15:32:02
	 * @throws 
	 * @return: java.util.List<com.zmm.mall.seckill.to.SeckillSkuRedisTo>
	 **/
	List<SeckillSkuRedisTo> getCurrentSeckillSkuList();

	/**
	 * 根据 skuId 获取秒杀商品的详情
	 * @author: 900045
	 * @date: 2021-04-19 16:03:35
	 * @throws 
	 * @param skuId: 
	 * @return: com.zmm.mall.seckill.to.SeckillSkuRedisTo
	 **/
	SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

	/**
	 * 进行秒杀
	 * @author: 900045
	 * @date: 2021-04-19 17:11:27
	 * @throws 
	 * @param killId: 
	 * @param key: 
	 * @param num: 
	 * @return: java.lang.String
	 **/
	String kill(String killId, String key, Integer num);
}
