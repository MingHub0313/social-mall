package com.zmm.mall.product.dao;

import com.zmm.mall.product.entity.AttrAttrGroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Mapper
public interface AttrAttrGroupRelationDao extends BaseMapper<AttrAttrGroupRelationEntity> {

	/**
	 * 批量删除关联关系
	 * @param entities
	 */
	void deleteBatchRelation(@Param("entities") List<AttrAttrGroupRelationEntity> entities);
}
