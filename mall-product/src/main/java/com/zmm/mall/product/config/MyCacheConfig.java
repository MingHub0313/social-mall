package com.zmm.mall.product.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.zmm.common.utils.redis.RedisUtil;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Name MyCacheConfig
 * @Author 900045
 * @Created by 2020/10/20 0020
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class MyCacheConfig {

	/*@Autowired
	CacheProperties cacheProperties;*/

	/**
	 * 1.原来和配置文件绑定的配置类是这样的
	 * 		@ConfigurationProperties(prefix = "spring.cache")
	 * 		public class CacheProperties {
	 *
	 * 	2.让其生效 添加注解 @EnableConfigurationProperties
	 * @return
	 */

	@Bean
	RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties){

		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

		//config = config.entryTtl()

		config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

		config =
				config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
		// 将配置文件中的所有配置都生效

		CacheProperties.Redis redisProperties = cacheProperties.getRedis();

		if (redisProperties.getTimeToLive() != null) {
			config = config.entryTtl(redisProperties.getTimeToLive());
		}

		if (redisProperties.getKeyPrefix() != null) {
			config = config.prefixKeysWith(redisProperties.getKeyPrefix());
		}

		if (!redisProperties.isCacheNullValues()) {
			config = config.disableCachingNullValues();
		}

		if (!redisProperties.isUseKeyPrefix()) {
			config = config.disableKeyPrefix();
		}

		return config;
	}


	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		if (!(redisConnectionFactory instanceof LettuceConnectionFactory)) {
			throw new RuntimeException(
					"un_support redis connection factory! " + redisConnectionFactory);
		}
		LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) redisConnectionFactory;
		//  2.0.0配置方式，  2.1.7 之后无效了
		// lettuceConnectionFactory.setDatabase(database)
		// redisTemplate.setConnectionFactory(lettuceConnectionFactory)

		//2.1.7
		//可以直接用  spring.redis.database=6 配置

		//2.1.7之后要完全替换
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
				lettuceConnectionFactory.getHostName(), lettuceConnectionFactory.getPort());
		redisStandaloneConfiguration.setDatabase(7);
		redisStandaloneConfiguration.setPassword(lettuceConnectionFactory.getPassword());
		LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(
				redisStandaloneConfiguration, lettuceConnectionFactory.getClientConfiguration());
		//这句一定不能少
		connectionFactory.afterPropertiesSet();

		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
		//  使用JSON格式的序列化,保存
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		// 值 不乱码
		redisTemplate.setValueSerializer(fastJsonRedisSerializer);
		redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
		redisTemplate.setConnectionFactory(connectionFactory);
		return redisTemplate;

	}

	@Bean
	public RedisUtil<Object> redisUtil(RedisTemplate<String, Object> redisTemplate) {
		return new RedisUtil<>(redisTemplate);
	}
}
