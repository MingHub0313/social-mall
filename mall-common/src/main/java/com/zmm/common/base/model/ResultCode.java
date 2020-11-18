package com.zmm.common.base.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @Name ResultCode
 * @Author 900045
 * @Created by 2020/11/18 0018
 */
public enum ResultCode implements RespCode {

	FAIL(999999, "系统异常，请刷新后重试"),
	FIELD(000000, "操作失败!"),
	METHOD_CALL_PARAMETER_ERROR(888888, "请求参数有误 请按照规则填写"),
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
