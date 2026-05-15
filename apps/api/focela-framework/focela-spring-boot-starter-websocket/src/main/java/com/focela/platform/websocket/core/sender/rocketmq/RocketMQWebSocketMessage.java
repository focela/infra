package com.focela.platform.websocket.core.sender.rocketmq;

import lombok.Data;

/**
 * WebSocket broadcast message over RocketMQ.
 */
@Data
public class RocketMQWebSocketMessage {

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
