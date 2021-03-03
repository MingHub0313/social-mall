package com.zmm.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.zmm.common.to.es.SkuEsModel;
import com.zmm.mall.search.config.MallElasticSearchConfig;
import com.zmm.mall.search.constant.EsConstant;
import com.zmm.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Name ProductSaveServiceImpl
 * @Author 900045
 * @Created by 2020/9/18 0018
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {


	@Autowired
	private RestHighLevelClient esRestClient;

	@Override
	public boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException {

		//保存 es
		// 1.给es 建立索引 product 建立好映射关系

		// 2.给es中保存这些数据
		// BulkRequest bulkRequest, RequestOptions options
		BulkRequest bulkRequest = new BulkRequest();
		for (SkuEsModel skuEsModel:skuEsModelList) {
			// 1.构造保存请求
			IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
			indexRequest.id(skuEsModel.getSkuId().toString());
			String jsonString = JSON.toJSONString(skuEsModel);
			indexRequest.source(jsonString, XContentType.JSON);
			bulkRequest.add(indexRequest);
		}
		BulkResponse bulk = esRestClient.bulk(bulkRequest, MallElasticSearchConfig.COMMON_OPTIONS);

		// 是否存在错误 TODO  没有错误为 false 有错误为true
		boolean hasFailures = bulk.hasFailures();

		List<String> stringList = Arrays.stream(bulk.getItems()).map(item -> {
			return item.getId();
		}).collect(Collectors.toList());
		log.error("商品上架完成-->{},返回数据:{}",stringList,bulk.toString());
		return !hasFailures;
	}
}
