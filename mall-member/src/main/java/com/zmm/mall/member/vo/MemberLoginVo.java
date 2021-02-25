package com.zmm.mall.member.vo;

import lombok.Data;

/**
 * @author 900045
 * @description:
 * @name MemberLoginVo
 * @date By 2021-02-25 10:13:57
 */
@Data
public class MemberLoginVo {

	/**
	 * 登录账号
	 */
	private String loginAccount;

	/**
	 * 登录密码
	 */
	private String password;
	
}
