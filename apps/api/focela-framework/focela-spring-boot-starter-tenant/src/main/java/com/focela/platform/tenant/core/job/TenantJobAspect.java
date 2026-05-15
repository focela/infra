package com.focela.platform.tenant.core.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.utils.json.JsonUtils;
import com.focela.platform.tenant.core.service.TenantFrameworkService;
import com.focela.platform.tenant.core.utils.TenantUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Multi-tenant JobHandler AOP
 * When the task is executed, the Job logic is executed for each tenant one by one.
 *
 * Note: the JobHandler must be idempotent. Because when a Job is retried due to a failure for some tenant, tenants
 * that previously executed successfully will also be executed again.
 */
@Aspect
@RequiredArgsConstructor
@Slf4j
public class TenantJobAspect {

    private final TenantFrameworkService tenantFrameworkService;

    @Around("@annotation(tenantJob)")
    public String around(ProceedingJoinPoint joinPoint, TenantJob tenantJob) {
        // Get the tenant list
        List<Long> tenantIds = tenantFrameworkService.getTenantIds();
        if (CollUtil.isEmpty(tenantIds)) {
            return null;
        }

        // Execute the Job for each tenant
        Map<Long, String> results = new ConcurrentHashMap<>();
        tenantIds.parallelStream().forEach(tenantId -> {
            // TODO: first use parallel to run in parallel; 1) multiple tenants share a single execution log; 2) handle exception cases
            TenantUtils.execute(tenantId, () -> {
                try {
                    Object result = joinPoint.proceed();
                    results.put(tenantId, StrUtil.toStringOrEmpty(result));
                } catch (Throwable e) {
                    log.error("[execute][tenant ({}) execute Job encountered exception", tenantId, e);
                    results.put(tenantId, ExceptionUtil.getRootCauseMessage(e));
                }
            });
        });
        return JsonUtils.toJsonString(results);
    }

}
