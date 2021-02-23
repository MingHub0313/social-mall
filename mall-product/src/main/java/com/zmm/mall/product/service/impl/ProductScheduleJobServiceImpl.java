package com.zmm.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.schedule.ScheduleJobEntity;
import com.zmm.mall.product.dao.ScheduleDao;
import com.zmm.mall.product.service.ProductScheduleJobService;
import com.zmm.mall.product.util.ProductScheduleUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Name ProductScheduleJobServiceImpl
 * @Author 900045
 * @Created by 2020/12/25 0025
 */
@Slf4j
@Service("productScheduleJobService")
public class ProductScheduleJobServiceImpl extends ServiceImpl<ScheduleDao, ScheduleJobEntity> implements ProductScheduleJobService {

	@Autowired
	private Scheduler scheduler;
	
	@Resource
	private ProductScheduleUtils productScheduleUtils;
	
	@Override
	public void run(Long[] jobIds) {
		log.error("参数传递jobIds----->{}", jobIds);
		for(Long jobId : jobIds){
			log.error("参数传递jobId------>{}", jobId);
			productScheduleUtils.run(scheduler, this.getById(jobId));
		}
	}
}
