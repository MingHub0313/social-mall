package com.zmm.common.base.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @Name ResultCode
 * @Author 900045
 * @Created by 2020/11/18 0018
 */
public enum ResultCode implements RespCode {

	/**
	 * 错误码和错误信息定义类
	 * 1.错误码定义规则为 6位数
	 * 2.前面两位表示业务场景,最后四位表示错误码. 例如 100001. 10: 通用 0001: 系统未知异常
	 * 3.维护错误码后需要维护错误信息
	 * 错误码列表:
	 * 	10: 通用
	 * 		0000: 成功
	 * 		0001: 系统未知异常
	 * 		0002: 操作失败
	 * 		0003: ....
	 * 	11:商品
	 * 	12:订单
	 * 	13:购物车
	 * 	14:物流
	 * 	15:用户
	 */
	
	TEST_REDIS_KEY_NULL(-111111,"测试redis: key-value 为空对象"),
	APP_FAIL(999999, "服务请求失败"),
	REMOTE_SERVICE_FAIL(888888,"远程服务调用失败"),
	


	SUCCESS(100000, "成功"),
	FAIL(100001, "系统异常，请刷新后重试"),
	FIELD(100002, "操作失败!"),
	SMS_CODE_EXCEPTION(100003, "短信验证码发送太快,稍后再试"),
	METHOD_CALL_PARAMETER_ERROR(100004, "请求参数有误 请按照规则填写"),
	TO_MANY_REQUEST(100005,"流量请求过大"),

	EXCEPTION(150000,"存在异常"),
	USERNAME_NOT_UNIQUE(150001,"用户名不唯一"),
	PHONE_NOT_UNIQUE(150002,"手机号不唯一"),
	LOGIN_ACCT_PASSWORD_INVALID_ERROR(150003,"账号密码错误"),
	WBO_AUTH_FAIL(150004,"微博认证失败"),
	USER_NOT_LOGIN(150005,"用户未登录"),
	
	
	;


	/**
	 * 编码
	 */
	private int code;

	/**
	 * 中文描述
	 */
	private String desc;

	/**
	 * 英文描述
	 * private String enDesc
	 */

	/**
	 * 返回码MAP
	 */
	private static Map<Integer, RespCode> map;


	static {
		// 初始化
		map = new HashMap<>();
		for (ResultCode code : ResultCode.values()) {
			map.put(code.getCode(), code);
		}

		/**
		for (RespOrderCode code : RespOrderCode.values()) {
			map.put(code.getCode(), code);
		}

		for (RespSupplyCode code : RespSupplyCode.values()) {
			map.put(code.getCode(), code);
		}
		 */
	}

	ResultCode(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	/**
	 * 获取返回码反描述
	 *
	 * @param
	 * @return
	 */
	public static String getDesc(int code) {
		RespCode respCode = map.get(code);
		return respCode == null ? "" : respCode.getDesc();
	}

	public static RespCode getType(int code) {
		if (code <= 0) {
			return null;
		}
		return map.get(code);
	}

	@Override
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public boolean print() {
		return false;
	}

	@Override
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
