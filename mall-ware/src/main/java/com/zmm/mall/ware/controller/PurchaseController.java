package com.zmm.mall.ware.controller;

import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.R;
import com.zmm.mall.ware.entity.PurchaseEntity;
import com.zmm.mall.ware.service.PurchaseService;
import com.zmm.mall.ware.vo.MergeVo;
import com.zmm.mall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Name PurchaseController
 * @Author 900045
 * @Created by 2020/9/4 0004
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {

	@Autowired
	private PurchaseService purchaseService;

	/**
	 * 完成采购
	 * @param doneVo
	 * @return
	 */
	@PostMapping("/done")
	public R finish(@RequestBody PurchaseDoneVo doneVo){

		purchaseService.done(doneVo);

		return R.ok();
	}

	/**
	 * 领取采购单 -- 外部接口
	 * @return
	 */
	@PostMapping("/received")
	public R received(@RequestBody List<Long> ids){

		purchaseService.received(ids);

		return R.ok();
	}

	/**
	 * 合并采购单
	 * @param mergeVo
	 * @return
	 */
	@PostMapping("/merge")
	public R merge(@RequestBody MergeVo mergeVo){

		purchaseService.mergePurchase(mergeVo);
		return R.ok();
	}

	@RequestMapping("/unreceive/list")
	public R unReceiveList(@RequestParam Map<String, Object> params){
		PageUtils page = purchaseService.queryPageUnReceivePurchase(params);

		return R.ok().put("page", page);
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = purchaseService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

		return R.ok().put("purchase", purchase);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody PurchaseEntity purchase){
		purchase.setUpdateTime(new Date());
		purchase.setCreateTime(new Date());
		purchaseService.save(purchase);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}
}
