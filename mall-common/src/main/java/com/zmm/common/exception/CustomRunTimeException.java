package com.zmm.common.exception;

import com.zmm.common.base.model.RespCode;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.enums.RecordType;

/**
 * @Name CustomRunTimeException
 * @Author 900045
 * @Created by 2020/11/18 0018
 */
public class CustomRunTimeException extends RuntimeException implements BaseException {

	/**
	 *  结果码
	 */
	protected RespCode        respCode         = ResultCode.FAIL;

	public CustomRunTimeException(String msg) {
		super(msg);
	}

	public CustomRunTimeException(Throwable e) {
		super(e);
	}

	/**
	 *    业务异常
	 *
	 * @param respCode   异常码对象
	 */
	public CustomRunTimeException(RespCode respCode) {
		super(respCode.toString());
		this.respCode = respCode;
	}


	/**
	 *    业务异常
	 *
	 * @param respCode   异常码对象
	 * @param msg   异常信息
	 */
	public CustomRunTimeException(RespCode respCode, String msg) {
		super(new StringBuffer().append(respCode).append(" ").append(getMsg(msg)).toString());
		this.respCode = respCode;
	}


	/**
	 *    业务异常
	 *
	 * @param respCode   异常码对象
	 * @param msg   异常信息
	 * @param e    异常对象
	 */
	public CustomRunTimeException(RespCode respCode, Object msg, Throwable e) {
		super(new StringBuffer().append(respCode).append(" ").append(getMsg(msg)).toString(), e);
		this.respCode = respCode;
	}

	public CustomRunTimeException(RespCode respCode, RecordType recordType, Object msg) {
		super(new StringBuffer().append(respCode).append(" ")
				.append((recordType == null ? "" : recordType)).append(" ").append(getMsg(msg))
				.toString());
		this.respCode = respCode;
	}

	public CustomRunTimeException(RespCode respCode, Throwable e) {
		super(respCode.getDesc(), e);
		this.respCode = respCode;
	}

	public CustomRunTimeException(String msg, Throwable e) {
		super(msg, e);
	}

	/**
	 *    业务异常
	 *
	 * @param respCode   异常码对象
	 * @param msg   异常信息
	 */
	public CustomRunTimeException(RespCode respCode, Object msg) {
		super(new StringBuffer().append(respCode).append(" ").append(getMsg(msg)).toString());
		this.respCode = respCode;
	}

	@Override
	public RespCode getCode() {
		return this.respCode;
	}


	/**
	 *  字符串处理
	 *      null字符串返回空
	 * @param msg
	 * @return
	 */
	private static String getMsg(String msg) {
		return msg == null ? "" : msg;
	}

	/**
	 *    对象消息处理
	 *
	 * @param msg
	 * @return
	 */
	private static String getMsg(Object msg) {
		return msg == null ? "" : String.valueOf(msg);
	}


}
