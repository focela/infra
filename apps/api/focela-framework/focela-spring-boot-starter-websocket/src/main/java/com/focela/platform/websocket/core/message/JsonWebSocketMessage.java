package com.focela.platform.websocket.core.message;

import com.focela.platform.websocket.core.listener.WebSocketMessageListener;
import lombok.Data;

import java.io.Serializable;

/**
 * WebSocket message frame in JSON format.
 */
@Data
public class JsonWebSocketMessage implements Serializable {

    /**
     * Message type.
     *
     * Purpose: dispatch to the corresponding {@link WebSocketMessageListener} implementation.
     */
    private String type;
    /**
     * Message content.
     *
     * Must be a JSON object.
     */
    private String content;

}
