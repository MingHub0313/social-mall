package com.zmm.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.exception.BusinessException;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.member.entity.MemberEntity;
import com.zmm.mall.member.vo.MemberLoginVo;
import com.zmm.mall.member.vo.MemberRegisterVo;
import com.zmm.mall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
public interface MemberService extends IService<MemberEntity> {

    /**
     * 查询 会员
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 会员注册
     * @author: 900045
     * @date: 2021-02-24 16:58:08
     * @throws BusinessException
     * @param vo: 
     * @return: void
     **/
	void register(MemberRegisterVo vo) throws BusinessException;
	
	/**
	 * 检查 邮箱是否唯一
	 * @author: 900045
	 * @date: 2021-02-24 17:07:05
	 * @throws BusinessException
	 * @param phone: 
	 * @return: void
	 **/
	void checkPhoneUnique(String phone) throws BusinessException;
	
	/**
	 * 检查 用户名是否唯一
	 * @author: 900045
	 * @date: 2021-02-24 17:07:23
	 * @throws BusinessException
	 * @param userName: 
	 * @return: void
	 **/
	void checkUserNameUnique(String userName) throws BusinessException;

	/**
	 * 用户登录
	 * @author: 900045
	 * @date: 2021-02-25 10:15:49
	 * @throws 
	 * @param vo: 
	 * @return: com.zmm.mall.member.entity.MemberEntity
	 **/
	MemberEntity login(MemberLoginVo vo);

	/**
	 * 社交登录
	 * @author: 900045
	 * @date: 2021-02-25 14:23:24
	 * @throws 
	 * @param socialUser: 
	 * @return: com.zmm.mall.member.entity.MemberEntity
	 **/
	MemberEntity login(SocialUser socialUser);
}

