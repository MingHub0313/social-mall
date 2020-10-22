package com.zmm.mall.ware.dao;

import com.zmm.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 15:20:08
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

	/**
	 * 采购成功入库
	 * @param skuId
	 * @param wareId
	 * @param skuNum
	 */
	void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

	/**
	 * 查询 库存总量
	 * @param skuId
	 * @return
	 */
	Long getSkuStock(Long skuId);
}
