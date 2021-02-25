package com.zmm.mall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @author 900045
 * @description:
 * @name LoginVo
 * @date By 2021-02-25 10:08:16
 */
@Data
public class LoginVo {


	/**
	 * 登录账号
	 */
	@NotEmpty(message = "用户名必须提交")
	@Length(min = 6,max = 18,message ="用户名必须是6-18位字符")
	private String loginAccount;

	/**
	 * 登录密码
	 */
	@NotEmpty(message = "密码名必须提交")
	@Length(min = 6,max = 18,message ="密码必须是6-18位字符")
	private String password;
}
