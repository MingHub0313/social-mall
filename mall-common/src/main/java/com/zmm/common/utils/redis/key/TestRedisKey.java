package com.zmm.common.utils.redis.key;

/**
 * @Name TestRedisKey
 * @Author 900045
 * @Created by 2020/11/19 0019
 */
public enum TestRedisKey implements RedisKey {

	// ========================= String 类型 =======================
	// 不存在的 key
	TEST_KEY_NOT_EXIST("TEST_KEY_NOT_EXIST"),
	TEST_KEY_ONE("TEST_KEY_ONE"),
	TEST_KEY_TWO("TEST_KEY_TWO"),
	// 设置 key 空对象
	TEST_KEY_NULL_EMPTY("TEST_KEY_NULL_EMPTY"),

	TEST_ORDER_("TEST_ORDER_"),
	ORDER_LOCK_ZSET_KEY("ORDER_LOCK_ZSET"),
	;


	/**
	 * key 前缀
	 */
	private String key;


	TestRedisKey() {
		key = this.name();
	}

	TestRedisKey(String key) {
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
