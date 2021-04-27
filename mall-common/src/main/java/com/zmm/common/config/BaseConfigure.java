package com.zmm.common.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.zmm.common.utils.redis.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Description:
 * @Name BaseConfigure
 * @Author Administrator
 * @Date By 2021-03-04 20:54:31
 */
public class BaseConfigure {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        
        /**
        if (!(redisConnectionFactory instanceof LettuceConnectionFactory)) {
            throw new RuntimeException(
                    "un_support redis connection factory! " + redisConnectionFactory);
        }
         */
        JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) redisConnectionFactory;
        //  2.0.0配置方式，  2.1.7 之后无效了
        // lettuceConnectionFactory.setDatabase(database)
        // redisTemplate.setConnectionFactory(lettuceConnectionFactory)

        //2.1.7
        //可以直接用  spring.redis.database=6 配置

        //2.1.7之后要完全替换
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                jedisConnectionFactory.getHostName(), jedisConnectionFactory.getPort());
        redisStandaloneConfiguration.setDatabase(7);
        redisStandaloneConfiguration.setPassword(jedisConnectionFactory.getPassword());
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(
                redisStandaloneConfiguration, jedisConnectionFactory.getClientConfiguration());
        //这句一定不能少
        connectionFactory.afterPropertiesSet();

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
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
