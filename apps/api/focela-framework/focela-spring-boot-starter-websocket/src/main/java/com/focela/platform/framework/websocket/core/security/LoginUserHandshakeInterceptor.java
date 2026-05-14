package com.focela.platform.framework.websocket.core.security;

import com.focela.platform.framework.security.core.LoginUser;
import com.focela.platform.framework.security.core.filter.TokenAuthenticationFilter;
import com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils;
import com.focela.platform.framework.websocket.core.utils.WebSocketFrameworkUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * {@link HandshakeInterceptor} implementation for the logged-in user.
 *
 * Flow:
 * 1. When the frontend connects to websocket, it appends ?token={token} to the ws:// URL so it can be authenticated by {@link TokenAuthenticationFilter}.
 * 2. {@link LoginUserHandshakeInterceptor} adds the {@link LoginUser} to the {@link WebSocketSession}.
 */
public class LoginUserHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null) {
            WebSocketFrameworkUtils.setLoginUser(loginUser, attributes);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // do nothing
    }

}
