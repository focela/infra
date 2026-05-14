package com.focela.platform.framework.websocket.core.sender;

import com.focela.platform.framework.common.utils.json.JsonUtils;

/**
 * WebSocket message sender interface
 */
public interface WebSocketMessageSender {

    /**
     * Send a message to the specified user.
     *
     * @param userType user type
     * @param userId user ID
     * @param messageType message type
     * @param messageContent message content, in JSON format
     */
    void send(Integer userType, Long userId, String messageType, String messageContent);

    /**
     * Send a message to the specified user type.
     *
     * @param userType user type
     * @param messageType message type
     * @param messageContent message content, in JSON format
     */
    void send(Integer userType, String messageType, String messageContent);

    /**
     * Send a message to the specified Session.
     *
     * @param sessionId Session ID
     * @param messageType message type
     * @param messageContent message content, in JSON format
     */
    void send(String sessionId, String messageType, String messageContent);

    default void sendObject(Integer userType, Long userId, String messageType, Object messageContent) {
        send(userType, userId, messageType, JsonUtils.toJsonString(messageContent));
    }

    default void sendObject(Integer userType, String messageType, Object messageContent) {
        send(userType, messageType, JsonUtils.toJsonString(messageContent));
    }

    default void sendObject(String sessionId, String messageType, Object messageContent) {
        send(sessionId, messageType, JsonUtils.toJsonString(messageContent));
    }

}
