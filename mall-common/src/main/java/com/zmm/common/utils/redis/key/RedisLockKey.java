package com.zmm.common.utils.redis.key;

/**
 * @author 900045
 * @description:
 * @name RedisLockKey
 * @date By 2021-03-15 17:24:42
 */
public enum RedisLockKey implements RedisKey {

	/** 京东价格同步锁 */
	JD_PRICE_SYNC_LOCK,
	;
	/**
	 * key 前缀
	 */
	private String   key;

	RedisLockKey() {
		this.key = this.name();
	}

	RedisLockKey(String key) {
		this.key = key;
	}

	@Override
	public String getKey() {
		String suffix = getSuffix();
		if (suffix == null) {
			return key;
		}
		return new StringBuilder(this.key).append(SEPARATE).append(suffix).toString();
	}
}
