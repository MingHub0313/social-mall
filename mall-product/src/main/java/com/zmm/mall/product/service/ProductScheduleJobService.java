package com.zmm.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.schedule.ScheduleJobEntity;

/**
 * @Name ProductScheduleJobService
 * @Author 900045
 * @Created by 2020/12/25 0025
 */
public interface ProductScheduleJobService extends IService<ScheduleJobEntity> {

	/**
	 * 立即执行
	 * @param jobIds
	 */
	void run(Long[] jobIds);
}
