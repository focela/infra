package com.focela.platform.tenant.core.mq.redis;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.mq.redis.core.interceptor.RedisMessageInterceptor;
import com.focela.platform.mq.redis.core.message.AbstractRedisMessage;
import com.focela.platform.tenant.core.context.TenantContextHolder;

import static com.focela.platform.web.core.utils.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Multi-tenant {@link AbstractRedisMessage} interceptor
 *
 * 1. When the Producer sends a message, add the tenant ID from {@link TenantContextHolder} to the message Header.
 * 2. When the Consumer consumes a message, add the tenant ID from the message Header to {@link TenantContextHolder}.
 */
public class TenantRedisMessageInterceptor implements RedisMessageInterceptor {

    @Override
    public void sendMessageBefore(AbstractRedisMessage message) {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            message.addHeader(HEADER_TENANT_ID, tenantId.toString());
        }
    }

    @Override
    public void consumeMessageBefore(AbstractRedisMessage message) {
        String tenantIdStr = message.getHeader(HEADER_TENANT_ID);
        if (StrUtil.isNotEmpty(tenantIdStr)) {
            TenantContextHolder.setTenantId(Long.valueOf(tenantIdStr));
        }
    }

    @Override
    public void consumeMessageAfter(AbstractRedisMessage message) {
        // Note: the Consumer is a logical entry point, so we do not consider the case where the tenant ID already exists in the context.
        TenantContextHolder.clear();
    }

}
