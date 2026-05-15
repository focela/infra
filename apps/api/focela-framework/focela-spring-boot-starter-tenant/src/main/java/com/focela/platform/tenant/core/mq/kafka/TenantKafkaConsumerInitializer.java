package com.focela.platform.tenant.core.mq.kafka;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

/**
 * Multi-tenant Kafka consumer initializer.
 *
 * <p>For every {@link ConcurrentKafkaListenerContainerFactory} bean, attaches a
 * {@link TenantKafkaRecordInterceptor} so every {@code @KafkaListener} method
 * runs inside the tenant context derived from the message header. Producer-side
 * wiring is handled separately by {@link TenantKafkaEnvironmentPostProcessor}
 * (sets {@code interceptor.classes}) — this initializer is the consumer-side
 * counterpart.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class TenantKafkaConsumerInitializer implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ConcurrentKafkaListenerContainerFactory factory) {
            factory.setRecordInterceptor(new TenantKafkaRecordInterceptor());
        }
        return bean;
    }

}
