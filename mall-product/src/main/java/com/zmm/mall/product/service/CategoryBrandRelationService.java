package com.zmm.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.product.entity.BrandEntity;
import com.zmm.mall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

	/**
	 * 查询
	 * @param params
	 * @return
	 */
    PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存对象
	 * @param categoryBrandRelation
	 */
	void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

	/**
	 * 由于品牌表名称修改 更新关联表数据
	 * @param brandId
	 * @param name
	 */
	void updateBrand(Long brandId, String name);

	/**
	 * 级联更新分类
	 * @param catId
	 * @param name
	 */
	void updateCategory(Long catId, String name);

	/**
	 * 查询指定分类中的所有品牌信息
	 * @param catId
	 * @return
	 */
	List<BrandEntity> getBrandsByCatId(Long catId);
}

