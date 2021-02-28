package com.bigdata.zmm.mall.cart.to;

import lombok.Data;

/**
 * @Description: to传输对象
 * @Name UserInfoVo
 * @Author Administrator
 * @Date By 2021-02-28 23:23:23
 */
@Data
public class UserInfoTo {

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 用户的临时key 一定存在
     */
    private String userKey;

    /**
     * flag 默认 false 不是临时用户
     */
    private boolean tempUser = false;
}
