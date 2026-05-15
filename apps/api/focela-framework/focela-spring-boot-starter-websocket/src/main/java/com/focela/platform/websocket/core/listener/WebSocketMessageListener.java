package com.focela.platform.websocket.core.listener;

import com.focela.platform.websocket.core.message.JsonWebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket message listener interface
 *
 * Purpose: after the frontend sends a message to the backend, handle the message of the corresponding {@link #getType()} type.
 *
 * @param <T> generic, message type
 */
public interface WebSocketMessageListener<T> {

    /**
     * Handle message
     *
     * @param session Session
     * @param message message
     */
    void onMessage(WebSocketSession session, T message);

    /**
     * Get the message type
     *
     * @see JsonWebSocketMessage#getType()
     * @return message type
     */
    String getType();

}
