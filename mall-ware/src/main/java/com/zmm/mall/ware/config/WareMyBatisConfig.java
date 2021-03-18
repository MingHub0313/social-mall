package com.zmm.mall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @Name WareMyBatisConfig
 * @Author 900045
 * @Created by 2020/9/4 0004
 */
@EnableTransactionManagement
@MapperScan("com.zmm.mall.ware.dao")
@Configuration
public class WareMyBatisConfig {

	@Resource
	private DataSourceProperties dataSourceProperties;

	@Bean
	public DataSource dataSource(DataSourceProperties dataSourceProperties){
		// properties.initializeDataSourceBuilder().type(type).build();
		HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
		if (StringUtils.hasText(dataSourceProperties.getName())){
			dataSource.setPoolName(dataSourceProperties.getName());
		}
		return new DataSourceProxy(dataSource);
	}


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
