package com.zmm.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.schedule.ScheduleJobLogEntity;
import com.zmm.mall.product.dao.ScheduleJobLogDao;
import com.zmm.mall.product.service.ScheduleJobLogService;
import org.springframework.stereotype.Service;

/**
 * @Name ScheduleJobLogServiceImpl
 * @Author 900045
 * @Created by 2020/12/24 0024
 */
@Service("scheduleJobLogService")
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogDao, ScheduleJobLogEntity> implements ScheduleJobLogService {

}
