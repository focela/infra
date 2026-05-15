package com.focela.platform.tenant.core.mq.rabbitmq;

import com.focela.platform.tenant.core.context.TenantContextHolder;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import static com.focela.platform.web.core.utils.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Multi-tenant {@link ProducerInterceptor} implementation for RabbitMQ message queue
 *
 * 1. When the Producer sends a message, add the tenant ID from {@link TenantContextHolder} to the message Header.
 * 2. When the Consumer consumes a message, add the tenant ID from the message Header to {@link TenantContextHolder}, implemented via {@link InvocableHandlerMethod}.
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
