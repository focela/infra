package com.focela.platform.security.core.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.api.system.oauth2.OAuth2TokenContractApi;
import com.focela.platform.common.api.system.oauth2.dto.OAuth2AccessTokenCheckRpcResponse;
import com.focela.platform.common.exception.ServiceException;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.servlet.ServletUtils;
import com.focela.platform.security.config.SecurityProperties;
import com.focela.platform.security.core.LoginUser;
import com.focela.platform.security.core.utils.SecurityFrameworkUtils;
import com.focela.platform.web.core.handler.GlobalExceptionHandler;
import com.focela.platform.web.core.utils.WebFrameworkUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Token filter that validates the token's validity.
 * After validation, retrieves the {@link LoginUser} info and adds it to the Spring Security context.
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;

    private final GlobalExceptionHandler globalExceptionHandler;

    private final OAuth2TokenContractApi oauth2TokenApi;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = SecurityFrameworkUtils.obtainAuthorization(request,
                securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StrUtil.isNotEmpty(token)) {
            Integer userType = WebFrameworkUtils.getLoginUserType(request);
            try {
                // 1.1 Build login user from token
                LoginUser loginUser = buildLoginUserByToken(token, userType);
                // 1.2 Mock Login functionality for convenience during development/debugging
                if (loginUser == null) {
                    loginUser = mockLoginUser(request, token, userType);
                }

                // 2. Set the current user
                if (loginUser != null) {
                    SecurityFrameworkUtils.setLoginUser(loginUser, request);
                }
            } catch (Throwable ex) {
                CommonResult<?> result = globalExceptionHandler.allExceptionHandler(request, ex);
                ServletUtils.writeJSON(response, result);
                return;
            }
        }

        // Continue the filter chain
        chain.doFilter(request, response);
    }

    private LoginUser buildLoginUserByToken(String token, Integer userType) {
        try {
            OAuth2AccessTokenCheckRpcResponse accessToken = oauth2TokenApi.checkAccessToken(token);
            if (accessToken == null) {
                return null;
            }
            // User type mismatch, no permission
            // Note: only /admin-api/* and /app-api/* have userType and require user type comparison.
            // WebSocket-like /ws/* connection addresses do not need user type comparison.
            if (userType != null
                    && ObjectUtil.notEqual(accessToken.getUserType(), userType)) {
                throw new AccessDeniedException("error user type");
            }
            // Build the login user
            return new LoginUser().setId(accessToken.getUserId()).setUserType(accessToken.getUserType())
                    .setInfo(accessToken.getUserInfo()) // Additional user information
                    .setTenantId(accessToken.getTenantId()).setScopes(accessToken.getScopes())
                    .setExpiresTime(accessToken.getExpiresTime());
        } catch (ServiceException serviceException) {
            // When token validation fails, since some endpoints do not require login, just return null
            return null;
        }
    }

    /**
     * Mock a login user, for convenience during development/debugging.
     *
     * Note: this feature MUST be disabled in production environments!
     *
     * @param request  request
     * @param token    mock token, format is {@link SecurityProperties#getMockSecret()} + user ID
     * @param userType user type
     * @return the mocked LoginUser
     */
    private LoginUser mockLoginUser(HttpServletRequest request, String token, Integer userType) {
        if (!securityProperties.getMockEnable()) {
            return null;
        }
        // Must start with mockSecret
        if (!token.startsWith(securityProperties.getMockSecret())) {
            return null;
        }
        // Build the mocked user
        Long userId = Long.valueOf(token.substring(securityProperties.getMockSecret().length()));
        return new LoginUser().setId(userId).setUserType(userType)
                .setTenantId(WebFrameworkUtils.getTenantId(request));
    }

}
