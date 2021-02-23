package com.zmm.mall.product.service.impl;

import com.zmm.common.utils.redis.RedisUtil;
import com.zmm.common.utils.redis.key.TestRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Name MessageProducerService
 * @Author 900045
 * @Created by 2020/12/12 0012
 */
@Service
public class MessageProducerService {

	@Autowired
	private RedisUtil redisUtil;

	@Value("${redis.queue.pop.timeout}")
	private Long popTimeout;

	public Long sendMessage(String orderSn) {
		SimpleDateFormat s=new SimpleDateFormat("yyyy年MM月dd日Ea hh时mm分ss秒SS毫秒");
		System.out.println(s.format(new Date()) +"发送了" + orderSn);
		redisUtil.zsetAdd(TestRedisKey.ORDER_LOCK_ZSET_KEY, orderSn, popTimeout);
    	return redisUtil.leftPush(TestRedisKey.TEST_ORDER_, orderSn);
	}
}
