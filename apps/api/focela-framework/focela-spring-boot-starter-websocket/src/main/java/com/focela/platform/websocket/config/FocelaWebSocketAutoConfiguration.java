package com.focela.platform.websocket.config;

import com.focela.platform.mq.redis.config.FocelaRedisMQConsumerAutoConfiguration;
import com.focela.platform.mq.redis.core.RedisMQTemplate;
import com.focela.platform.websocket.core.handler.JsonWebSocketMessageHandler;
import com.focela.platform.websocket.core.listener.WebSocketMessageListener;
import com.focela.platform.websocket.core.security.LoginUserHandshakeInterceptor;
import com.focela.platform.websocket.core.security.WebSocketAuthorizeRequestsCustomizer;
import com.focela.platform.websocket.core.sender.kafka.KafkaWebSocketMessageConsumer;
import com.focela.platform.websocket.core.sender.kafka.KafkaWebSocketMessageSender;
import com.focela.platform.websocket.core.sender.local.LocalWebSocketMessageSender;
import com.focela.platform.websocket.core.sender.rabbitmq.RabbitMQWebSocketMessageConsumer;
import com.focela.platform.websocket.core.sender.rabbitmq.RabbitMQWebSocketMessageSender;
import com.focela.platform.websocket.core.sender.redis.RedisWebSocketMessageConsumer;
import com.focela.platform.websocket.core.sender.redis.RedisWebSocketMessageSender;
import com.focela.platform.websocket.core.sender.rocketmq.RocketMQWebSocketMessageConsumer;
import com.focela.platform.websocket.core.sender.rocketmq.RocketMQWebSocketMessageSender;
import com.focela.platform.websocket.core.session.WebSocketSessionHandlerDecorator;
import com.focela.platform.websocket.core.session.WebSocketSessionManager;
import com.focela.platform.websocket.core.session.DefaultWebSocketSessionManager;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;

/**
 * WebSocket auto-configuration.
 */
@AutoConfiguration(before = FocelaRedisMQConsumerAutoConfiguration.class) // Ordered before FocelaRedisMQConsumerAutoConfiguration so that RedisWebSocketMessageConsumer is created first, allowing RedisMessageListenerContainer to be created
@EnableWebSocket // Enable websocket
@ConditionalOnProperty(prefix = "focela.websocket", value = "enable", matchIfMissing = true)
@EnableConfigurationProperties(WebSocketProperties.class)
public class FocelaWebSocketAutoConfiguration {

    @Bean
    public WebSocketConfigurer webSocketConfigurer(HandshakeInterceptor[] handshakeInterceptors,
                                                   WebSocketHandler webSocketHandler,
                                                   WebSocketProperties webSocketProperties) {
        return registry -> registry
                // Add the WebSocketHandler
                .addHandler(webSocketHandler, webSocketProperties.getPath())
                .addInterceptors(handshakeInterceptors)
                // Allow cross-origin, otherwise the front-end connection would be dropped
                .setAllowedOriginPatterns("*");
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new LoginUserHandshakeInterceptor();
    }

    @Bean
    public WebSocketHandler webSocketHandler(WebSocketSessionManager sessionManager,
                                             List<? extends WebSocketMessageListener<?>> messageListeners) {
        // 1. Create the JsonWebSocketMessageHandler that handles messages
        JsonWebSocketMessageHandler messageHandler = new JsonWebSocketMessageHandler(messageListeners);
        // 2. Create the WebSocketSessionHandlerDecorator that handles connections
        return new WebSocketSessionHandlerDecorator(messageHandler, sessionManager);
    }

    @Bean
    public WebSocketSessionManager webSocketSessionManager() {
        return new DefaultWebSocketSessionManager();
    }

    @Bean
    public WebSocketAuthorizeRequestsCustomizer webSocketAuthorizeRequestsCustomizer(
            com.focela.platform.web.config.WebProperties webProperties,
            WebSocketProperties webSocketProperties) {
        return new WebSocketAuthorizeRequestsCustomizer(webProperties, webSocketProperties);
    }

