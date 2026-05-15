package com.focela.platform.websocket.core.sender.redis;

import com.focela.platform.mq.redis.core.RedisMQTemplate;
import com.focela.platform.websocket.core.sender.AbstractWebSocketMessageSender;
import com.focela.platform.websocket.core.sender.WebSocketMessageSender;
import com.focela.platform.websocket.core.session.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis-based {@link WebSocketMessageSender} implementation.
 */
@Slf4j
public class RedisWebSocketMessageSender extends AbstractWebSocketMessageSender {

    private final RedisMQTemplate redisMQTemplate;

    public RedisWebSocketMessageSender(WebSocketSessionManager sessionManager,
                                       RedisMQTemplate redisMQTemplate) {
        super(sessionManager);
        this.redisMQTemplate = redisMQTemplate;
    }

    @Override
    public void send(Integer userType, Long userId, String messageType, String messageContent) {
        sendRedisMessage(null, userId, userType, messageType, messageContent);
    }

    @Override
    public void send(Integer userType, String messageType, String messageContent) {
        sendRedisMessage(null, null, userType, messageType, messageContent);
    }

    @Override
    public void send(String sessionId, String messageType, String messageContent) {
        sendRedisMessage(sessionId, null, null, messageType, messageContent);
    }

    /**
     * Broadcast a message via Redis.
     *
     * @param sessionId Session ID
     * @param userId user ID
     * @param userType user type
     * @param messageType message type
     * @param messageContent message content
     */
    private void sendRedisMessage(String sessionId, Long userId, Integer userType,
                                  String messageType, String messageContent) {
        RedisWebSocketMessage mqMessage = new RedisWebSocketMessage()
                .setSessionId(sessionId).setUserId(userId).setUserType(userType)
                .setMessageType(messageType).setMessageContent(messageContent);
        redisMQTemplate.send(mqMessage);
    }

}
