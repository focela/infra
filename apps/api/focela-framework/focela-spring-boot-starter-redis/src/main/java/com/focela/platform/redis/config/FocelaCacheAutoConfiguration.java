package com.focela.platform.redis.config;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.redis.core.TimeoutRedisCacheManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static com.focela.platform.redis.config.FocelaRedisAutoConfiguration.buildRedisSerializer;

/**
 * Cache configuration class, backed by Redis.
 */
@AutoConfiguration
@EnableConfigurationProperties({CacheProperties.class, FocelaCacheProperties.class})
@EnableCaching
public class FocelaCacheAutoConfiguration {

    /**
     * RedisCacheConfiguration Bean
     * <p>
     * Adapted from the createConfiguration method in org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration.
     */
    @Bean
    @Primary
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // Use a single colon ":" instead of the double "::" to avoid extra whitespace in Redis Desktop Manager.
        // Details: https://blog.csdn.net/chuixue24/article/details/103928965
        // Keep the single-colon prefix stable for existing Redis keys.
        config = config.computePrefixWith(cacheName -> {
            String keyPrefix = cacheProperties.getRedis().getKeyPrefix();
            if (StringUtils.hasText(keyPrefix)) {
                keyPrefix = keyPrefix.lastIndexOf(StrUtil.COLON) == -1 ? keyPrefix + StrUtil.COLON : keyPrefix;
                return keyPrefix + cacheName + StrUtil.COLON;
            }
            return cacheName + StrUtil.COLON;
        });
        // Use JSON value serialization
        config = config.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(buildRedisSerializer()));

        // Apply CacheProperties.Redis settings
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisTemplate<String, Object> redisTemplate,
                                               RedisCacheConfiguration redisCacheConfiguration,
                                               FocelaCacheProperties focelaCacheProperties) {
        // Build the RedisCacheWriter
        RedisConnectionFactory connectionFactory = Objects.requireNonNull(redisTemplate.getConnectionFactory());
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory,
                BatchStrategies.scan(focelaCacheProperties.getRedisScanBatchSize()));
        // Build the TenantRedisCacheManager
        return new TimeoutRedisCacheManager(cacheWriter, redisCacheConfiguration);
    }

}
