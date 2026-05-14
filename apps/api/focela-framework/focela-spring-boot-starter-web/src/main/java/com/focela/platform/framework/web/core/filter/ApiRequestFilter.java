package com.focela.platform.framework.web.core.filter;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.web.config.WebProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Filter for API requests such as /admin-api, /app-api
 */
@RequiredArgsConstructor
public abstract class ApiRequestFilter extends OncePerRequestFilter {

    protected final WebProperties webProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // only filter API request URLs
        String apiUri = request.getRequestURI().substring(request.getContextPath().length());
        return !StrUtil.startWithAny(apiUri, webProperties.getAdminApi().getPrefix(), webProperties.getAppApi().getPrefix());
    }

}
