package com.zmm.common.utils.redis.key;

/**
 * @Description:
 * @Name CartKey
 * @Author Administrator
 * @Date By 2021-03-03 20:54:28
 */
public enum CartKey implements RedisKey{

    MALL_CART("mall:cart:"),
    ;

    /**
     * key 前缀
     */
    private String key;


    CartKey() {
        key = this.name();
    }

    CartKey(String key) {
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
