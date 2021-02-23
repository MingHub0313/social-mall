package com.zmm.mall.search.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
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

		HttpHost host = new HttpHost("47.116.113.35",9200, HttpHost.DEFAULT_SCHEME_NAME);

		RestClientBuilder builder = RestClient.builder(host);

		/**
		 * 通过密码来连接 es
		 */

		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

		credentialsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("elastic", "19961001zmm"));

		builder.setHttpClientConfigCallback(f->f.setDefaultCredentialsProvider(credentialsProvider));

		RestHighLevelClient client = new RestHighLevelClient(builder);

		return client;
	}
}
