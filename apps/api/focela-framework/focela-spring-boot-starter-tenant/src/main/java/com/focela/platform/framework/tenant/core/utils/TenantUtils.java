package com.focela.platform.framework.tenant.core.utils;

import com.focela.platform.framework.tenant.core.context.TenantContextHolder;

import java.util.Map;
import java.util.concurrent.Callable;

import static com.focela.platform.framework.web.core.utils.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Multi-tenant Util
 */
public class TenantUtils {

    /**
     * Execute the corresponding logic using the specified tenant.
     *
     * Note: if the tenant is currently being ignored, it will be forcibly set to not-ignore.
     * Of course, after execution completes, it will be restored.
     *
     * @param tenantId tenant ID
     * @param runnable logic
     */
    public static void execute(Long tenantId, Runnable runnable) {
        Long oldTenantId = TenantContextHolder.getTenantId();
        Boolean oldIgnore = TenantContextHolder.isIgnore();
        try {
            TenantContextHolder.setTenantId(tenantId);
            TenantContextHolder.setIgnore(false);
            // Execute logic
            runnable.run();
        } finally {
            TenantContextHolder.setTenantId(oldTenantId);
            TenantContextHolder.setIgnore(oldIgnore);
        }
    }

    /**
     * Execute the corresponding logic using the specified tenant.
     *
     * Note: if the tenant is currently being ignored, it will be forcibly set to not-ignore.
     * Of course, after execution completes, it will be restored.
     *
     * @param tenantId tenant ID
     * @param callable logic
     * @return result
     */
    public static <V> V execute(Long tenantId, Callable<V> callable) {
        Long oldTenantId = TenantContextHolder.getTenantId();
        Boolean oldIgnore = TenantContextHolder.isIgnore();
        try {
            TenantContextHolder.setTenantId(tenantId);
            TenantContextHolder.setIgnore(false);
            // Execute logic
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            TenantContextHolder.setTenantId(oldTenantId);
            TenantContextHolder.setIgnore(oldIgnore);
        }
    }

    /**
     * Ignore tenant and execute the corresponding logic.
     *
     * @param runnable logic
     */
    public static void executeIgnore(Runnable runnable) {
        Boolean oldIgnore = TenantContextHolder.isIgnore();
        try {
            TenantContextHolder.setIgnore(true);
            // Execute logic
            runnable.run();
        } finally {
            TenantContextHolder.setIgnore(oldIgnore);
        }
    }

    /**
     * Ignore tenant and execute the corresponding logic.
     *
     * @param callable logic
     * @return result
     */
    public static <V> V executeIgnore(Callable<V> callable) {
        Boolean oldIgnore = TenantContextHolder.isIgnore();
        try {
            TenantContextHolder.setIgnore(true);
            // Execute logic
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            TenantContextHolder.setIgnore(oldIgnore);
        }
    }

    /**
     * Add the tenant ID to the header.
     *
     * @param headers HTTP request headers
     * @param tenantId tenant ID
     */
    public static void addTenantHeader(Map<String, String> headers, Long tenantId) {
        if (tenantId != null) {
            headers.put(HEADER_TENANT_ID, tenantId.toString());
        }
    }

}
