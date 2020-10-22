package com.zmm.mall.product.feign;

import com.zmm.common.to.SkuHasStockVo;
import com.zmm.common.to.SpuBoundTo;
import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Name WareFeignService
 * @Author 900045
 * @Created by 2020/9/18 0018
 */
@FeignClient("mall-ware")
public interface WareFeignService {

	/**
	 * 远程调用 1.R 可以添加泛型
	 * 2. 直接返回我们想要的结果
	 * 3.自己封装解析结果
	 * @param skuIds
	 * @return
	 */
	@PostMapping("/ware/waresku/hasstock")
	R getSkuHasStock(@RequestBody List<Long> skuIds);
}
