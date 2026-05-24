package com.focela.platform.idempotent.config;

import com.focela.platform.redis.config.FocelaRedisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * Idempotency auto-configuration.
 */
@AutoConfiguration(after = FocelaRedisAutoConfiguration.class)
@SuppressWarnings("deprecation")
public class FocelaIdempotentAutoConfiguration extends FocelaIdempotentConfiguration {
}
