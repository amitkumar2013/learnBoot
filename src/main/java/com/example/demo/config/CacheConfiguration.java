package com.example.demo.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Using Jedis – a simple and powerful Redis client implementation.
 * spring.cache.type=redis along with @EnableCaching here and @Cacheable on methods.
 * Remember using @CachePut & @CacheEvict - on complementary methods either at API/Service level.
 *  
 * @author amit.30.kumar
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jedisConFactory = new JedisConnectionFactory();
		return jedisConFactory;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		return template;
	}
}
