package com.zmm.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.product.entity.AttrEntity;
import com.zmm.mall.product.vo.AttrGroupRelationVo;
import com.zmm.mall.product.vo.AttrRespVo;
import com.zmm.mall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
public interface AttrService extends IService<AttrEntity> {

	/**
	 * 查询
	 * @param params
	 * @return
	 */
    PageUtils queryPage(Map<String, Object> params);


	/**
	 * 保存对象
	 * @param attrVo
	 */
	void saveAttr(AttrVo attrVo);

	/**
	 * 查询列表
	 * @param params
	 * @param cateId
	 * @param attrType
	 * @return
	 */
	PageUtils queryBaseAttrPage(Map<String, Object> params, Long cateId, String attrType);

	/**
	 * 查询
	 * @param attrId
	 * @return
	 */
	AttrRespVo getAttrInfo(Long attrId);

	/**
	 * 更新
	 * @param attrVo
	 */
	void updateAttr(AttrVo attrVo);

	/**
	 * 根据分组Id 查询 组内关联的所有属性
	 * @param attrGroupId
	 * @return
	 */
	List<AttrEntity> getRelationAttr(Long attrGroupId);

	/**
	 * 删除关联关系
	 * @param attrGroupRelationVos
	 */
	void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVos);

	/**
	 * 获取当前分组没有绑定的所有属性
	 * @param params
	 * @param attrGroupId
	 * @return
	 */
	PageUtils getRelationNoAttr(Map<String, Object> params, Long attrGroupId);

	/**
	 * 在指定的所有属性集合中挑出检索信息
	 * @param attrIdList
	 * @return
	 */
	List<Long> selectSearchAttrs(List<Long> attrIdList);
}

