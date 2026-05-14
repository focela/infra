package com.focela.platform.framework.tenant.core.redis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.redis.core.TimeoutRedisCacheManager;
import com.focela.platform.framework.tenant.core.context.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.util.Set;

/**
 * Multi-tenant {@link RedisCacheManager} implementation class
 *
 * When operating a {@link Cache} with a specific name, automatically append the tenant suffix in the format name + ":" + tenantId + suffix.
 */
@Slf4j
public class TenantRedisCacheManager extends TimeoutRedisCacheManager {

    private static final String SPLIT = "#";

    private final Set<String> ignoreCaches;

    public TenantRedisCacheManager(RedisCacheWriter cacheWriter,
                                   RedisCacheConfiguration defaultCacheConfiguration,
                                   Set<String> ignoreCaches) {
        super(cacheWriter, defaultCacheConfiguration);
        this.ignoreCaches = ignoreCaches;
    }

    @Override
    public Cache getCache(String name) {
        String[] names = StrUtil.splitToArray(name, SPLIT);
        // If multi-tenancy is enabled, append the tenant suffix to name
        if (!TenantContextHolder.isIgnore()
                && TenantContextHolder.getTenantId() != null
                && !CollUtil.contains(ignoreCaches, names[0])) {
            name = name + ":" + TenantContextHolder.getTenantId();
        }

        // Continue with the parent method
        return super.getCache(name);
    }

}
