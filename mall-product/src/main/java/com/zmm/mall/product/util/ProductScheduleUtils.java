package com.zmm.mall.product.util;

import com.zmm.common.exception.RRException;
import com.zmm.common.utils.Constant;
import com.zmm.common.utils.schedule.MyScheduleUtils;
import com.zmm.common.utils.schedule.ScheduleJobEntity;
import com.zmm.mall.product.schedule.ScheduleJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

/**
 * @Name ProductScheduleUtils
 * @Author 900045
 * @Created by 2020/12/24 0024
 */
@Component
public class ProductScheduleUtils extends MyScheduleUtils {

	/**
	 * 重写 createScheduleJob 方法
	 * 创建定时任务
	 * @param scheduler
	 * @param object
	 */

	@Override
	public void createScheduleJob(Scheduler scheduler, Object object) {

		ScheduleJobEntity scheduleConfig= (ScheduleJobEntity)object;
		try {
			//构建job信息
			JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class).withIdentity(getJobKey(scheduleConfig.getJobId())).build();

			//表达式调度构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleConfig.getCron())
					.withMisfireHandlingInstructionDoNothing();

			//按新的cronExpression表达式构建一个新的trigger
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(scheduleConfig.getJobId())).withSchedule(scheduleBuilder).build();

			//放入参数，运行时的方法可以获取
			jobDetail.getJobDataMap().put(Constant.JOB_PARAM_KEY, scheduleConfig);

			scheduler.scheduleJob(jobDetail, trigger);

			//暂停任务
			if(scheduleConfig.getStatus() == Constant.ScheduleStatus.PAUSE.getValue()){
				pauseJob(scheduler, scheduleConfig.getJobId());
			}
		} catch (SchedulerException e) {
			throw new RRException("创建定时任务失败", e);
		}
	}


	/**
	 * 重写 updateScheduleJob 方法
	 * 更新定时任务
	 * @param scheduler
	 * @param object
	 */
	@Override
	public void updateScheduleJob(Scheduler scheduler, Object object) {
		ScheduleJobEntity scheduleConfig= (ScheduleJobEntity)object;
		try {
			TriggerKey triggerKey = getTriggerKey(scheduleConfig.getJobId());

			//表达式调度构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleConfig.getCron())
					.withMisfireHandlingInstructionDoNothing();

			CronTrigger trigger = getCronTrigger(scheduler, scheduleConfig.getJobId());

			//按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

			//参数
			trigger.getJobDataMap().put(Constant.JOB_PARAM_KEY, scheduleConfig);

			scheduler.rescheduleJob(triggerKey, trigger);

			//暂停任务
			if(scheduleConfig.getStatus() == Constant.ScheduleStatus.PAUSE.getValue()){
				pauseJob(scheduler, scheduleConfig.getJobId());
			}

		} catch (SchedulerException e) {
			throw new RRException("更新定时任务失败", e);
		}
	}
}
