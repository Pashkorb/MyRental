package com.example.rental.config;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
    @Bean
    public RedisTemplate<String, Object> redisTemplate (RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // Используем String для ключей и JSON для значений
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }

    public static class RedisCacheErrorHandler implements CacheErrorHandler {
        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
            System.err.println("Redis GET error: " + exception.getMessage());
        }

        @Override
        public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
            System.err.println("Redis PUT error: " + exception.getMessage());
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
            System.err.println("Redis EVICT error: " + exception.getMessage());
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, Cache cache) {
            System.err.println("Redis CLEAR error: " + exception.getMessage());
        }
    }
}
