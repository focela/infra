package com.focela.platform.framework.tenant.core.aop;

import com.focela.platform.framework.common.utils.spring.SpringExpressionUtils;
import com.focela.platform.framework.tenant.core.context.TenantContextHolder;
import com.focela.platform.framework.tenant.core.utils.TenantUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Aspect for ignoring multi-tenancy, implemented based on the {@link TenantIgnore} annotation, used for some global logic.
 * For example, a scheduled task that reads all data and processes it.
 * Or, reading all data and caching it.
 *
 * The overall logic implementation must remain consistent with {@link TenantUtils#executeIgnore(Runnable)}.
 */
@Aspect
@Slf4j
public class TenantIgnoreAspect {

    @Around("@annotation(tenantIgnore)")
    public Object around(ProceedingJoinPoint joinPoint, TenantIgnore tenantIgnore) throws Throwable {
        Boolean oldIgnore = TenantContextHolder.isIgnore();
        try {
            // Evaluate the condition; only ignore if it is satisfied
            Object enable = SpringExpressionUtils.parseExpression(tenantIgnore.enable());
            if (Boolean.TRUE.equals(enable)) {
                TenantContextHolder.setIgnore(true);
            }

            // Execute logic
            return joinPoint.proceed();
        } finally {
            TenantContextHolder.setIgnore(oldIgnore);
        }
    }

}
