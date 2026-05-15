package com.focela.platform.websocket.core.sender.rabbitmq;

import com.focela.platform.websocket.core.sender.AbstractWebSocketMessageSender;
import com.focela.platform.websocket.core.sender.WebSocketMessageSender;
import com.focela.platform.websocket.core.session.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * RabbitMQ-based implementation of {@link WebSocketMessageSender}.
 */
@Slf4j
public class RabbitMQWebSocketMessageSender extends AbstractWebSocketMessageSender {

    private final RabbitTemplate rabbitTemplate;

    private final TopicExchange topicExchange;

    public RabbitMQWebSocketMessageSender(WebSocketSessionManager sessionManager,
                                          RabbitTemplate rabbitTemplate,
                                          TopicExchange topicExchange) {
        super(sessionManager);
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
    }

    @Override
    public void send(Integer userType, Long userId, String messageType, String messageContent) {
        sendRabbitMQMessage(null, userId, userType, messageType, messageContent);
    }

    @Override
    public void send(Integer userType, String messageType, String messageContent) {
        sendRabbitMQMessage(null, null, userType, messageType, messageContent);
    }

    @Override
    public void send(String sessionId, String messageType, String messageContent) {
        sendRabbitMQMessage(sessionId, null, null, messageType, messageContent);
    }

    /**
     * Broadcast a message over RabbitMQ.
     *
     * @param sessionId Session ID
     * @param userId User ID
     * @param userType User type
     * @param messageType Message type
     * @param messageContent Message content
     */
    private void sendRabbitMQMessage(String sessionId, Long userId, Integer userType,
                                     String messageType, String messageContent) {
        RabbitMQWebSocketMessage mqMessage = new RabbitMQWebSocketMessage()
                .setSessionId(sessionId).setUserId(userId).setUserType(userType)
                .setMessageType(messageType).setMessageContent(messageContent);
        rabbitTemplate.convertAndSend(topicExchange.getName(), null, mqMessage);
    }

}
