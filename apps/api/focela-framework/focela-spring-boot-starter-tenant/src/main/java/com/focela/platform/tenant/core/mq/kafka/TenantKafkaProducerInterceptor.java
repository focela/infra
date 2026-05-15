package com.focela.platform.tenant.core.mq.kafka;

import cn.hutool.core.util.ReflectUtil;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;

import java.util.Map;

import static com.focela.platform.web.core.utils.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Multi-tenant {@link ProducerInterceptor} that stamps the outgoing Kafka record
 * with the current tenant ID.
 *
 * <p>1. Producer side (this class): when sending a record, copy the tenant ID from
 * {@link TenantContextHolder} into the Kafka record headers.
 * <p>2. Consumer side: see {@link TenantKafkaRecordInterceptor}, a Spring Kafka
 * {@code RecordInterceptor} that runs before every {@code @KafkaListener} method
 * and restores the tenant context from the header.
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
