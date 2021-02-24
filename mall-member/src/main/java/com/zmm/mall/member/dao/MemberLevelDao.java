package com.zmm.mall.member.dao;

import com.zmm.mall.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

	/**
	 * 获取 默认的等级
	 * @author: 900045
	 * @date: 2021-02-24 17:01:51
	 * @throws 
	
	 * @return: com.zmm.mall.member.entity.MemberLevelEntity
	 **/
	MemberLevelEntity getDefaultLevel();
}
