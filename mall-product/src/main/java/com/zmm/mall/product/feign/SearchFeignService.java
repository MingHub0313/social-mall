package com.zmm.mall.product.feign;

import com.zmm.common.to.es.SkuEsModel;
import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Name SearchFeignService
 * @Author 900045
 * @Created by 2020/9/18 0018
 */
@FeignClient("mall-search")
public interface SearchFeignService {

	/**
	 * 远程服务调用
	 * @param skuEsModelList
	 * @return
	 */
	@PostMapping("/search/save/product")
	public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList);
}
