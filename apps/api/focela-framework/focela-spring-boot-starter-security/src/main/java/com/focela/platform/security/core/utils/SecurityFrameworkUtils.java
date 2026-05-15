package com.focela.platform.security.core.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.security.core.LoginUser;
import com.focela.platform.web.core.utils.WebFrameworkUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * Security service utility class.
 */
public class SecurityFrameworkUtils {

    /**
     * Prefix of the HEADER authentication value
     */
    public static final String AUTHORIZATION_BEARER = "Bearer";

    private SecurityFrameworkUtils() {}

    /**
     * Get the authentication Token from the request.
     *
     * @param request       request
     * @param headerName    header name for the authentication Token
     * @param parameterName parameter name for the authentication Token
     * @return authentication Token
     */
    public static String obtainAuthorization(HttpServletRequest request,
                                             String headerName, String parameterName) {
        // 1. Get the Token. Priority: Header > Parameter
        String token = request.getHeader(headerName);
        if (StrUtil.isEmpty(token)) {
            token = request.getParameter(parameterName);
        }
        if (!StringUtils.hasText(token)) {
            return null;
        }
        // 2. Strip the Bearer prefix from the Token
        int index = token.indexOf(AUTHORIZATION_BEARER + " ");
        return index >= 0 ? token.substring(index + 7).trim() : token;
    }

    /**
     * Get the current authentication info.
     *
     * @return authentication info
     */
    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        return context.getAuthentication();
    }

    /**
     * Get the current user.
     *
     * @return current user
     */
    @Nullable
    public static LoginUser getLoginUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getPrincipal() instanceof LoginUser ? (LoginUser) authentication.getPrincipal() : null;
    }

    /**
     * Get the current user's ID from the context.
     *
     * @return user ID
     */
    @Nullable
    public static Long getLoginUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getId() : null;
    }

    /**
     * Get the current user's nickname from the context.
     *
     * @return nickname
     */
    @Nullable
    public static String getLoginUserNickname() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? MapUtil.getStr(loginUser.getInfo(), LoginUser.INFO_KEY_NICKNAME) : null;
    }

    /**
     * Get the current user's department ID from the context.
     *
     * @return department ID
     */
    @Nullable
    public static Long getLoginUserDeptId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? MapUtil.getLong(loginUser.getInfo(), LoginUser.INFO_KEY_DEPT_ID) : null;
    }

    /**
     * Set the current user.
     *
     * @param loginUser login user
     * @param request   request
     */
    public static void setLoginUser(LoginUser loginUser, HttpServletRequest request) {
        // Create the Authentication and set it on the context
        Authentication authentication = buildAuthentication(loginUser, request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Additionally set on the request so ApiAccessLogFilter can read the user ID.
        // Reason: the Spring Security Filter runs after ApiAccessLogFilter; by the time access logs are recorded the
        // context no longer has user ID information.
        if (request != null) {
            WebFrameworkUtils.setLoginUserId(request, loginUser.getId());
            WebFrameworkUtils.setLoginUserType(request, loginUser.getUserType());
        }
    }

    private static Authentication buildAuthentication(LoginUser loginUser, HttpServletRequest request) {
        // Create the UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginUser, null, Collections.emptyList());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }

    /**
     * Whether to conditionally skip permission checks (including data permissions and feature permissions).
     *
     * @return whether to skip
     */
    public static boolean skipPermissionCheck() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return false;
        }
        if (loginUser.getVisitTenantId() == null) {
            return false;
        }
        // Important: permission checks cannot be performed during cross-tenant access
        return ObjUtil.notEqual(loginUser.getVisitTenantId(), loginUser.getTenantId());
    }

}
