package com.focela.platform.tenant.core.mq.rocketmq;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

import static com.focela.platform.web.core.utils.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Multi-tenant {@link ConsumeMessageHook} that restores the tenant context on
 * the RocketMQ consumer side. The tenant ID is read from the message user
 * property set by the producer-side hook, pushed onto
 * {@link TenantContextHolder} before the listener body runs, and cleared
 * after consumption completes.
 */
public class TenantRocketMQConsumeMessageHook implements ConsumeMessageHook {

    @Override
    public String hookName() {
        return getClass().getSimpleName();
    }

    @Override
    public void consumeMessageBefore(ConsumeMessageContext context) {
        // Validate: there must be exactly one message, otherwise the tenant may be set incorrectly.
        List<MessageExt> messages = context.getMsgList();
        Assert.isTrue(messages.size() == 1, "Message count ({}) is incorrect", messages.size());
        // Set the tenant ID
        String tenantId = messages.get(0).getUserProperty(HEADER_TENANT_ID);
        if (StrUtil.isNotEmpty(tenantId)) {
            TenantContextHolder.setTenantId(Long.parseLong(tenantId));
        }
    }

    @Override
    public void consumeMessageAfter(ConsumeMessageContext context) {
        TenantContextHolder.clear();
    }

}
