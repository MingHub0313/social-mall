package com.zmm.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.product.entity.SpuInfoEntity;
import com.zmm.mall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

	/**
	 * 查询
	 * @param params
	 * @return
	 */
    PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存 SPU
	 * @param vo
	 */
	void saveSpuInfo(SpuSaveVo vo);

	/**
	 * 保存 SPU
	 * @param spuInfoEntity
	 */
	void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

	/**
	 * 分页查询
	 * @param params
	 * @return
	 */
	PageUtils queryPageByCondition(Map<String, Object> params);

	/**
	 * 商品上架
	 * @param spuId
	 */
	void up(Long spuId);

	/**
	 * 根据 skuId 查询 spu信息
	 * @description:
	 * @author: Administrator
	 * @date: 2021-03-11 22:00:50
	 * @param skuId: 
	 * @return: com.zmm.mall.product.entity.SpuInfoEntity
	 **/
    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

