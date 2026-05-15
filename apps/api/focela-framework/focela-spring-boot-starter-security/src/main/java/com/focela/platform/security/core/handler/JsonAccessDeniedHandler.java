package com.focela.platform.security.core.handler;

import com.focela.platform.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.security.core.utils.SecurityFrameworkUtils;
import com.focela.platform.common.utils.servlet.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.focela.platform.common.exception.enums.GlobalErrorCodeConstants.FORBIDDEN;

/**
 * When accessing a URL resource that requires authentication while authenticated (logged in) but lacking permission,
 * return the {@link GlobalErrorCodeConstants#FORBIDDEN} error code.
 *
 * Note: Spring Security invokes this class via the
 * {@link ExceptionTranslationFilter#handleAccessDeniedException(HttpServletRequest, HttpServletResponse, FilterChain, AccessDeniedException)} method.
 */
@Slf4j
@SuppressWarnings("JavadocReference")
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
            throws IOException, ServletException {
        // Log at warn level so we can periodically review warnings for malicious activity
        log.warn("[commence][access URL({}) when, user ({}) permission insufficient]", request.getRequestURI(),
                SecurityFrameworkUtils.getLoginUserId(), e);
        // Return 403
        ServletUtils.writeJSON(response, CommonResult.error(FORBIDDEN));
    }

}
