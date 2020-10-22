package com.zmm.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.ware.entity.PurchaseEntity;
import com.zmm.mall.ware.vo.MergeVo;
import com.zmm.mall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * @Name PurchaseService
 * @Author 900045
 * @Created by 2020/9/4 0004
 */
public interface PurchaseService extends IService<PurchaseEntity> {


	/**
	 * 查询
	 * @param params
	 * @return
	 */
	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 未领取
	 * @param params
	 * @return
	 */
	PageUtils queryPageUnReceivePurchase(Map<String, Object> params);


	/**
	 * 合并
	 * @param mergeVo
	 */
	void mergePurchase(MergeVo mergeVo);


	/**
	 * 领取采购单
	 * @param ids
	 */
	void received(List<Long> ids);


	/**
	 * 完成采购单
	 * @param doneVo
	 */
	void done(PurchaseDoneVo doneVo);
}
