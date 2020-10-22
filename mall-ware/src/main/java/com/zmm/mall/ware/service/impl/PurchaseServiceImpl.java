package com.zmm.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.constant.WareConstant;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.mall.ware.dao.PurchaseDao;
import com.zmm.mall.ware.entity.PurchaseDetailEntity;
import com.zmm.mall.ware.entity.PurchaseEntity;
import com.zmm.mall.ware.service.PurchaseDetailService;
import com.zmm.mall.ware.service.PurchaseService;
import com.zmm.mall.ware.service.WareSkuService;
import com.zmm.mall.ware.vo.MergeVo;
import com.zmm.mall.ware.vo.PurchaseDoneVo;
import com.zmm.mall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Name PurchaseServiceImpl
 * @Author 900045
 * @Created by 2020/9/4 0004
 */
@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

	@Autowired
	PurchaseDetailService detailService;

	@Autowired
	WareSkuService wareSkuService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<PurchaseEntity> page = this.page(
				new Query<PurchaseEntity>().getPage(params),
				new QueryWrapper<PurchaseEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPageUnReceivePurchase(Map<String, Object> params) {
		IPage<PurchaseEntity> page = this.page(
				new Query<PurchaseEntity>().getPage(params),
				new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
		);

		return new PageUtils(page);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void mergePurchase(MergeVo mergeVo) {
		Long purchaseId = mergeVo.getPurchaseId();
		if(purchaseId == null){
			//1、新建一个
			PurchaseEntity purchaseEntity = new PurchaseEntity();

			purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
			purchaseEntity.setCreateTime(new Date());
			purchaseEntity.setUpdateTime(new Date());
			this.save(purchaseEntity);
			purchaseId = purchaseEntity.getId();
		}

		//TODO 确认采购单状态是0,1才可以合并

		List<Long> items = mergeVo.getItems();
		Long finalPurchaseId = purchaseId;
		List<PurchaseDetailEntity> collect = items.stream().map(i -> {
			PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();

			detailEntity.setId(i);
			detailEntity.setPurchaseId(finalPurchaseId);
			detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
			return detailEntity;
		}).collect(Collectors.toList());


		detailService.updateBatchById(collect);

		/**
		 * 修改采购单的 时间
		 */
		PurchaseEntity purchaseEntity = new PurchaseEntity();
		purchaseEntity.setId(purchaseId);
		purchaseEntity.setUpdateTime(new Date());
		this.updateById(purchaseEntity);
	}

	/**
	 *
	 * @param ids 采购单id
	 */
	@Override
	public void received(List<Long> ids) {
		//1、确认当前采购单是新建或者已分配状态
		List<PurchaseEntity> collect = ids.stream().map(id -> {
			PurchaseEntity byId = this.getById(id);
			return byId;
		}).filter(item -> {
			if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
					item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
				return true;
			}
			return false;
		}).map(item->{
			item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
			item.setUpdateTime(new Date());
			return item;
		}).collect(Collectors.toList());

		//2、改变采购单的状态
		this.updateBatchById(collect);



		//3、改变采购项的状态
		collect.forEach((item)->{
			List<PurchaseDetailEntity> entities = detailService.listDetailByPurchaseId(item.getId());
			List<PurchaseDetailEntity> detailEntities = entities.stream().map(entity -> {
				PurchaseDetailEntity entity1 = new PurchaseDetailEntity();
				entity1.setId(entity.getId());
				entity1.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
				return entity1;
			}).collect(Collectors.toList());
			detailService.updateBatchById(detailEntities);
		});
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void done(PurchaseDoneVo doneVo) {

		Long id = doneVo.getId();


		//2、改变采购项的状态
		Boolean flag = true;
		List<PurchaseItemDoneVo> items = doneVo.getItems();

		List<PurchaseDetailEntity> updates = new ArrayList<>();
		for (PurchaseItemDoneVo item : items) {
			PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
			if(item.getStatus() == WareConstant.PurchaseDetailStatusEnum.UNUSUAL.getCode()){
				flag = false;
				detailEntity.setStatus(item.getStatus());
			}else{
				detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
				////3、将成功采购的进行入库
				PurchaseDetailEntity entity = detailService.getById(item.getItemId());
				wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());

			}
			detailEntity.setId(item.getItemId());
			updates.add(detailEntity);
		}

		detailService.updateBatchById(updates);

		//1、改变采购单状态
		PurchaseEntity purchaseEntity = new PurchaseEntity();
		purchaseEntity.setId(id);
		purchaseEntity.setStatus(flag?WareConstant.PurchaseStatusEnum.FINISH.getCode():WareConstant.PurchaseStatusEnum.UNUSUAL.getCode());
		purchaseEntity.setUpdateTime(new Date());
		this.updateById(purchaseEntity);

	}
}
