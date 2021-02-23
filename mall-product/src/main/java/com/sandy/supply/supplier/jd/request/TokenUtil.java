package com.sandy.supply.supplier.jd.request;

/**
 * @Name TokenUtil
 * @Author 900045
 * @Created by 2021/1/14 0014
 */
public class TokenUtil {

	public static final int USERCODELENGTH = 8;

	/**
	 * 生成获取APP 用户 token标识  主要为了区分用户类型，以防ID重复
	 *
	 * @param customerId
	 * @return
	 */
	public static String genernateCustomerId(int customerId) {
		return genernateUserId(customerId, TokenType.CUSTOMER);
	}

	/**
	 * 生成获取管理人员用户TOKEN标识   主要为了区分用户类型，以防ID重复
	 *
	 * @param customerId
	 * @return
	 */
	public static String genernateServicerId(int serviceId) {
		return genernateUserId(serviceId, TokenType.SERVICER);
	}

	/**
	 *  生成POP店TOKEN标识
	 *
	 * @param serviceId
	 * @return
	 */
	public static String genernatePoperId(int serviceId) {
		return genernateUserId(serviceId, TokenType.POPER);
	}

	/**
	 * 用户token对应的ID生成获取，主要为了区分用户类型，以防ID重复
	 *
	 * @param userId
	 * @param tokenType
	 * @return
	 */
	public static String genernateUserId(int userId, TokenType tokenType) {
		return new StringBuilder(tokenType.toString()).append(userId).toString();
	}

	/**
	 *  token类型  区分用户类型
	 *
	 * @author zhangyg
	 * @version $Id: TokenUtil.java, v 0.1 2019年3月11日 上午9:46:32 zhangyg Exp $
	 */
	public enum TokenType {
		CUSTOMER, SERVICER, POPER
	}

	/**
	 * 方法名: getToken
	 * 方法描述: 根据用户编号生成token(会员编号64进制+随机数+1位检验码+会员编号64位长度)
	 * 修改时间：2017年9月20日 上午10:39:35 
	 * 参数 @param userId
	 * 参数 @return 参数说明
	 * 返回类型 String 返回类型
	 */
	

	/**
	 * 方法名: fillZero
	 * 方法描述: 位数不足0补齐
	 * 修改时间：2016年12月30日 上午10:23:21 
	 * 参数 @param goodsId
	 * 参数 @return 参数说明
	 * 返回类型 String 返回类型
	 */
	

	/**
	 * 方法名: getSignature
	 * 方法描述: 用代理信息生成签名
	 * 修改时间：2017年10月16日 下午2:40:23 
	 * 参数 @param user
	 * 参数 @return 参数说明
	 * 返回类型 String 返回类型
	 */
	
	/**
	 * 方法名: checkSignature
	 * 方法描述: 验证签名
	 * 修改时间：2017年10月16日 下午2:44:04 
	 * 参数 @param signature
	 * 参数 @param sigStr
	 * 参数 @return 参数说明
	 * 返回类型 boolean 返回类型
	 */
	
	/**
	 * 方法名: checkToken
	 * 方法描述: 验证token是否合法
	 * 修改时间：2017年9月20日 上午10:50:47 
	 * 参数 @param token
	 * 参数 @return 参数说明
	 * 返回类型 boolean 返回类型
	 */
	

	/**
	 * 方法名: checkToken
	 * 方法描述: 验证token是否合法
	 * 修改时间：2017年9月20日 上午10:50:47 
	 * 参数 @param token
	 * 参数 @return 参数说明
	 * 返回类型 boolean 返回类型
	 */
	

	/**
	 * 方法名: getUserIdCode
	 * 方法描述: 获取会员编号64位编码并补全11位
	 * 修改时间：2017年9月20日 上午11:33:02 
	 * 参数 @param userId
	 * 参数 @return 参数说明
	 * 返回类型 String 返回类型
	 */

	/**
	 * 方法名: getUserIdByCode
	 * 方法描述: 根据token获取用户编号
	 * 修改时间：2017年11月24日 上午11:28:16 
	 * 参数 @param token
	 * 参数 @return 参数说明
	 * 返回类型 long 返回类型
	 */

}
