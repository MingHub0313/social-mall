package com.zmm.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.product.entity.AttrGroupEntity;
import com.zmm.mall.product.vo.AttrGroupWithAttrsVo;
import com.zmm.mall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

	/**
	 * 查询
	 * @param params
	 * @return
	 */
    PageUtils queryPage(Map<String, Object> params);


	/**
	 * 查询
	 * @param params
	 * @param cateId
	 * @return
	 */
	PageUtils queryPage(Map<String, Object> params, Long cateId);


	/**
	 * 根据分类Id 查询 所有的分组以及组内的所有属性
	 * @param cateLogId
	 * @return
	 */
	List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCateLogId(Long cateLogId);

	/**
	 * 根据 spuId 获取所有属性分组以及分组里面所有属性对应的值
	 * @author: 900045
	 * @date: 2021-02-23 16:13:30
	 * @throws 
	 * @param catalogId: 三级分类id
	 * @param spuId: spuId
	 * @return: java.util.List<com.zmm.mall.product.vo.SkuItemVo.SpuItemAttrGroupVo>
	 **/
	List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long catalogId, Long spuId);
}

