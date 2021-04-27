package com.zmm.mall.product.config;

import com.zmm.common.utils.schedule.ScheduleJobEntity;
import com.zmm.mall.product.dao.ScheduleDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Name ScheduleSettingConfig
 * @Author 900045
 * @Created by 2020/12/24 0024
 */
@Slf4j
@Component
public class ScheduleSettingConfig implements SchedulingConfigurer {

	@Resource
	private ScheduleDao scheduleDao;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// 获取所有任务
		//List<ScheduleJobEntity> scheduleList = scheduleDao.getScheduleList()
		List<ScheduleJobEntity> scheduleList = new ArrayList<>();
		log.error("数据库的集合size----->{}",scheduleList.size());
		if (!CollectionUtils.isEmpty(scheduleList)) {
      		for (ScheduleJobEntity s : scheduleList) {
        		taskRegistrar.addTriggerTask(getRunnable(s), getTrigger(s));
      		}
		}
	}

	/**
	 * runnable
	 * @param scheduleConfig
	 * @return
	 */
	private Runnable getRunnable(ScheduleJobEntity scheduleConfig){
		return new Runnable() {
			@Override
			public void run() {
				Class<?> clazz;
				try {
					clazz = Class.forName(scheduleConfig.getClassName());
					String className = lowerFirstCase(clazz.getSimpleName());
					Object bean = (Object) ApplicationContextHelper.getBean(className);
					Method method = ReflectionUtils.findMethod(bean.getClass(), scheduleConfig.getMethod());
					ReflectionUtils.invokeMethod(method, bean);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		};
	}

	/**
	 * 转换首字母小写
	 *
	 * @param str
	 * @return
	 */
	public static String lowerFirstCase(String str) {
		char[] chars = str.toCharArray();
		chars[0] += 32;
		return String.valueOf(chars);
	}

	/**
	 * Trigger
	 * @param scheduleConfig
	 * @return
	 */
	private Trigger getTrigger(ScheduleJobEntity scheduleConfig){
		return new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				CronTrigger trigger = new CronTrigger(scheduleConfig.getCron());
				Date nextExec = trigger.nextExecutionTime(triggerContext);
				return nextExec;
			}
		};

	}
}
