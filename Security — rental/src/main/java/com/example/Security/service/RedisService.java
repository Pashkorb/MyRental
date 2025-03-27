package com.example.Security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Сохранить код сброса пароля с TTL (время жизни)
    public void saveResetCode(String email, Integer  code, long timeoutMinutes) {
        String key = "reset_password:" + email; // Формат ключа (пример: reset_password:user@mail.com)
        redisTemplate.opsForValue().set(key, code, timeoutMinutes, TimeUnit.MINUTES);
    }

    // Получить код сброса пароля
    public Integer getResetCode(String email) {
        String key = "reset_password:" + email;
        Integer s= (Integer) redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);
        return s;
    }


}