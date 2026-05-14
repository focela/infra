package com.focela.platform.framework.idempotent.core.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Idempotent Redis DAO.
 */
@AllArgsConstructor
public class IdempotentRedisDAO {

    /**
     * Idempotent operation.
     *
     * KEY format: idempotent:%s // parameter is a uuid
     * VALUE format: String
     * Expiration: variable
     */
    private static final String IDEMPOTENT = "idempotent:%s";

    private final StringRedisTemplate redisTemplate;

    public Boolean setIfAbsent(String key, long timeout, TimeUnit timeUnit) {
        String redisKey = formatKey(key);
        return redisTemplate.opsForValue().setIfAbsent(redisKey, "", timeout, timeUnit);
    }

    public void delete(String key) {
        String redisKey = formatKey(key);
        redisTemplate.delete(redisKey);
    }

    private static String formatKey(String key) {
        return String.format(IDEMPOTENT, key);
    }

}
