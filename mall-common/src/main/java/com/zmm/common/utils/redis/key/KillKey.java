package com.zmm.common.utils.redis.key;

/**
 * 秒杀的key
 * @author 900045
 * @description:
 * @name KillKey
 * @date By 2021-04-16 16:35:36
 */
public enum KillKey implements RedisKey{
	
	/** 秒杀活动信息*/
	KILL("seckill:sessions"),
	/** 秒杀活动对应的 商品信息*/
	SECKILL_SKU_LIST("seckill:sku:list"),
	/** 秒杀库存 + 商品随机码*/
	SECKILL_STOCK("seckill:stock:"),
	
	
	;

	private String key;

	KillKey() {
		key = this.name();
	}

	KillKey(String key) {
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
