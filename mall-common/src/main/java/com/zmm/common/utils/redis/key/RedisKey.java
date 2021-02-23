package com.zmm.common.utils.redis.key;

import java.util.HashMap;
import java.util.Map;

/**
 * @Name RedisKey
 * @Author 900045
 * @Created by 2020/11/18 0018
 */
public interface RedisKey {

	String SEPARATE    = "";


	/**
	 * 因为枚举是单例所以只能用这个进行缓存
	 */
	ThreadLocal<Map<RedisKey, String>> SUFFIX_MAP = new ThreadLocal<>();


	/**
	 * 设置后缀 追加
	 * @param suffix
	 * @param postfix
	 * @return
	 */
	default RedisKey setSuffix(String suffix, String... postfix) {
		Map<RedisKey, String> map = SUFFIX_MAP.get();
		if (map == null) {
			map = new HashMap<>();
		}
		if (postfix != null && postfix.length > 0) {
			StringBuilder sb = new StringBuilder(suffix);
			for (String string : postfix) {
				sb.append(SEPARATE).append(string);
			}
			map.put(this, sb.toString());
		} else {
			map.put(this, suffix);
		}
		SUFFIX_MAP.set(map);
		return this;
	}

	/**
	 * 设置后缀
	 * @param suffix
	 * @param postfix
	 * @return
	 */
	default RedisKey setSuffix(Number suffix, String... postfix) {
		setSuffix(String.valueOf(suffix), postfix);
		return this;
	}

	/**
	 * 设置后缀 注意: 注意不能使用   setSuffix(s).setSuffix(s)  多次设置的方式,  每次设置都会覆盖之前的配置
	 * @param suffix 字符串类型
	 * @return
	 */
	default RedisKey setSuffix(String suffix) {
		Map<RedisKey, String> map = SUFFIX_MAP.get();
		if (map == null) {
			map = new HashMap<>();
		}
		map.put(this, suffix);
		SUFFIX_MAP.set(map);
		return this;
	}

	/**
	 * 设置后缀
	 * @param suffix 数值类型
	 * @return
	 */
	default RedisKey setSuffix(Number suffix) {
		setSuffix(String.valueOf(suffix));
		return this;
	}


	/**
	 * 获取后缀
	 * @return
	 */
	default String getSuffix() {
		Map<RedisKey, String> map = SUFFIX_MAP.get();
		return map == null ? null : map.get(this);
	}


	/**
	 * 获取实际 redis key值
	 *
	 * @return
	 */
	String getKey();

}
