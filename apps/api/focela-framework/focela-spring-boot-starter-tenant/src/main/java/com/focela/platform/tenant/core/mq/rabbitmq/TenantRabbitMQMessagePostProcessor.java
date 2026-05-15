package com.focela.platform.tenant.core.mq.rabbitmq;

import com.focela.platform.tenant.core.context.TenantContextHolder;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

import static com.focela.platform.web.core.utils.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Multi-tenant {@link MessagePostProcessor} that stamps the outgoing RabbitMQ message
 * with the current tenant ID.
 *
 * <p>1. Producer side (this class): when sending a message, copy the tenant ID from
 * {@link TenantContextHolder} into the AMQP message header.
 * <p>2. Consumer side: see {@link TenantRabbitMQAdvice}, an aopalliance advice that
 * runs around every {@code @RabbitListener} method and restores the tenant context
 * from the header.
 */
public class TenantRabbitMQMessagePostProcessor implements MessagePostProcessor {

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            message.getMessageProperties().getHeaders().put(HEADER_TENANT_ID, tenantId);
        }
        return message;
    }

}
