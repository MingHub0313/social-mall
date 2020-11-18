package com.zmm.common.exception;

import com.zmm.common.base.model.RespCode;

/**
 * 基础异常类
 * @Name BaseException
 * @Author 900045
 * @Created by 2020/11/18 0018
 */
public interface BaseException {

	/**
	 * 获取返回码
	 *
	 * @return
	 */
	RespCode getCode();
}
