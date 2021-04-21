package com.zmm.mall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zmm.common.to.mq.SeckillOrderTo;
import com.zmm.common.utils.R;
import com.zmm.common.utils.redis.RedisUtil;
import com.zmm.common.utils.redis.key.KillKey;
import com.zmm.common.utils.redis.key.RedisKey;
import com.zmm.common.vo.MemberRespVo;
import com.zmm.mall.seckill.feign.CouponFeignService;
import com.zmm.mall.seckill.feign.ProductFeignService;
import com.zmm.mall.seckill.interceptor.LoginUserInterceptor;
import com.zmm.mall.seckill.service.SeckillService;
import com.zmm.mall.seckill.to.SeckillSkuRedisTo;
import com.zmm.mall.seckill.vo.SeckillSessionWithSku;
import com.zmm.mall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 900045
 * @description:
 * @name SeckillServiceImpl
 * @date By 2021-04-16 15:56:20
 */
@Slf4j
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

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Override
	public void uploadSeckillSkuLatest3Days() {
		// 1.扫描最近三天需要参与秒杀的活动
		R latest3DaySession = couponFeignService.getLatest3DaySession();
		/**
		 * 改进
		 * 		1.上架秒杀商品 每个数据都有过期时间
		 * 		2.秒杀后续的流程,简化收货地址信息	
		 */
		if (latest3DaySession.getCode() == 0) {
			List<SeckillSessionWithSku> dataList = latest3DaySession.getData(new TypeReference<List<SeckillSessionWithSku>>() {
			});
			// 将查询到的数据 缓存到 redis 中
			if (!CollectionUtils.isEmpty(dataList)) {
				// 1.缓存活动信息
				saveSessionInfos(dataList);
				// 2.缓存活动关联的商品信息
				saveSessionSkuInfos(dataList);
			}
		}
	}

	@Override
	public List<SeckillSkuRedisTo> getCurrentSeckillSkuList() {
		// 1.确定当前时间属于哪个秒杀场次
		long now = System.currentTimeMillis();
		Set<KillKey> keys = redisUtil.hashKeys(KillKey.KILL.getKey() + "*");
		for (KillKey key : keys){
			// seckill:sessions:1582250400000_158225400000
			String replace = key.getKey().replace(KillKey.KILL.getKey(), "");
			String[] s = replace.split("_");
			Long start = Long.parseLong(s[0]);
			Long end = Long.parseLong(s[1]);
			if (now >= start && now <= end) {
				// 2.获取这个秒杀场次需要的所有商品信息
				// 4_2 场次_商品id
				List<String> range = redisUtil.range(key, -100, 100);
				List<Object> list = redisUtil.hashMultiGet(KillKey.SECKILL_SKU_LIST, range);
				if (!CollectionUtils.isEmpty(list)){
					List<SeckillSkuRedisTo> collect = list.stream().map(item -> {
						SeckillSkuRedisTo to = JSON.parseObject(item.toString(), SeckillSkuRedisTo.class);
						// 当前秒杀开始需要 随机码
						// to.setRandomCode("******")
						return to;
					}).collect(Collectors.toList());
					return collect;
				}
				break;
			}
		}
		
		return null;
	}

	@Override
	public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
		// 1.查询所有需要参与秒杀的商品key
		Set<String> keys = redisUtil.hashKeys(KillKey.SECKILL_SKU_LIST.getKey());
		if (!CollectionUtils.isEmpty(keys)){
			String regx = "\\d_"+skuId;
			for (String key : keys){
				// 6_4
				boolean matches = Pattern.matches(regx, key);
				if (matches){
					// 匹配成功
					String s = redisUtil.hashGet(KillKey.SECKILL_SKU_LIST, key);
					SeckillSkuRedisTo redisTo = JSON.parseObject(s, SeckillSkuRedisTo.class);
					// 随机码 处理
					long now = System.currentTimeMillis();
					Long startTime = redisTo.getStartTime();
					Long endTime = redisTo.getEndTime();
					if (now >= startTime && now <= endTime) {
						
					} else {
						redisTo.setRandomCode("******");
					}
					
					return redisTo;
				}
			}
		}
		return null;
	}


	@Override
	public String kill(String killId, String key, Integer num) {
		long start = System.currentTimeMillis();
		MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
		// 1.登录 -->拦截器  LoginUserInterceptor
		// 2.获取当期秒杀的详情信息
		Object hash = redisUtil.hash(KillKey.SECKILL_SKU_LIST, killId);
		if (StringUtils.isEmpty(hash)) {
			return null;
		} else {
			SeckillSkuRedisTo redisTo = JSON.parseObject((String) hash, SeckillSkuRedisTo.class);
			// step1.校验合法性
			Long startTime = redisTo.getStartTime();
			Long endTime = redisTo.getEndTime();
			long now = System.currentTimeMillis();
			long ttl = endTime - now;
			if (now >= startTime && now <= endTime) {
				// 校验成功
				// step2.校验随机码和商品id
				String randomCode = redisTo.getRandomCode();
				String skuId = redisTo.getPromotionId()+"_"+ redisTo.getSkuId();
				if (randomCode.equals(key) && skuId.equals(killId)){
					// 校验成功
					// step3.验证购物数量是否合理
					if (num <= redisTo.getSecKillLimit()){
						// 验证这个人是否已经购买过 -->幂等性处理 [规定 如果秒杀成功 就去占位 userId_sessionId_skuId]
						String s = memberRespVo.getId() + "_" + skuId;
						// 设置自动过期
						boolean ifAbsent = redisUtil.setIfAbsent(KillKey.USER_BUY.setSuffix(s), num, ttl, TimeUnit.MILLISECONDS);
						if (ifAbsent){
							// 占位成功 说明从来没有买过
							KillKey seckillStock = KillKey.SECKILL_STOCK;
							RSemaphore semaphore = redisSon.getSemaphore(seckillStock.setSuffix(randomCode).getKey());
							// 不能阻塞 调用 acquire
							try {
								// tryAcquire(num)
								boolean b = semaphore.tryAcquire(num,100, TimeUnit.MILLISECONDS);
								if (b) {
									// 秒杀成功 ---> 进行快速下单 发送MQ 消息
									String timeId = IdWorker.getTimeId();
									SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
									seckillOrderTo.setOrderSn(timeId);
									seckillOrderTo.setMemberId(memberRespVo.getId());
									seckillOrderTo.setNum(num);
									seckillOrderTo.setPromotionSessionId(redisTo.getPromotionId());
									seckillOrderTo.setSkuId(redisTo.getSkuId());
									seckillOrderTo.setSecKillPrice(redisTo.getSecKillPrice());
									rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", seckillOrderTo);
									long end = System.currentTimeMillis();
									log.info("消耗时间:[{}]",end - start);
									return timeId;
								}
								return null;
							} catch (InterruptedException e) {
								// 拿不到信号量 就失败
								return null;
							}
						} else {
							//说明已经购买过了
							return null;
						}
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		return null;
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
