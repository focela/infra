package com.focela.platform.websocket.core.sender;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.utils.json.JsonUtils;
import com.focela.platform.websocket.core.message.JsonWebSocketMessage;
import com.focela.platform.websocket.core.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * WebSocketMessageSender implementation.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractWebSocketMessageSender implements WebSocketMessageSender {

    private final WebSocketSessionManager sessionManager;

    @Override
    public void send(Integer userType, Long userId, String messageType, String messageContent) {
        send(null, userType, userId, messageType, messageContent);
    }

    @Override
    public void send(Integer userType, String messageType, String messageContent) {
        send(null, userType, null, messageType, messageContent);
    }

    @Override
    public void send(String sessionId, String messageType, String messageContent) {
        send(sessionId, null, null, messageType, messageContent);
    }

    /**
     * Send a message.
     *
     * @param sessionId Session ID
     * @param userType user type
     * @param userId user ID
     * @param messageType message type
     * @param messageContent message content
     */
    public void send(String sessionId, Integer userType, Long userId, String messageType, String messageContent) {
        // 1. Get the Session list
        List<WebSocketSession> sessions = Collections.emptyList();
        if (StrUtil.isNotEmpty(sessionId)) {
            WebSocketSession session = sessionManager.getSession(sessionId);
            if (session != null) {
                sessions = Collections.singletonList(session);
            }
        } else if (userType != null && userId != null) {
            sessions = (List<WebSocketSession>) sessionManager.getSessionList(userType, userId);
        } else if (userType != null) {
            sessions = (List<WebSocketSession>) sessionManager.getSessionList(userType);
        }
        if (CollUtil.isEmpty(sessions)) {
            if (log.isDebugEnabled()) {
                log.debug("[send][sessionId({}) userType({}) userId({}) messageType({}) messageContent({}) not matches to session]",
                        sessionId, userType, userId, messageType, messageContent);
            }
        }
        // 2. Send
        doSend(sessions, messageType, messageContent);
    }

    /**
     * Concrete implementation of sending a message.
     *
     * @param sessions Session list
     * @param messageType message type
     * @param messageContent message content
     */
    public void doSend(Collection<WebSocketSession> sessions, String messageType, String messageContent) {
        JsonWebSocketMessage message = new JsonWebSocketMessage().setType(messageType).setContent(messageContent);
        String payload = JsonUtils.toJsonString(message); // Key step: serialize as JSON
        sessions.forEach(session -> {
            // 1. Various checks to ensure the Session can be sent to
            if (session == null) {
                log.error("[doSend][session is empty, message({})]", message);
                return;
            }
            if (!session.isOpen()) {
                log.error("[doSend][session({}) is closed, message({})]", session.getId(), message);
                return;
            }
            // 2. Send
            try {
                session.sendMessage(new TextMessage(payload));
                log.info("[doSend][session({}) send message success, message({})]", session.getId(), message);
            } catch (IOException ex) {
                log.error("[doSend][session({}) send message failed, message({})]", session.getId(), message, ex);
            }
        });
    }

}
