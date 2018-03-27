package org.midnightbsd.advisory.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

/**
 * @author Lucas Holt
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
    private final RedisConnectionFactory redisConnectionFactory;

    @Autowired
    public CacheConfig(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Bean(name = {"redisTemplate"})
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(factory);
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        template.setKeySerializer(template.getStringSerializer());
        template.setValueSerializer(jdkSerializationRedisSerializer);
        template.setDefaultSerializer(template.getStringSerializer());
        template.setHashKeySerializer(jdkSerializationRedisSerializer);
        template.setHashValueSerializer(jdkSerializationRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Override
    public CacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager(this.redisTemplate(this.redisConnectionFactory));
        redisCacheManager.setUsePrefix(true);
        return redisCacheManager;
    }
}