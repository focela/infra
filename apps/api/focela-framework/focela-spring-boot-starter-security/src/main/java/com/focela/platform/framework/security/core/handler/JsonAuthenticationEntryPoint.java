package com.focela.platform.framework.security.core.handler;

import com.focela.platform.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.utils.servlet.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.focela.platform.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;

/**
 * When accessing a URL resource that requires authentication while not yet authenticated (logged in),
 * return the {@link GlobalErrorCodeConstants#UNAUTHORIZED} error code so the frontend redirects to the login page.
 *
 * Note: Spring Security invokes this class via the
 * {@link ExceptionTranslationFilter#sendStartAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, AuthenticationException)} method.
 */
@Slf4j
@SuppressWarnings("JavadocReference") // suppress Javadoc reference warnings
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        log.debug("[commence][access URL({}) when, no login]", request.getRequestURI(), e);
        // Return 401
        ServletUtils.writeJSON(response, CommonResult.error(UNAUTHORIZED));
    }

}
