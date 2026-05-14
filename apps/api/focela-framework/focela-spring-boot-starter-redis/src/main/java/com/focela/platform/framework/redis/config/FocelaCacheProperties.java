package com.focela.platform.framework.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Cache configuration properties.
 */
@ConfigurationProperties("focela.cache")
@Data
@Validated
public class FocelaCacheProperties {

    /**
     * Default value for {@link #redisScanBatchSize}.
     */
    private static final Integer REDIS_SCAN_BATCH_SIZE_DEFAULT = 30;

    /**
     * Number of entries returned by a single redis scan call.
     */
    private Integer redisScanBatchSize = REDIS_SCAN_BATCH_SIZE_DEFAULT;

}
