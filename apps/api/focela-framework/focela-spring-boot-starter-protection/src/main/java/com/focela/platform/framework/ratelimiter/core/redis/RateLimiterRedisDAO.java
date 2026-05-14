package com.focela.platform.framework.ratelimiter.core.redis;

import lombok.AllArgsConstructor;
import org.redisson.api.*;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Rate limiter Redis DAO.
 */
@AllArgsConstructor
public class RateLimiterRedisDAO {

    /**
     * Rate limiter operation.
     *
     * KEY format: rate_limiter:%s // parameter is a uuid
     * VALUE format: String
     * Expiration: variable
     */
    private static final String RATE_LIMITER = "rate_limiter:%s";

    private final RedissonClient redissonClient;

    public Boolean tryAcquire(String key, int count, int time, TimeUnit timeUnit) {
        // 1. Get the RRateLimiter and configure the rate
        RRateLimiter rateLimiter = getRRateLimiter(key, count, time, timeUnit);
        // 2. Try to acquire one permit
        return rateLimiter.tryAcquire();
    }

    private static String formatKey(String key) {
        return String.format(RATE_LIMITER, key);
    }

    private RRateLimiter getRRateLimiter(String key, long count, int time, TimeUnit timeUnit) {
        String redisKey = formatKey(key);
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(redisKey);
        long rateInterval = timeUnit.toSeconds(time);
        Duration duration = Duration.ofSeconds(rateInterval);
        // 1. If it does not exist, configure the rate
        RateLimiterConfig config = rateLimiter.getConfig();
        if (config == null) {
            rateLimiter.trySetRate(RateType.OVERALL, count, duration);
            // Reason: see https://t.zsxq.com/lcR0W
            rateLimiter.expire(duration);
            return rateLimiter;
        }
        // 2. If it exists with the same config, return as-is
        if (config.getRateType() == RateType.OVERALL
                && Objects.equals(config.getRate(), count)
                && Objects.equals(config.getRateInterval(), TimeUnit.SECONDS.toMillis(rateInterval))) {
            return rateLimiter;
        }
        // 3. If it exists with a different config, reconfigure it
        rateLimiter.setRate(RateType.OVERALL, count, duration);
        // Reason: see https://t.zsxq.com/lcR0W
        rateLimiter.expire(duration);
        return rateLimiter;
    }

}
