package com.zmm.common.base.model;

/**
 * 通用返回码定义
 * @Name RespCode
 * @Author 900045
 * @Created by 2020/11/18 0018
 */
public interface RespCode {

	/**
	 * 获取返回码
	 *
	 * @return
	 */
	int getCode();

	/**
	 *   是否打印详情日志
	 *
	 * @return
	 */
	boolean print();

	/**
	 * 获取返回描述
	 *
	 * @return
	 */
	String getDesc();
}
