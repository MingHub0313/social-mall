package com.zmm.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * @Name PurchaseDetailService
 * @Author 900045
 * @Created by 2020/9/4 0004
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

	/**
	 * 查询
	 * @param params
	 * @return
	 */
	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 按照采购单Id 找到采购项 然后再更新状态
	 * @param id
	 * @return
	 */
	List<PurchaseDetailEntity> listDetailByPurchaseId(Long id);
}
