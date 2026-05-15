package com.focela.platform.tenant.core.mq.rabbitmq;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Multi-tenant RabbitMQ initializer.
 *
 * <p>Wires two tenant-propagation hooks at bean post-processing time:
 * <ul>
 *   <li>Producer side: registers {@link TenantRabbitMQMessagePostProcessor} on every
 *       {@link RabbitTemplate} so outgoing messages carry the {@code tenant-id} header.</li>
 *   <li>Consumer side: appends {@link TenantRabbitMQAdvice} to the advice chain of every
 *       {@link SimpleRabbitListenerContainerFactory} so {@code @RabbitListener} methods
 *       run inside the tenant context.</li>
 * </ul>
 */
public class TenantRabbitMQInitializer implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RabbitTemplate rabbitTemplate) {
            rabbitTemplate.addBeforePublishPostProcessors(new TenantRabbitMQMessagePostProcessor());
        } else if (bean instanceof SimpleRabbitListenerContainerFactory factory) {
            Advice[] existing = factory.getAdviceChain();
            Advice[] merged;
            if (existing == null || existing.length == 0) {
                merged = new Advice[]{new TenantRabbitMQAdvice()};
            } else {
                merged = new Advice[existing.length + 1];
                System.arraycopy(existing, 0, merged, 0, existing.length);
                merged[existing.length] = new TenantRabbitMQAdvice();
            }
            factory.setAdviceChain(merged);
        }
        return bean;
    }

}
