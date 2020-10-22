package com.zmm.mall.product.dao;

import com.zmm.mall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

	/**
	 * 查询
	 * @param attrIdList
	 * @return
	 */
	List<Long> selectSearchAttrs(@Param("attrIds") List<Long> attrIdList);
}
