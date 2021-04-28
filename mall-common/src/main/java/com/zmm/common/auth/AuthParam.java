package com.zmm.common.auth;

import com.zmm.common.vo.MemberRespVo;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证使用的参数模型
 * @author 900045
 * @description:
 * @name AuthParam
 * @date By 2021-04-28 10:43:49
 */
@Data
public class AuthParam {

	private String path;


	/**
	 * 是否允许匿名访问 
	 */
	private boolean canAnon;

	private String ipAddress;


	private MemberRespVo user;

	/**
	 * 请求开始时间
	 */
	private long        startTime = System.currentTimeMillis();

	private HttpServletRequest request;
	private HttpServletResponse response;

	public AuthParam(HttpServletRequest request, HttpServletResponse response) {
		super();
		this.request = request;
		this.response = response;
	}
}
