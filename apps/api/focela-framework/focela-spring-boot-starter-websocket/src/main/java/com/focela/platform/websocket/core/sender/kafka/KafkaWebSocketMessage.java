package com.focela.platform.websocket.core.sender.kafka;

import lombok.Data;

/**
 * WebSocket broadcast message over Kafka.
 */
@Data
public class KafkaWebSocketMessage {

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
