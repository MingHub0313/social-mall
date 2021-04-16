package com.zmm.mall.seckill.scheduled;

import com.zmm.common.utils.redis.LockKey;
import com.zmm.mall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品的定时上架
 * 	每天晚上3点,上架最近三天需要秒杀的商品
 * 	当天的 00:00:00 ~ 23:59:59
 * 	明天的 00:00:00 ~ 23:59:59
 * 	后天的 00:00:00 ~ 23:59:59
 * @author 900045
 * @description:
 * @name SeckillSkuScheduled
 * @date By 2021-04-16 15:50:56
 */
@Slf4j
@Service
public class SeckillSkuScheduled {
	
	@Resource
	private SeckillService seckillService;
	
	@Resource
	private RedissonClient redisSon;
	
	
	/**
	 * 定时任务执行 上架秒杀商品  只允许上架一次
	 * 分布式情况下 -- 定时任务  --- 加分布式锁
	 * @author: 900045
	 * @date: 2021-04-16 17:27:08
	 * @throws 
	 * @return: void
	 **/
	@Scheduled(cron = "0 0 3 * * ?")
	public void uploadSeckillSkuLatest3Days(){
		String uploadLock = LockKey.UPLOAD_LOCK;
		// 分布式锁 锁的业务执行完成,状态已经更新完成.释放锁之后,其他人获取到的就会是最新状态
		RLock lock = redisSon.getLock(uploadLock);
		lock.lock(10, TimeUnit.SECONDS);
		try {
			log.error("上架秒杀的商品信息....");
			// 上架过的商品 下次定时任务就无需执行了  ===> 幂等性问题
			// 1.重复上架 无需处理
			seckillService.uploadSeckillSkuLatest3Days();
		} finally {
			lock.unlock();
		}
	}
}
