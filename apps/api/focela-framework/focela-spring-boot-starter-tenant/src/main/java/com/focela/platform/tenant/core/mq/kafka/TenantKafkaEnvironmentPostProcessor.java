package com.focela.platform.tenant.core.mq.kafka;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Multi-tenant Kafka {@link EnvironmentPostProcessor} implementation
 *
 * Adds the {@link TenantKafkaProducerInterceptor} interceptor when the Kafka Producer sends messages.
 */
@Slf4j
public class TenantKafkaEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_KEY_INTERCEPTOR_CLASSES = "spring.kafka.producer.properties.interceptor.classes";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Add the TenantKafkaProducerInterceptor interceptor
        try {
            String value = environment.getProperty(PROPERTY_KEY_INTERCEPTOR_CLASSES);
            if (StrUtil.isEmpty(value)) {
                value = TenantKafkaProducerInterceptor.class.getName();
            } else {
                value += "," + TenantKafkaProducerInterceptor.class.getName();
            }
            environment.getSystemProperties().put(PROPERTY_KEY_INTERCEPTOR_CLASSES, value);
        } catch (NoClassDefFoundError ignore) {
            // If a NoClassDefFoundError is thrown, the TenantKafkaProducerInterceptor class does not exist, i.e., the kafka-spring dependency was not introduced.
        }
    }

}
