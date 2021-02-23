package com.zmm.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.mall.product.dao.ScheduleDao;
import com.zmm.common.utils.schedule.ScheduleJobEntity;
import com.zmm.mall.product.service.ScheduleService;
import org.springframework.stereotype.Service;


/**
 * @Name ScheduleServiceImpl
 * @Author 900045
 * @Created by 2020/12/24 0024
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleDao, ScheduleJobEntity> implements ScheduleService {

	@Override
	public void test1() {
		log.warn("=========== 测试一 开始执行 2s===================");
	}

	@Override
	public void test2() {
		log.warn("=========== [测试二] 开始执行 6s===================");
	}
}