    // ==================== Sender related ====================

    @Configuration
    @ConditionalOnProperty(prefix = "focela.websocket", name = "sender-type", havingValue = "local")
    public class LocalWebSocketMessageSenderConfiguration {

        @Bean
        public LocalWebSocketMessageSender localWebSocketMessageSender(WebSocketSessionManager sessionManager) {
            return new LocalWebSocketMessageSender(sessionManager);
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "focela.websocket", name = "sender-type", havingValue = "redis")
    public class RedisWebSocketMessageSenderConfiguration {

        @Bean
        public RedisWebSocketMessageSender redisWebSocketMessageSender(WebSocketSessionManager sessionManager,
                                                                       RedisMQTemplate redisMQTemplate) {
            return new RedisWebSocketMessageSender(sessionManager, redisMQTemplate);
        }

        @Bean
        public RedisWebSocketMessageConsumer redisWebSocketMessageConsumer(
                RedisWebSocketMessageSender redisWebSocketMessageSender) {
            return new RedisWebSocketMessageConsumer(redisWebSocketMessageSender);
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "focela.websocket", name = "sender-type", havingValue = "rocketmq")
    public class RocketMQWebSocketMessageSenderConfiguration {

        @Bean
        public RocketMQWebSocketMessageSender rocketMQWebSocketMessageSender(
                WebSocketSessionManager sessionManager, RocketMQTemplate rocketMQTemplate,
                @Value("${focela.websocket.sender-rocketmq.topic}") String topic) {
            return new RocketMQWebSocketMessageSender(sessionManager, rocketMQTemplate, topic);
        }

        @Bean
        public RocketMQWebSocketMessageConsumer rocketMQWebSocketMessageConsumer(
                RocketMQWebSocketMessageSender rocketMQWebSocketMessageSender) {
            return new RocketMQWebSocketMessageConsumer(rocketMQWebSocketMessageSender);
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "focela.websocket", name = "sender-type", havingValue = "rabbitmq")
    public class RabbitMQWebSocketMessageSenderConfiguration {

        @Bean
        public RabbitMQWebSocketMessageSender rabbitMQWebSocketMessageSender(
                WebSocketSessionManager sessionManager, RabbitTemplate rabbitTemplate,
                TopicExchange websocketTopicExchange) {
            return new RabbitMQWebSocketMessageSender(sessionManager, rabbitTemplate, websocketTopicExchange);
        }

        @Bean
        public RabbitMQWebSocketMessageConsumer rabbitMQWebSocketMessageConsumer(
                RabbitMQWebSocketMessageSender rabbitMQWebSocketMessageSender) {
            return new RabbitMQWebSocketMessageConsumer(rabbitMQWebSocketMessageSender);
        }

        /**
         * Create the Topic Exchange.
         */
        @Bean
        public TopicExchange websocketTopicExchange(@Value("${focela.websocket.sender-rabbitmq.exchange}") String exchange) {
            return new TopicExchange(exchange,
                    true,  // durable: whether to persist
                    false);  // exclusive: whether exclusive
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "focela.websocket", name = "sender-type", havingValue = "kafka")
    public class KafkaWebSocketMessageSenderConfiguration {

        @Bean
        public KafkaWebSocketMessageSender kafkaWebSocketMessageSender(
                WebSocketSessionManager sessionManager, KafkaTemplate<Object, Object> kafkaTemplate,
                @Value("${focela.websocket.sender-kafka.topic}") String topic) {
            return new KafkaWebSocketMessageSender(sessionManager, kafkaTemplate, topic);
        }

        @Bean
        public KafkaWebSocketMessageConsumer kafkaWebSocketMessageConsumer(
                KafkaWebSocketMessageSender kafkaWebSocketMessageSender) {
            return new KafkaWebSocketMessageConsumer(kafkaWebSocketMessageSender);
        }

    }

}