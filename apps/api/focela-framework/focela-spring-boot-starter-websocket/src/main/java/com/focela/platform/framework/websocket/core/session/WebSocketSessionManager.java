package com.focela.platform.framework.websocket.core.session;

import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;

/**
 * Manager interface for {@link WebSocketSession} instances.
 */
public interface WebSocketSessionManager {

    /**
     * Add a Session.
     *
     * @param session Session
     */
    void addSession(WebSocketSession session);

    /**
     * Remove a Session.
     *
     * @param session Session
     */
    void removeSession(WebSocketSession session);

    /**
     * Get the Session with the given ID.
     *
     * @param id Session ID
     * @return Session
     */
    WebSocketSession getSession(String id);

    /**
     * Get all Sessions for the given user type.
     *
     * @param userType user type
     * @return Session list
     */
    Collection<WebSocketSession> getSessionList(Integer userType);

    /**
     * Get all Sessions for the given user.
     *
     * @param userType user type
     * @param userId user ID
     * @return Session list
     */
    Collection<WebSocketSession> getSessionList(Integer userType, Long userId);

}