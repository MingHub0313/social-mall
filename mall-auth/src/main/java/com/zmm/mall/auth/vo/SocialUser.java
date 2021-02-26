package com.zmm.mall.auth.vo;

import lombok.Data;

/**
 * @author 900045
 * @description:
 * @name SocialUser
 * @date By 2021-02-25 14:09:23
 */
@Data
public class SocialUser {

	private String access_token;

	private String remind_in;

	private String expires_in;

	private String uid;

	private String isRealName;
}
