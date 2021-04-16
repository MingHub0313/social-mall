package com.zmm.mall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zmm.common.constant.NumberConstant;
import com.zmm.common.utils.R;
import com.zmm.common.utils.redis.RedisUtil;
import com.zmm.common.utils.redis.key.KillKey;
import com.zmm.common.utils.redis.key.RedisKey;
import com.zmm.mall.seckill.feign.CouponFeignService;
import com.zmm.mall.seckill.feign.ProductFeignService;
import com.zmm.mall.seckill.service.SeckillService;
import com.zmm.mall.seckill.to.SeckillSkuRedisTo;
import com.zmm.mall.seckill.vo.SecKillSkuVo;
import com.zmm.mall.seckill.vo.SeckillSessionWithSku;
import com.zmm.mall.seckill.vo.SkuInfoVo;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author 900045
 * @description:
 * @name SeckillServiceImpl
 * @date By 2021-04-16 15:56:20
 */
@Service
public class SeckillServiceImpl implements SeckillService {
	
	@Resource
	private CouponFeignService couponFeignService;
	
	@Resource
	private ProductFeignService productFeignService;
	
	@Resource
	private RedisUtil redisUtil;
	
	@Resource
	private RedissonClient redisSon;

	@Override
	public void uploadSeckillSkuLatest3Days() {
		// 1.扫描最近三天需要参与秒杀的活动
		R latest3DaySession = couponFeignService.getLatest3DaySession();
		if (latest3DaySession.getCode() == 0) {
			List<SeckillSessionWithSku> dataList = latest3DaySession.getData(new TypeReference<List<SeckillSessionWithSku>>() {
			});
			// 将查询到的数据 缓存到 redis 中
			// 1.缓存活动信息
			saveSessionInfos(dataList);
			// 2.缓存活动关联的商品信息
			saveSessionSkuInfos(dataList);
		}
	}
	
	/**
	 * 缓存活动信息
	 * @author: 900045
	 * @date: 2021-04-16 16:32:18
	 * @throws 
	 * @param sessions: 
	 * @return: void
	 **/
	private void saveSessionInfos(List<SeckillSessionWithSku> sessions){
		sessions.stream().forEach(session->{
			Long startTime = session.getStartTime().getTime();
			Long endTime = session.getEndTime().getTime();
			RedisKey redisKey = KillKey.KILL.setSuffix(startTime).setSuffix("_").setSuffix(endTime);
			boolean exists = redisUtil.exists(redisKey);
			if (!exists){
				// 1-2 场次-商品skuId
				List<String> collect =
						session.getRelationSkuList().stream().map( item->item.getPromotionId()+"_"+item.getSkuId()).collect(Collectors.toList());
				redisUtil.leftPushAll(redisKey,collect);
			}
		});
	}
	
	/**
	 * 缓存活动关联的商品信息
	 * @author: 900045
	 * @date: 2021-04-16 16:32:24
	 * @throws 
	 * @param sessions: 
	 * @return: void
	 **/
	private void saveSessionSkuInfos(List<SeckillSessionWithSku> sessions){
		RedisKey redisKey = KillKey.SECKILL_SKU_LIST;
		sessions.stream().forEach(session->{
			session.getRelationSkuList().stream().forEach(secKillSkuVo -> {
				
				String token = UUID.randomUUID().toString().replace("-", "");
				String filed = secKillSkuVo.getPromotionId() + "_" + secKillSkuVo.getSkuId();
				boolean b = redisUtil.hashExists(redisKey,filed);
				if (!b){
					SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();

					// 1.sku基本信息
					R r = productFeignService.getSkuInfo(secKillSkuVo.getSkuId());
					if (r.getCode() == 0){
						SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
						});
						redisTo.setSkuInfoVo(skuInfo);
					}

					// 2.sku的秒杀信息
					BeanUtils.copyProperties(secKillSkuVo,redisTo);

					// 3.设置 开始时间和结束时间
					redisTo.setStartTime(session.getStartTime().getTime());
					redisTo.setEndTime(session.getEndTime().getTime());

					// 4.随机码? seckill?skuId =1 接口暴露 但是拼接一个随机码 别人就不知道了
					redisTo.setRandomCode(token);

					// 1-2 场次-商品skuId
					redisUtil.hash(redisKey,filed,JSON.toJSONString(redisTo));

					KillKey seckillStock = KillKey.SECKILL_STOCK;
					// 5.使用库存作为引入分布式信号量 (其作用限流)
					// 如果当前场次的商品库存信息 已经上架就不需要上架
					RSemaphore semaphore = redisSon.getSemaphore(seckillStock.setSuffix(token).getKey());
					// 商品的秒杀数量作为 信号量 进入该方法就 减一
					semaphore.trySetPermits(secKillSkuVo.getSecKillCount());
				}
			});
		});
	}
}
