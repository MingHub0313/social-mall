package com.zmm.mall.product.service.impl;

import com.zmm.common.utils.redis.RedisUtil;
import com.zmm.common.utils.redis.key.TestRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @Name MessageConsumerService
 * @Author 900045
 * @Created by 2020/12/12 0012
 */
@Service
public class MessageConsumerService extends Thread {

	@Autowired
	private RedisUtil redisUtil;

	private CountDownLatch latch;


	private volatile boolean flag = true;

	@Value("${redis.queue.pop.timeout}")
	private Long popTimeout;

	@Override
	public void run() {
		try {
			Object message;
			while(flag && !Thread.currentThread().isInterrupted()) {
				message = redisUtil.rightPop(TestRedisKey.TEST_ORDER_,popTimeout);
				SimpleDateFormat s=new SimpleDateFormat("yyyy年MM月dd日Ea hh时mm分ss秒SS毫秒");
				System.out.println(s.format(new Date())+"接收到了" + message);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}


}
