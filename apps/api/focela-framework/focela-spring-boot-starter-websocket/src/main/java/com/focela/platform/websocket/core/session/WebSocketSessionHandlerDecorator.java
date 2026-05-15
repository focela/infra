package com.focela.platform.websocket.core.session;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

/**
 * Decorator for {@link WebSocketHandler} that provides:
 *
 * 1. Tracks {@link WebSocketSession} open/close lifecycle via {@link #sessionManager}.
 * 2. Wraps {@link WebSocketSession} to support concurrent operations.
 */
public class WebSocketSessionHandlerDecorator extends WebSocketHandlerDecorator {

    /**
     * Send timeout, in milliseconds.
     */
    private static final Integer SEND_TIME_LIMIT = 1000 * 5;
    /**
     * Outgoing message buffer cap, in bytes.
     */
    private static final Integer BUFFER_SIZE_LIMIT = 1024 * 100;

    private final WebSocketSessionManager sessionManager;

    public WebSocketSessionHandlerDecorator(WebSocketHandler delegate,
                                            WebSocketSessionManager sessionManager) {
        super(delegate);
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Make the session support concurrent writes — see https://blog.csdn.net/abu935009066/article/details/131218149
        session = new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT);
        // Register with WebSocketSessionManager
        sessionManager.addSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        sessionManager.removeSession(session);
    }

}
