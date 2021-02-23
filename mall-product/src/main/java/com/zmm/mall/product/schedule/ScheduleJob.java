package com.zmm.mall.product.schedule;

import com.zmm.common.utils.Constant;
import com.zmm.common.utils.schedule.ScheduleJobEntity;
import com.zmm.common.utils.schedule.ScheduleJobLogEntity;
import com.zmm.mall.product.config.ApplicationContextHelper;
import com.zmm.mall.product.service.ScheduleJobLogService;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * @Name ScheduleJob
 * @Author 900045
 * @Created by 2020/12/24 0024
 */
public class ScheduleJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {

		ScheduleJobEntity scheduleJob = (ScheduleJobEntity) jobExecutionContext.getMergedJobDataMap()
				.get(Constant.JOB_PARAM_KEY);

		//获取spring bean
		ScheduleJobLogService scheduleJobLogService = (ScheduleJobLogService) ApplicationContextHelper.getBean("scheduleJobLogService");
		//数据库保存执行记录
		ScheduleJobLogEntity scheduleJobLogEntity = new ScheduleJobLogEntity();
		scheduleJobLogEntity.setJobId(scheduleJob.getJobId());
		scheduleJobLogEntity.setBeanName(scheduleJob.getClassName());
		scheduleJobLogEntity.setParams(scheduleJob.getParams());
		scheduleJobLogEntity.setCreateTime(new Date());

		//任务开始时间
		long startTime = System.currentTimeMillis();
		try {
			//执行任务
			logger.debug("任务准备执行，任务ID：" + scheduleJob.getJobId());

			Object target = ApplicationContextHelper.getBean(scheduleJob.getClassName());
			Method method = target.getClass().getDeclaredMethod("run", String.class);
			method.invoke(target, scheduleJob.getParams());

			//任务执行总时长
			long times = System.currentTimeMillis() - startTime;
			scheduleJobLogEntity.setTimes((int)times);
			//任务状态    0：成功    1：失败
			scheduleJobLogEntity.setStatus(0);

			logger.debug("任务执行完毕，任务ID：" + scheduleJob.getJobId() + "  总共耗时：" + times + "毫秒");
		} catch (Exception e) {
			logger.error("任务执行失败，任务ID：" + scheduleJob.getJobId(), e);

			//任务执行总时长
			long times = System.currentTimeMillis() - startTime;
			scheduleJobLogEntity.setTimes((int)times);

			//任务状态    0：成功    1：失败
			scheduleJobLogEntity.setStatus(1);
			scheduleJobLogEntity.setError(StringUtils.substring(e.toString(), 0, 2000));
		}finally {
			scheduleJobLogService.save(scheduleJobLogEntity);
		}
	}
}
