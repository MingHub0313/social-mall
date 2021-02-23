package com.zmm.mall.product.util;

import com.zmm.common.utils.redis.key.TestRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @Name RedisKeyExpirationListener
 * @Author 900045
 * @Created by 2020/12/12 0012
 */
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

	public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
		super(listenerContainer);
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		// 注意：我们只能获取到失效的 key 而获取不到 value，
		// 所以在设计 key 的时候可以考虑把 value 一并放到 key，然后根据规则取出 value
		String key = new String(message.getBody());
		log.info("key:{}", key);
		// message.toString()也可以获取失效的key
		String expiredKey = message.toString();
		log.info("expiredKey:{}", expiredKey);
		//如果是 order- 开头的key，将订单状态设置为 已取消
		if(expiredKey.startsWith(TestRedisKey.TEST_ORDER_.getKey())){
			log.info("订单已过期：查询数据库对应记录是否已支付");
			// TODO
		}
	}

}
