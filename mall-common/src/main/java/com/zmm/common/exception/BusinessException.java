package com.zmm.common.exception;

import com.zmm.common.base.model.RespCode;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.enums.RecordType;

/**
 * @Name BusinessException
 * @Author 900045
 * @Created by 2020/11/19 0019
 */
public class BusinessException extends Exception implements BaseException {


	private static final long serialVersionUID = 1005154697943946949L;

	/**
	 *  结果码
	 */
	protected RespCode        respCode         = ResultCode.FAIL;

	public BusinessException(String msg) {
		super(msg);
	}

	public BusinessException(Throwable e) {
		super(e);
	}

	/**
	 *    业务异常
	 *
	 * @param respCode   异常码对象
	 */
	public BusinessException(RespCode respCode) {
		super(respCode.toString());
		this.respCode = respCode;
	}

	/**
	 *    业务异常
	 *
	 * @param respCode   异常码对象
	 * @param recordType  关联记录类型
	 */
	public BusinessException(RespCode respCode, RecordType recordType) {
		super(new StringBuffer().append(respCode).append(" ")
				.append((recordType == null ? "" : recordType)).toString());
		this.respCode = respCode;
	}

	/**
	 *    业务异常
	 *
	 * @param respCode   异常码对象
	 * @param recordType  关联记录类型
	 * @param msg   异常信息
	 */
	public BusinessException(RespCode respCode, RecordType recordType, Object msg) {
		super(new StringBuffer().append(respCode).append(" ")
				.append((recordType == null ? "" : recordType)).append(" ").append(getMsg(msg))
				.toString());
		this.respCode = respCode;
	}

	/**
	 *    业务异常
	 *
	 * @param respCode   异常码对象
	 * @param msg   异常信息
	 */
	public BusinessException(RespCode respCode, String msg) {
		super(new StringBuffer().append(respCode).append(" ").append(getMsg(msg)).toString());
		this.respCode = respCode;
	}

	/**
	 *    业务异常
	 *
	 * @param respCode   异常码对象
	 * @param msg   异常信息
	 */
	public BusinessException(RespCode respCode, Object msg) {
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
	public BusinessException(RespCode respCode, Object msg, Throwable e) {
		super(new StringBuffer().append(respCode).append(" ").append(getMsg(msg)).toString(), e);
		this.respCode = respCode;
	}

	public BusinessException(RespCode respCode, Throwable e) {
		super(respCode.getDesc(), e);
		this.respCode = respCode;
	}

	public BusinessException(String msg, Throwable e) {
		super(msg, e);
	}

	/**
	 * 获取  返回的结果码
	 *
	 * @return
	 */
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
