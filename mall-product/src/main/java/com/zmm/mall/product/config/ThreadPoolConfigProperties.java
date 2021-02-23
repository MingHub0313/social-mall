package com.zmm.mall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 900045
 * @description:
 * @name ThreadPoolConfigProperties
 * @date By 2021-02-23 17:24:28
 */
@ConfigurationProperties(prefix = "mall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
	
	
	private Integer coreSize;
	
	private Integer maxSize;
	
	private Integer keepAliveTime;
}
