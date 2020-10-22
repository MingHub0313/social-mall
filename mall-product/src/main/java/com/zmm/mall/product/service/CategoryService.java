package com.zmm.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.product.entity.CategoryEntity;
import com.zmm.mall.product.vo.Catalog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
public interface CategoryService extends IService<CategoryEntity> {

	/**
	 * 查询
	 * @param params
	 * @return
	 */
    PageUtils queryPage(Map<String, Object> params);

	/**
	 * list 集合 组成父子分类
	 * @return
	 */
	List<CategoryEntity> listWithTree();

	/**
	 * 删除分类
	 * @param asList
	 */
	void removeMenuByIds(List<Long> asList);

	/**
	 * 查询所有路径 [一级/二级/三级]
	 * @param cateId
	 * @return
	 */
	Long[] findCateLogPath(Long cateId);

	/**
	 * 级联更新
	 * @param category
	 */
	void updateCascade(CategoryEntity category);

	/**
	 * 询出所有的一级分类
	 * @return
	 */
	List<CategoryEntity> getLevel1Categories();

	/**
	 * 查出所有分类
	 * @return
	 */
	Map<String,List<Catalog2Vo>> getCatalogJson();
}

