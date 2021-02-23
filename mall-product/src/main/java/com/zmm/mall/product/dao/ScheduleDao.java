package com.zmm.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmm.common.utils.schedule.ScheduleJobEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Name ScheduleDao
 * @Author 900045
 * @Created by 2020/12/24 0024
 */
@Mapper
public interface ScheduleDao extends BaseMapper<ScheduleJobEntity> {

	/**
	 * 获取全部的定时任务
	 * @return
	 */
	List<ScheduleJobEntity> getScheduleList();
}
