package com.zmm.mall.ware.dao;

import com.zmm.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

	/**
	 * 根据 指定的 skuId 查询 库存地址
	 * @description:
	 * @author: Administrator
	 * @date: 2021-03-16 20:22:56
	 * @param skuId: 
	 * @return: java.util.List<java.lang.Long>
	 **/
    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    /**
	 * 锁定库存
     * @description:
     * @author: Administrator
     * @date: 2021-03-16 20:37:28
     * @param skuId: 
     * @param wareId: 
     * @param num: 
     * @return: java.lang.Integer
     **/
	Integer lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);

	/**
	 * 解锁库存
	 * @author: 900045
	 * @date: 2021-03-24 13:58:16
	 * @throws 
	 * @param skuId: 
	 * @param wareId: 
	 * @param num:
	 * @return: void
	 **/
	void unLockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
	
}
