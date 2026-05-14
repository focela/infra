package com.focela.platform.framework.websocket.core.utils;

import com.focela.platform.framework.security.core.LoginUser;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

/**
 * Utility class scoped to the web package.
 */
public class WebSocketFrameworkUtils {

    public static final String ATTRIBUTE_LOGIN_USER = "LOGIN_USER";

    /**
     * Set the current user.
     *
     * @param loginUser logged-in user
     * @param attributes Session attributes
     */
    public static void setLoginUser(LoginUser loginUser, Map<String, Object> attributes) {
        attributes.put(ATTRIBUTE_LOGIN_USER, loginUser);
    }

    /**
     * Get the current user.
     *
     * @return current user
     */
    public static LoginUser getLoginUser(WebSocketSession session) {
        return (LoginUser) session.getAttributes().get(ATTRIBUTE_LOGIN_USER);
    }

    /**
     * Get the current user's ID.
     *
     * @return user ID
     */
    public static Long getLoginUserId(WebSocketSession session) {
        LoginUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getId() : null;
    }

    /**
     * Get the current user's type.
     *
     * @return user type
     */
    public static Integer getLoginUserType(WebSocketSession session) {
        LoginUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getUserType() : null;
    }

    /**
     * Get the current user's tenant ID.
     *
     * @param session Session
     * @return tenant ID
     */
    public static Long getTenantId(WebSocketSession session) {
        LoginUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getTenantId() : null;
    }

}
