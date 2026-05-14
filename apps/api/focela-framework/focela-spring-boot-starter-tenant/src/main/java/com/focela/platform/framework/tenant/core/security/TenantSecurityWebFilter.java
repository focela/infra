package com.focela.platform.framework.tenant.core.security;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.utils.servlet.ServletUtils;
import com.focela.platform.framework.security.core.LoginUser;
import com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils;
import com.focela.platform.framework.tenant.config.TenantProperties;
import com.focela.platform.framework.tenant.core.context.TenantContextHolder;
import com.focela.platform.framework.tenant.core.service.TenantFrameworkService;
import com.focela.platform.framework.web.config.WebProperties;
import com.focela.platform.framework.web.core.filter.ApiRequestFilter;
import com.focela.platform.framework.web.core.handler.GlobalExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

/**
 * Multi-tenant Security Web filter
 * 1. For a logged-in user, verify whether they have permission to access this tenant, to avoid privilege escalation.
 * 2. If the request does not carry a tenant ID, check whether it is an ignored URL; otherwise access is not allowed.
 * 3. Verify the tenant is valid, e.g., not disabled, not expired.
 */
@Slf4j
public class TenantSecurityWebFilter extends ApiRequestFilter {

    private final TenantProperties tenantProperties;

    /**
     * List of URLs allowed to ignore tenant
     *
     * Purpose: resolve the issue where modifying configuration causes the @TenantIgnore Controller endpoint filter to become ineffective.
     */
    private final Set<String> ignoreUrls;

    private final AntPathMatcher pathMatcher;

    private final GlobalExceptionHandler globalExceptionHandler;
    private final TenantFrameworkService tenantFrameworkService;

    public TenantSecurityWebFilter(WebProperties webProperties,
                                   TenantProperties tenantProperties,
                                   Set<String> ignoreUrls,
                                   GlobalExceptionHandler globalExceptionHandler,
                                   TenantFrameworkService tenantFrameworkService) {
        super(webProperties);
        this.tenantProperties = tenantProperties;
        this.ignoreUrls = ignoreUrls;
        this.pathMatcher = new AntPathMatcher();
        this.globalExceptionHandler = globalExceptionHandler;
        this.tenantFrameworkService = tenantFrameworkService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Long tenantId = TenantContextHolder.getTenantId();
        // 1. For a logged-in user, verify whether they have permission to access this tenant, to avoid privilege escalation.
        LoginUser user = SecurityFrameworkUtils.getLoginUser();
        if (user != null) {
            // If the tenant ID cannot be obtained, try to use the logged-in user's tenant ID
            if (tenantId == null) {
                tenantId = user.getTenantId();
                TenantContextHolder.setTenantId(tenantId);
            // If a tenant ID is provided, compare it to prevent privilege escalation
            } else if (!Objects.equals(user.getTenantId(), TenantContextHolder.getTenantId())) {
                log.error("[doFilterInternal][tenant ({}) User({}/{}) unauthorized access to tenant ({}) URL({}/{})]",
                        user.getTenantId(), user.getId(), user.getUserType(),
                        TenantContextHolder.getTenantId(), request.getRequestURI(), request.getMethod());
                ServletUtils.writeJSON(response, CommonResult.error(GlobalErrorCodeConstants.FORBIDDEN.getCode(),
                        "You are not authorized to access this tenant's data"));
                return;
            }
        }

        // If not an URL allowed to ignore tenant, verify the tenant is valid
        if (!isIgnoreUrl(request)) {
            // 2. If the request does not carry a tenant ID, access is not allowed.
            if (tenantId == null) {
                log.error("[doFilterInternal][URL({}/{}) did not pass tenant ID]", request.getRequestURI(), request.getMethod());
                ServletUtils.writeJSON(response, CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(),
                        "The tenant identifier of the request was not passed, please check"));
                return;
            }
            // 3. Verify the tenant is valid, e.g., not disabled, not expired
            try {
                tenantFrameworkService.validTenant(tenantId);
            } catch (Throwable ex) {
                CommonResult<?> result = globalExceptionHandler.allExceptionHandler(request, ex);
                ServletUtils.writeJSON(response, result);
                return;
            }
        } else { // For URLs allowed to ignore tenant: if no tenant ID is passed, default to ignoring the tenant ID to avoid errors
            if (tenantId == null) {
                TenantContextHolder.setIgnore(true);
            }
        }

        // Continue filtering
        chain.doFilter(request, response);
    }

    private boolean isIgnoreUrl(HttpServletRequest request) {
        String apiUri = request.getRequestURI().substring(request.getContextPath().length());
        // Fast match for performance
        if (CollUtil.contains(tenantProperties.getIgnoreUrls(), apiUri)
            || CollUtil.contains(ignoreUrls, apiUri)) {
            return true;
        }
        // Match each Ant path
        for (String url : tenantProperties.getIgnoreUrls()) {
            if (pathMatcher.match(url, apiUri)) {
                return true;
            }
        }
        for (String url : ignoreUrls) {
            if (pathMatcher.match(url, apiUri)) {
                return true;
            }
        }
        return false;
    }

}
