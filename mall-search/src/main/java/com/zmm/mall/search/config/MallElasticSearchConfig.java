package com.zmm.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Name MallElasticSearchConfig
 * @Author 900045
 * @Created by 2020/9/17 0017
 */
@Configuration
public class MallElasticSearchConfig {

	public static final RequestOptions COMMON_OPTIONS;
	static {
		RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
		/** builder.addHeader("Authorization", "Bearer " + TOKEN)
		builder.setHttpAsyncResponseConsumerFactory(
				new HttpAsyncResponseConsumerFactory
						.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024))*/
		COMMON_OPTIONS = builder.build();
	}

	/**
	 * 1.导入依赖
	 * 2.编写配置 ---给容器中 注入一个 RestHighLevelClient
	 */

	@Bean
	public RestHighLevelClient esRestClient(){
		RestHighLevelClient client = new RestHighLevelClient(
				RestClient.builder(
						new HttpHost("47.101.132.45",9200,"http")));
		return client;
	}
}
