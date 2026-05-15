package com.focela.platform.tenant.core.mq.kafka;

import cn.hutool.core.util.ReflectUtil;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import java.util.Map;

import static com.focela.platform.web.core.utils.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Multi-tenant {@link ProducerInterceptor} implementation for Kafka message queue
 *
 * 1. When the Producer sends a message, add the tenant ID from {@link TenantContextHolder} to the message Header.
 * 2. When the Consumer consumes a message, add the tenant ID from the message Header to {@link TenantContextHolder}, implemented via {@link InvocableHandlerMethod}.
 */
public class TenantKafkaProducerInterceptor implements ProducerInterceptor<Object, Object> {

    @Override
    public ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> record) {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            Headers headers = (Headers) ReflectUtil.getFieldValue(record, "headers"); // private field, no getter, use reflection
            headers.add(HEADER_TENANT_ID, tenantId.toString().getBytes());
        }
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }

}
