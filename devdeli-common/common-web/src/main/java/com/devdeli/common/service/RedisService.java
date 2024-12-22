package com.devdeli.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void putValue(String key, String value, Duration lifeTime){
        redisTemplate.opsForValue()
                .set(key, value, lifeTime);
    }

    public String getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }
}
