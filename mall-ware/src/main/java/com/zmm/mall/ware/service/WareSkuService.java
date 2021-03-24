package com.zmm.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.to.mq.OrderTo;
import com.zmm.common.to.mq.StockLockedTo;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.ware.entity.WareSkuEntity;
import com.zmm.mall.ware.vo.SkuHasStockVo;
import com.zmm.mall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 15:20:08
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    /**
     * 查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 采购成功 入库
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    void addStock(Long skuId, Long wareId, Integer skuNum);

	/**
	 *  检查每一个sku的库存
	 * @param skuIds
	 * @return
	 */
	List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

	/**
	 * 为某个订单锁定库存
	 * @description:
	 * @author: Administrator
	 * @date: 2021-03-16 20:09:07
	 * @param vo: 
	 * @return: boolean
	 **/
	boolean orderLockStock(WareSkuLockVo vo);

	/**
	 * 解锁库存
	 * @author: 900045
	 * @date: 2021-03-24 14:18:53
	 * @throws 
	 * @param to: 
	 * @return: void
	 **/
	void unLockStock(StockLockedTo to);

	/**
	 * 防止订单服务卡顿 导致订单状态消息一直改不了 库存消息优先到期.
	 * 查询订单状态为新建转态 什么都不做 就跳过了 ==> 卡顿的订单 永远都不能解锁库存
	 * 订单关闭解锁库存
	 * @author: 900045
	 * @date: 2021-03-24 15:16:28
	 * @throws 
	 * @param orderTo: 
	 * @return: void
	 **/
	void unLockStock(OrderTo orderTo);
}

