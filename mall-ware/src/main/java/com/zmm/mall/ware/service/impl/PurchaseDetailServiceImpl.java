package com.zmm.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.mall.ware.dao.PurchaseDetailDao;
import com.zmm.mall.ware.entity.PurchaseDetailEntity;
import com.zmm.mall.ware.service.PurchaseDetailService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @Name PurchaseDetailServiceImpl
 * @Author 900045
 * @Created by 2020/9/4 0004
 */
@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {

		/**
		 * status: 0,//状态
		 *    wareId: 1,//仓库id
		 */

		QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<PurchaseDetailEntity>();

		String key = (String) params.get("key");
		if(!StringUtils.isEmpty(key)){
			//purchase_id  sku_id
			queryWrapper.and(w->{
				w.eq("purchase_id",key).or().eq("sku_id",key);
			});
		}

		String status = (String) params.get("status");
		if(!StringUtils.isEmpty(status)){
			//purchase_id  sku_id
			queryWrapper.eq("status",status);
		}

		String wareId = (String) params.get("wareId");
		if(!StringUtils.isEmpty(wareId)){
			//purchase_id  sku_id
			queryWrapper.eq("ware_id",wareId);
		}

		IPage<PurchaseDetailEntity> page = this.page(
				new Query<PurchaseDetailEntity>().getPage(params),
				queryWrapper
		);

		return new PageUtils(page);
	}

	@Override
	public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {

		List<PurchaseDetailEntity> purchaseId = this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));

		return purchaseId;
	}

}
