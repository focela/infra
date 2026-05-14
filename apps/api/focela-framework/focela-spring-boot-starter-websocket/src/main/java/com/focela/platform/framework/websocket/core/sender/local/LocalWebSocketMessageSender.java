package com.focela.platform.framework.websocket.core.sender.local;

import com.focela.platform.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import com.focela.platform.framework.websocket.core.sender.WebSocketMessageSender;
import com.focela.platform.framework.websocket.core.session.WebSocketSessionManager;

/**
 * Local {@link WebSocketMessageSender} implementation.
 *
 * Note: only suitable for single-node scenarios!
 */
public class LocalWebSocketMessageSender extends AbstractWebSocketMessageSender {

    public LocalWebSocketMessageSender(WebSocketSessionManager sessionManager) {
        super(sessionManager);
    }

}
