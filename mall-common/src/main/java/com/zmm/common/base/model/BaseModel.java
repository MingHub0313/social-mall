package com.zmm.common.base.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * 基础类封装  不序列化的字段
 * @Name BaseModel
 * @Author 900045
 * @Created by 2020/11/18 0018
 */
@JsonIgnoreProperties(value = { "optimistic" })
public class BaseModel implements Serializable {

	private static final long serialVersionUID = 7858050170706503711L;
}
