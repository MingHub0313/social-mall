package com.zmm.common.enums;

/**
 * @Name AssertEnum
 * @Author 900045
 * @Created by 2020/11/18 0018
 */
public enum AssertEnum {

	NOT_POSITIVE_INTEGER("该数不是正整数!!!"),
	PARAMETER_CANNOT_NULL("参数不能为空!!!"),
	STRING_CANNOT_NULL("字符串不能为空!!!"),

	;

	private String message;

	AssertEnum(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
