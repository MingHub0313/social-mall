package com.zmm.common.utils.schedule;

import com.zmm.common.exception.RRException;
import org.quartz.CronTrigger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

/**
 * @Name MyScheduleUtils 定时任务工具类
 * @Author 900045
 * @Created by 2020/12/24 0024
 */
public class MyScheduleUtils {

	private final static String JOB_NAME = "TASK_";

	/**
	 * 获取触发器key
	 */
	public TriggerKey getTriggerKey(Long jobId) {
		return TriggerKey.triggerKey(JOB_NAME + jobId);
	}

	/**
	 * 获取jobKey
	 */
	public JobKey getJobKey(Long jobId) {
		return JobKey.jobKey(JOB_NAME + jobId);
	}

	/**
	 * 获取表达式触发器
	 */
	public CronTrigger getCronTrigger(Scheduler scheduler, Long jobId) {
		try {
			return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
		} catch (SchedulerException e) {
			throw new RRException("获取定时任务CronTrigger出现异常", e);
		}
	}

	/**
	 * 创建定时任务
	 */
	public void createScheduleJob(Scheduler scheduler, Object object) { }

	/**
	 * 更新定时任务
	 */
	public void updateScheduleJob(Scheduler scheduler, Object object) { }

	/**
	 * 立即执行任务
	 */
	public void run(Scheduler scheduler, Object object) { }

	/**
	 * 暂停任务
	 */
	public void pauseJob(Scheduler scheduler, Long jobId) {
		try {
			scheduler.pauseJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			throw new RRException("暂停定时任务失败", e);
		}
	}

	/**
	 * 恢复任务
	 */
	public void resumeJob(Scheduler scheduler, Long jobId) {
		try {
			scheduler.resumeJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			throw new RRException("暂停定时任务失败", e);
		}
	}

	/**
	 * 删除定时任务
	 */
	public void deleteScheduleJob(Scheduler scheduler, Long jobId) {
		try {
			scheduler.deleteJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			throw new RRException("删除定时任务失败", e);
		}
	}
}
