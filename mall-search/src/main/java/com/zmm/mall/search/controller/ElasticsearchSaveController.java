package com.zmm.mall.search.controller;

import com.zmm.common.exception.BizCodeEnums;
import com.zmm.common.to.es.SkuEsModel;
import com.zmm.common.utils.R;
import com.zmm.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @Name ElasticsearchSaveController
 * @Author 900045
 * @Created by 2020/9/18 0018
 */
@Slf4j
@RestController
@RequestMapping("/search/save")
public class ElasticsearchSaveController {

	@Autowired
	private ProductSaveService productSaveService;

	/**
	 * 上架商品
	 * @param skuEsModelList
	 * @return
	 */
	@PostMapping("/product")
	public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList){
		boolean b = false;
		try {
			b = productSaveService.productStatusUp(skuEsModelList);
		} catch (IOException e) {
			log.error("ElasticsearchSaveController 商品上架失败{}",e);
			return R.error(BizCodeEnums.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnums.PRODUCT_UP_EXCEPTION.getMsg());
		}
		if (b){
			return R.ok();
		} else {
			return R.error(BizCodeEnums.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnums.PRODUCT_UP_EXCEPTION.getMsg());
		}
	}
}
