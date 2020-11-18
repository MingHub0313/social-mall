package com.zmm.common.base.model;

/**
 * @Name EmptyModel
 * @Author 900045
 * @Created by 2020/11/18 0018
 */
public class EmptyModel extends BaseModel {


	/**
	 * 饥饿单例
	 */
	private static final EmptyModel EMPTY_MODEL      = new EmptyModel();

	private EmptyModel() {

	}


	/**
	 * 获取空模型对象
	 *
	 * @return
	 */
	public static EmptyModel instance() {
		return EMPTY_MODEL;
	}


}
