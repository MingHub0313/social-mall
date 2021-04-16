package com.zmm.common.utils.redis;

/**
 * 分布式锁
 * @author 900045
 * @description:
 * @name LockKey
 * @date By 2021-04-16 17:28:57
 */
public class LockKey {

	/**
	 * 上架秒杀商品的锁
	 */
	public static final String UPLOAD_LOCK = "seckill:upload:lock";
}
