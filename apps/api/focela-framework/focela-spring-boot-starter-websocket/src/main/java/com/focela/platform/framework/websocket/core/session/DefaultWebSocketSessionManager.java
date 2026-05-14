package com.focela.platform.framework.websocket.core.session;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.security.core.LoginUser;
import com.focela.platform.framework.tenant.core.context.TenantContextHolder;
import com.focela.platform.framework.websocket.core.utils.WebSocketFrameworkUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default {@link WebSocketSessionManager} implementation.
 */
public class DefaultWebSocketSessionManager implements WebSocketSessionManager {

    /**
     * id-to-WebSocketSession map.
     *
     * key: Session ID
     */
    private final ConcurrentMap<String, WebSocketSession> idSessions = new ConcurrentHashMap<>();

    /**
     * user-to-WebSocketSession map.
     *
     * key1: user type
     * key2: user ID
     */
    private final ConcurrentMap<Integer, ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>>> userSessions
            = new ConcurrentHashMap<>();

    @Override
    public void addSession(WebSocketSession session) {
        // Add to idSessions
        idSessions.put(session.getId(), session);
        // Add to userSessions
        LoginUser user = WebSocketFrameworkUtils.getLoginUser(session);
        if (user == null) {
            return;
        }
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(user.getUserType());
        if (userSessionsMap == null) {
            userSessionsMap = new ConcurrentHashMap<>();
            if (userSessions.putIfAbsent(user.getUserType(), userSessionsMap) != null) {
                userSessionsMap = userSessions.get(user.getUserType());
            }
        }
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(user.getId());
        if (sessions == null) {
            sessions = new CopyOnWriteArrayList<>();
            if (userSessionsMap.putIfAbsent(user.getId(), sessions) != null) {
                sessions = userSessionsMap.get(user.getId());
            }
        }
        sessions.add(session);
    }

    @Override
    public void removeSession(WebSocketSession session) {
        // Remove from idSessions
        idSessions.remove(session.getId());
        // Remove from userSessions
        LoginUser user = WebSocketFrameworkUtils.getLoginUser(session);
        if (user == null) {
            return;
        }
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(user.getUserType());
        if (userSessionsMap == null) {
            return;
        }
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(user.getId());
        sessions.removeIf(session0 -> session0.getId().equals(session.getId()));
        if (CollUtil.isEmpty(sessions)) {
            userSessionsMap.remove(user.getId(), sessions);
        }
    }

    @Override
    public WebSocketSession getSession(String id) {
        return idSessions.get(id);
    }

    @Override
    public Collection<WebSocketSession> getSessionList(Integer userType) {
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(userType);
        if (CollUtil.isEmpty(userSessionsMap)) {
            return new ArrayList<>();
        }
        LinkedList<WebSocketSession> result = new LinkedList<>(); // Avoid resizing
        Long contextTenantId = TenantContextHolder.getTenantId();
        for (List<WebSocketSession> sessions : userSessionsMap.values()) {
            if (CollUtil.isEmpty(sessions)) {
                continue;
            }
            // Special: if tenant does not match, skip directly
            if (contextTenantId != null) {
                Long userTenantId = WebSocketFrameworkUtils.getTenantId(sessions.get(0));
                if (!contextTenantId.equals(userTenantId)) {
                    continue;
                }
            }
            result.addAll(sessions);
        }
        return result;
    }

    @Override
    public Collection<WebSocketSession> getSessionList(Integer userType, Long userId) {
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(userType);
        if (CollUtil.isEmpty(userSessionsMap)) {
            return new ArrayList<>();
        }
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(userId);
        return CollUtil.isNotEmpty(sessions) ? new ArrayList<>(sessions) : new ArrayList<>();
    }

}
