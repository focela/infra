package com.focela.platform.websocket.core.sender.redis;

import com.focela.platform.mq.redis.core.pubsub.AbstractRedisChannelMessage;
import lombok.Data;

/**
 * WebSocket broadcast message over Redis.
 */
@Data
public class RedisWebSocketMessage extends AbstractRedisChannelMessage {

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
