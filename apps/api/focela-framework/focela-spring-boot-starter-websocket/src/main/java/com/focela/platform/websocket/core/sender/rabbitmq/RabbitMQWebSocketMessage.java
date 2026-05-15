package com.focela.platform.websocket.core.sender.rabbitmq;

import lombok.Data;

import java.io.Serializable;

/**
 * WebSocket message broadcast via RabbitMQ.
 */
@Data
public class RabbitMQWebSocketMessage implements Serializable {

    /**
     * Session ID
     */
    private String sessionId;
    /**
     * User type
     */
    private Integer userType;
    /**
     * User ID
     */
    private Long userId;

    /**
     * Message type
     */
    private String messageType;
    /**
     * Message content
     */
    private String messageContent;

}
