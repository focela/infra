package com.focela.platform.framework.tenant.core.web;

import cn.hutool.core.util.ObjUtil;
import com.focela.platform.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.framework.security.core.LoginUser;
import com.focela.platform.framework.security.core.service.SecurityFrameworkService;
import com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils;
import com.focela.platform.framework.tenant.config.TenantProperties;
import com.focela.platform.framework.tenant.core.context.TenantContextHolder;
import com.focela.platform.framework.web.core.utils.WebFrameworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.focela.platform.framework.common.exception.utils.ServiceExceptionUtils.exception0;

@RequiredArgsConstructor
@Slf4j
public class TenantVisitContextInterceptor implements HandlerInterceptor {

    private static final String PERMISSION = "system:tenant:visit";

    private final TenantProperties tenantProperties;

    private final SecurityFrameworkService securityFrameworkService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // If it matches the current tenant ID, skip directly
        Long visitTenantId = WebFrameworkUtils.getVisitTenantId(request);
        if (visitTenantId == null) {
            return true;
        }
        if (ObjUtil.equal(visitTenantId, TenantContextHolder.getTenantId())) {
            return true;
        }
        // Must be a logged-in user
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return true;
        }

        // Verify whether the user is allowed to switch tenant
        if (!securityFrameworkService.hasAnyPermissions(PERMISSION)) {
            throw exception0(GlobalErrorCodeConstants.FORBIDDEN.getCode(), "You are not authorized to switch tenant");
        }

        // [IMPORTANT] switch tenant ID
        loginUser.setVisitTenantId(visitTenantId);
        TenantContextHolder.setTenantId(visitTenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // [IMPORTANT] clean up the switch, restore the original tenant ID
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null && loginUser.getTenantId() != null) {
            TenantContextHolder.setTenantId(loginUser.getTenantId());
        }
    }

}
