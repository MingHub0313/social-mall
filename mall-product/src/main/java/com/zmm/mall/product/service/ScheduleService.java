package com.zmm.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.schedule.ScheduleJobEntity;

/**
 * @Name ScheduleService
 * @Author 900045
 * @Created by 2020/12/24 0024
 */
public interface ScheduleService extends IService<ScheduleJobEntity> {


	/**
	 * 测试 方法一
	 */
	void test1();

	/**
	 * 测试 方法二
	 */
	void test2();
}
