package com.focela.platform.tenant.core.mq.rabbitmq;

import com.focela.platform.tenant.core.context.TenantContextHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.Message;
import org.springframework.lang.Nullable;

import static com.focela.platform.web.core.utils.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * RabbitMQ listener advice that propagates the multi-tenant context.
 *
 * <p>Wired into {@code SimpleRabbitListenerContainerFactory.setAdviceChain(...)}
 * so it runs around every {@code @RabbitListener} method invocation. When the
 * incoming AMQP {@link Message} carries a {@code tenant-id} header (set by
 * {@link TenantRabbitMQMessagePostProcessor} on the producer side) the listener
 * call is wrapped in a save/restore of {@link TenantContextHolder} so the tenant
 * context is set before the listener body runs and cleared on the way out
 * (success or exception).
 *
 * <p>This replaces the previous approach of shadowing Spring's
 * {@code InvocableHandlerMethod} class, using the first-class Spring AMQP
 * advice-chain extension point instead.
 */
public class TenantRabbitMQAdvice implements MethodInterceptor {

    @Nullable
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Long tenantId = parseTenantId(invocation);
        if (tenantId == null) {
            return invocation.proceed();
        }
        Long previousTenantId = TenantContextHolder.getTenantId();
        Boolean previousIgnore = TenantContextHolder.isIgnore();
        try {
            TenantContextHolder.setTenantId(tenantId);
            TenantContextHolder.setIgnore(false);
            return invocation.proceed();
        } finally {
            TenantContextHolder.setTenantId(previousTenantId);
            TenantContextHolder.setIgnore(previousIgnore);
        }
    }

    @Nullable
    private static Long parseTenantId(MethodInvocation invocation) {
        for (Object arg : invocation.getArguments()) {
            if (arg instanceof Message message) {
                Object value = message.getMessageProperties().getHeaders().get(HEADER_TENANT_ID);
                if (value == null) {
                    return null;
                }
                if (value instanceof Long l) {
                    return l;
                }
                if (value instanceof Number n) {
                    return n.longValue();
                }
                return Long.parseLong(value.toString());
            }
        }
        return null;
    }
}
