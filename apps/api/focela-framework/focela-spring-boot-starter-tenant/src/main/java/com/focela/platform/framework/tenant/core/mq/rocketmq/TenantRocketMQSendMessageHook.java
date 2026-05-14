package com.focela.platform.framework.tenant.core.mq.rocketmq;

import com.focela.platform.framework.tenant.core.context.TenantContextHolder;
import org.apache.rocketmq.client.hook.SendMessageContext;
import org.apache.rocketmq.client.hook.SendMessageHook;

import static com.focela.platform.framework.web.core.utils.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Multi-tenant {@link SendMessageHook} implementation for RocketMQ message queue
 *
 * When the Producer sends a message, add the tenant ID from {@link TenantContextHolder} to the message Header.
 */
public class TenantRocketMQSendMessageHook implements SendMessageHook {

    @Override
    public String hookName() {
        return getClass().getSimpleName();
    }

    @Override
    public void sendMessageBefore(SendMessageContext sendMessageContext) {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            return;
        }
        sendMessageContext.getMessage().putUserProperty(HEADER_TENANT_ID, tenantId.toString());
    }

    @Override
    public void sendMessageAfter(SendMessageContext sendMessageContext) {
    }

}
