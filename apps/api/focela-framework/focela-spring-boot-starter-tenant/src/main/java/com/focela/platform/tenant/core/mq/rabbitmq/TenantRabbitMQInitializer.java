package com.focela.platform.tenant.core.mq.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Multi-tenant RabbitMQ initializer
 */
public class TenantRabbitMQInitializer implements BeanPostProcessor {

    @Override
    @SuppressWarnings("PatternVariableCanBeUsed")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RabbitTemplate) {
            RabbitTemplate rabbitTemplate = (RabbitTemplate) bean;
            rabbitTemplate.addBeforePublishPostProcessors(new TenantRabbitMQMessagePostProcessor());
        }
        return bean;
    }

}
