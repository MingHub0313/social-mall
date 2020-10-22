package com.zmm.mall.search.service;

import com.zmm.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Name ProductSaveService
 * @Author 900045
 * @Created by 2020/9/18 0018
 */
public interface ProductSaveService {

	/**
	 * 上级商品
	 * @param skuEsModelList
	 * @return
	 * @throws IOException
	 */
	boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException;
}
