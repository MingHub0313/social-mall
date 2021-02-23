package com.zmm.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmm.mall.product.entity.AttrGroupEntity;
import com.zmm.mall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

	/**
	 * 连接查询
	 * @author: 900045
	 * @date: 2021-02-23 16:29:26
	 * @throws 
	 * @param catalogId: 
	 * @param spuId: 
	 * @return: java.util.List<com.zmm.mall.product.vo.SkuItemVo.SpuItemAttrGroupVo>
	 **/
	List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("catalogId") Long catalogId, @Param("spuId") Long spuId);
}
