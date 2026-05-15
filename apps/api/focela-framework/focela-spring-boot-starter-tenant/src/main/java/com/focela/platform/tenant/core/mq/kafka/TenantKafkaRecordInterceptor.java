package com.focela.platform.tenant.core.mq.kafka;

import com.focela.platform.tenant.core.context.TenantContextHolder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.lang.Nullable;

import static com.focela.platform.web.core.utils.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Kafka {@link RecordInterceptor} that propagates the multi-tenant context.
 *
 * <p>For every consumed record the {@code tenant-id} header (set by
 * {@link TenantKafkaProducerInterceptor} on the producer side) is read and
 * pushed onto {@link TenantContextHolder} before the {@code @KafkaListener}
 * method runs. After the listener returns — whether successfully or with an
 * exception — {@link #afterRecord} restores the previous context. The previous
 * tenant id / ignore flag are remembered in a thread-local snapshot so any
 * pre-existing context on the consumer thread is not lost.
 *
 * <p>This replaces the previous approach of shadowing Spring's
 * {@code InvocableHandlerMethod} class, using a first-class Spring Kafka
 * extension point instead.
 */
public class TenantKafkaRecordInterceptor implements RecordInterceptor<Object, Object> {

    private static final ThreadLocal<ContextSnapshot> PREVIOUS_CONTEXT = new ThreadLocal<>();

    @Nullable
    @Override
    public ConsumerRecord<Object, Object> intercept(ConsumerRecord<Object, Object> record,
                                                    Consumer<Object, Object> consumer) {
        Long tenantId = parseTenantId(record);
        if (tenantId == null) {
            return record;
        }
        PREVIOUS_CONTEXT.set(new ContextSnapshot(
                TenantContextHolder.getTenantId(), TenantContextHolder.isIgnore()));
        TenantContextHolder.setTenantId(tenantId);
        TenantContextHolder.setIgnore(false);
        return record;
    }

    @Override
    public void afterRecord(ConsumerRecord<Object, Object> record, Consumer<Object, Object> consumer) {
        ContextSnapshot snapshot = PREVIOUS_CONTEXT.get();
        if (snapshot == null) {
            return;
        }
        try {
            TenantContextHolder.setTenantId(snapshot.tenantId());
            TenantContextHolder.setIgnore(snapshot.ignore());
        } finally {
            PREVIOUS_CONTEXT.remove();
        }
    }

    @Nullable
    private static Long parseTenantId(ConsumerRecord<Object, Object> record) {
        Header header = record.headers().lastHeader(HEADER_TENANT_ID);
        if (header == null || header.value() == null) {
            return null;
        }
        try {
            return Long.parseLong(new String(header.value()));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Unparseable " + HEADER_TENANT_ID + " header value: " + new String(header.value()), ex);
        }
    }

    private record ContextSnapshot(@Nullable Long tenantId, @Nullable Boolean ignore) {
    }
}
