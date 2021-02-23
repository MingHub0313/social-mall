package com.zmm.common.utils.schedule;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Name ScheduleJobEntity
 * @Author 900045
 * @Created by 2020/12/24 0024
 */
@Data
@TableName("schedule_setting")
public class ScheduleJobEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId
	private Long jobId;

	private String jobName;

	private String className;

	private String method;

	private String params;

	private String cron;

	private Integer status;
}
