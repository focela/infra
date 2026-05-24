package com.focela.platform.ratelimiter.config;

import com.focela.platform.redis.config.FocelaRedisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * Rate limiter auto-configuration.
 */
@AutoConfiguration(after = FocelaRedisAutoConfiguration.class)
@SuppressWarnings("deprecation")
public class FocelaRateLimiterAutoConfiguration extends FocelaRateLimiterConfiguration {
}
