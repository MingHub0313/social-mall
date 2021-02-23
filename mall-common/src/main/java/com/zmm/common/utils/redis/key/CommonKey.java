package com.zmm.common.utils.redis.key;

/**
 * @Name CommonKey
 * @Author 900045
 * @Created by 2020/11/21 0021
 */
public enum CommonKey implements RedisKey {

	ORDER_LOCK_ZSET_LOCK_KEY,
	ACT_LUCK_WHEEL_PRIZE_INFO("ACT:LUCK_WHEEL:PRIZE_INFO"),
	/**
	 * 认证： 用户信息
	 */
	AUTH_USER_KEY("AUTH:USER"),

	/**
	 * 认证： token 对应的 用户编码
	 */
	AUTH_TOKEN_USER_PREFIX("AUTH:TOKEN_USER_"),

	/**
	 * 认证： 刷新token 对应的 用户编码
	 */
	AUTH_TOKEN_REFRESH_PREFIX("AUTH:TOKEN_R_USER_"),
	;

	;

	private String key;

	CommonKey() {
		key = this.name();
	}

	CommonKey(String key) {
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
