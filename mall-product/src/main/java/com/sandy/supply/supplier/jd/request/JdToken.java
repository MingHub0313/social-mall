package com.sandy.supply.supplier.jd.request;

import java.io.Serializable;

/**
 * @Name JdToken
 * @Author 900045
 * @Created by 2020/11/13 0013
 */
public class JdToken implements Serializable {

	/**  */
	private static final long serialVersionUID = 8748004075938350427L;
	private String  uid;
	private String  access_token;
	private String  refresh_token;
	/**   当前时间，时间戳格式：1551663377887 */
	private Long    time;
	/** access_token的有效期，单位：秒，有效期24小时 */
	private Integer expires_in;
	/** refresh_token的过期时间，毫秒级别,时间戳*/
	private Integer refresh_token_expires;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Integer getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(Integer expires_in) {
		this.expires_in = expires_in;
	}

	public Integer getRefresh_token_expires() {
		return refresh_token_expires;
	}

	public void setRefresh_token_expires(Integer refresh_token_expires) {
		this.refresh_token_expires = refresh_token_expires;
	}

	@Override
	public String toString() {
		return "JdToken{" +
				"uid='" + uid + '\'' +
				", access_token='" + access_token + '\'' +
				", refresh_token='" + refresh_token + '\'' +
				", time=" + time +
				", expires_in=" + expires_in +
				", refresh_token_expires=" + refresh_token_expires +
				'}';
	}
}
