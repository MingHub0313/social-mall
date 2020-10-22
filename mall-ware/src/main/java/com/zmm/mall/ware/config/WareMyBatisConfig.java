package com.zmm.mall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Name WareMyBatisConfig
 * @Author 900045
 * @Created by 2020/9/4 0004
 */
@EnableTransactionManagement
@MapperScan("com.zmm.mall.ware.dao")
@Configuration
public class WareMyBatisConfig {

	/**
	 * 引入分页插件
	 * @return
	 */
	@Bean
	public PaginationInterceptor paginationInterceptor() {
		PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

		/**
		 * 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
		 * paginationInterceptor.setOverflow(true)
		 * 设置最大单页限制数量，默认 500 条，-1 不受限制
		 * paginationInterceptor.setLimit(1000)
		 *
		 */
		return paginationInterceptor;
	}
}
