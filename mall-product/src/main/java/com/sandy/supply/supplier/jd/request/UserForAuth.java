package com.sandy.supply.supplier.jd.request;

import java.io.Serializable;

/**
 * @Name UserForAuth
 * @Author 900045
 * @Created by 2021/1/14 0014
 */
public class UserForAuth implements Serializable {

	/**  */
	private static final long serialVersionUID = -2141955778900057369L;

	private int               userId;

	private String            userName;

	private String            phone;

	private short             roleId;

	/** 是否是服务工作人员， 除开正常用户外的所有管理 工作人员*/
	private boolean           servicer;

	/**
	 * 用户类型
	 */
	private byte              userType;

	/**
	 *
	 *   为了不能影响线上，暂时不能转回Integer
	 *
	 * 店铺ID（POP店使用）
	 * @author 000
	 * @Description
	 * @Date 17:42 2019/7/16 0016
	 * @param
	 * @return
	 */
	private Long              shopId;

	/**
	 *   为了不能影响线上，暂时不能转回Integer
	 *
	 * 供应商id
	 * @author 000
	 * @Description
	 * @Date 14:38 2019/7/18 0018
	 * @param
	 * @return
	 */
	private Long              supplierId;

	/**
	 *
	 * 运营模式：1.商家自营，2.一件代发，3.平台自营
	 * @author 000
	 * @Description
	 * @Date 10:53 2019/9/18 0018
	 * @param
	 * @return
	 */
	private Integer           businessModel;

	public byte getUserType() {
		return userType;
	}

	public void setUserType(byte userType) {
		this.userType = userType;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public short getRoleId() {
		return roleId;
	}

	public void setRoleId(short roleId) {
		this.roleId = roleId;
	}

	public boolean isServicer() {
		return servicer;
	}

	public void setServicer(boolean servicer) {
		this.servicer = servicer;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public Integer getBusinessModel() {
		return businessModel;
	}

	public void setBusinessModel(Integer businessModel) {
		this.businessModel = businessModel;
	}

	@Override
	public String toString() {
		return "UserForAuth [userId=" + userId + ", userName=" + userName + ", roleId=" + roleId
				+ ", servicer=" + servicer + "]";
	}

}
