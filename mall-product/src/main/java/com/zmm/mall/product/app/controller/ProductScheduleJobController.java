package com.zmm.mall.product.app.controller;

import com.zmm.common.utils.R;
import com.zmm.mall.product.service.ProductScheduleJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Name ProductScheduleJobController
 * @Author 900045
 * @Created by 2020/12/25 0025
 */
@RestController
@RequestMapping("product/schedule/job")
public class ProductScheduleJobController {

	@Autowired
	private ProductScheduleJobService productScheduleJobService;

	/**
	 * 立即执行任务
	 */
	@RequestMapping("/run")
	public R run(@RequestBody Long[] jobIds){
		productScheduleJobService.run(jobIds);
		return R.ok();
	}
}
