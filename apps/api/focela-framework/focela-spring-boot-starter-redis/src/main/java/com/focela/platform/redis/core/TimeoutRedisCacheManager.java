package com.focela.platform.redis.core;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;

/**
 * {@link RedisCacheManager} implementation that supports custom expiration time.
 *
 * When {@link Cacheable#cacheNames()} is in the format "key#ttl", the value after # is the expiration time.
 * The unit is the trailing letter (supported: d days, h hours, m minutes, s seconds); defaults to seconds.
 */
public class TimeoutRedisCacheManager extends RedisCacheManager {

    private static final String SPLIT = "#";

    public TimeoutRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        if (StrUtil.isEmpty(name)) {
            return super.createRedisCache(name, cacheConfig);
        }
        // If split by # does not produce exactly 2 segments, custom expiration is not used.
        String[] names = StrUtil.splitToArray(name, SPLIT);
        if (names.length != 2) {
            return super.createRedisCache(name, cacheConfig);
        }

        // Core: implement custom expiration by overriding cacheConfig.entryTtl.
        if (cacheConfig != null) {
            // Strip the colon after # (and everything beyond) so it doesn't interfere with parsing.
            String ttlStr = StrUtil.subBefore(names[1], StrUtil.COLON, false); // get the ttlStr time portion
            names[1] = StrUtil.subAfter(names[1], ttlStr, false); // strip the ttlStr time portion
            // Parse the duration
            Duration duration = parseDuration(ttlStr);
            cacheConfig = cacheConfig.entryTtl(duration);
        }

        // Build the RedisCache, omitting the ttlStr portion from the name.
        return super.createRedisCache(names[0] + names[1], cacheConfig);
    }

    /**
     * Parse the expiration time Duration.
     *
     * @param ttlStr expiration time string
     * @return Duration
     */
    private Duration parseDuration(String ttlStr) {
        String timeUnit = StrUtil.subSuf(ttlStr, -1);
        switch (timeUnit) {
            case "d":
                return Duration.ofDays(removeDurationSuffix(ttlStr));
            case "h":
                return Duration.ofHours(removeDurationSuffix(ttlStr));
            case "m":
                return Duration.ofMinutes(removeDurationSuffix(ttlStr));
            case "s":
                return Duration.ofSeconds(removeDurationSuffix(ttlStr));
            default:
                return Duration.ofSeconds(Long.parseLong(ttlStr));
        }
    }

    /**
     * Strip the trailing unit suffix and return the numeric value.
     *
     * @param ttlStr expiration time string
     * @return numeric time value
     */
    private Long removeDurationSuffix(String ttlStr) {
        return NumberUtil.parseLong(StrUtil.sub(ttlStr, 0, ttlStr.length() - 1));
    }

}
