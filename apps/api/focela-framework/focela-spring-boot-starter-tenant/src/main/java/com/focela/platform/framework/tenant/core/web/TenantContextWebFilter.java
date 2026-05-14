package com.focela.platform.framework.tenant.core.web;

import com.focela.platform.framework.tenant.core.context.TenantContextHolder;
import com.focela.platform.framework.web.core.utils.WebFrameworkUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Multi-tenant Context Web filter
 * Parses the tenant-id from the request Header and adds it to {@link TenantContextHolder}, so that subsequent DB and other operations can obtain the tenant ID.
 */
public class TenantContextWebFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Set
        Long tenantId = WebFrameworkUtils.getTenantId(request);
        if (tenantId != null) {
            TenantContextHolder.setTenantId(tenantId);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            // Clean up
            TenantContextHolder.clear();
        }
    }

}
