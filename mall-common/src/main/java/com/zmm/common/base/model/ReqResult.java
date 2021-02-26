package com.zmm.common.base.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * 操作返回对象
 * @author 900045
 * @description:
 * @name ReqResult
 * @date By 2021-02-24 14:37:21
 */
@Data
public class ReqResult<T> implements Serializable {
	private static final long serialVersionUID = 6723459114143256160L;
	
	/**
	 * 返回码模型，不序列化到前端
	 */
	@JsonIgnore
	private RespCode          respCode         = ResultCode.SUCCESS;

	/**
	 * 返回错误描述   正式环境下可能不返回
	 */
	private String            error;
	/** 错误日志唯一标识 */
	private String            errorCode;

	private int               httpStatus;

	/**
	 * 操作时间
	 */
	private long              nowTime          = System.currentTimeMillis();

	/**
	 * 返回数据
	 */
	public T                  data;

	/**
	 * 扩展数据
	 */
	public Object             exData;


	public ReqResult() {
	}

	public ReqResult(T data) {
		this.data = data;
	}

	public <T> T getEntityObject(Object data,TypeReference<T> typeReference){
		// get() --->Map类型
		String jsonString = JSON.toJSONString(data);
		T t = JSON.parseObject(jsonString, typeReference);
		return t;
	}

	public ReqResult(RespCode respCode) {
		this.setResultCode(respCode);
	}

	public void setResultCode(RespCode respCode) {
		this.respCode = respCode;
	}

	public ReqResult(RespCode respCode, T msg) {
		this.setResultCode(respCode, msg);
	}

	public void setResultCode(RespCode respCode, T msg) {
		this.respCode = respCode;
		if (this.getError() == null) {
			this.setError(new StringBuilder().append(respCode).append(":")
					.append(respCode.getDesc()).toString());
			this.setData(msg);
		}
	}

	public int getResultCode() {
		return respCode.getCode();
	}

	/**
	 *   返回文案
	 *
	 * @return
	 */
	public String getReason() {
		return respCode.getDesc();
	}

	/**
	 *  是否成功
	 *
	 * @return
	 */
	public boolean isSuccess() {
		return this.respCode == ResultCode.SUCCESS;
	}


	public void setResultCode(int respCode) {
		RespCode type = ResultCode.getType(respCode);
		this.respCode = type == null ? ResultCode.APP_FAIL : type;
	}

	
}
