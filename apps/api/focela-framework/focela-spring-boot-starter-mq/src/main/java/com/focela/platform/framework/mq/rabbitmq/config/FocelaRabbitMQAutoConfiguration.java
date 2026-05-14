package com.focela.platform.framework.mq.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * RabbitMQ message queue configuration class.
 */
@AutoConfiguration
@Slf4j
@ConditionalOnClass(name = "org.springframework.amqp.rabbit.core.RabbitTemplate")
public class FocelaRabbitMQAutoConfiguration {

    /**
     * Jackson2JsonMessageConverter bean: serialize messages with Jackson.
     */
    @Bean
    public MessageConverter createMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
