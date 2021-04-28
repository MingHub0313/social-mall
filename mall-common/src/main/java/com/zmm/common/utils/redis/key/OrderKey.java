package com.zmm.common.utils.redis.key;

/**
 * @Description:
 * @Name OrderKey
 * @Author Administrator
 * @Date By 2021-03-11 20:34:02
 */
public enum OrderKey implements RedisKey{

    /** 订单防重令牌*/
    USER_ORDER_TOKEN("order:token:")
    ;


    private String key;

    OrderKey() {
        key = this.name();
    }

    OrderKey(String key) {
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
