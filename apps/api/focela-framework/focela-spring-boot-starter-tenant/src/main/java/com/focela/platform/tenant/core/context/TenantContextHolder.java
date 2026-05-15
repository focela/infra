package com.focela.platform.tenant.core.context;

import com.focela.platform.common.enums.DocumentEnum;
import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * Multi-tenant context Holder
 */
public class TenantContextHolder {

    /**
     * Current tenant ID
     */
    private static final ThreadLocal<Long> TENANT_ID = new TransmittableThreadLocal<>();

    /**
     * Whether to ignore tenant
     */
    private static final ThreadLocal<Boolean> IGNORE = new TransmittableThreadLocal<>();

    /**
     * Get the tenant ID
     *
     * @return tenant ID
     */
    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * Get the tenant ID. Throws NullPointerException if not present.
     *
     * @return tenant ID
     */
    public static Long getRequiredTenantId() {
        Long tenantId = getTenantId();
        if (tenantId == null) {
            throw new NullPointerException("TenantContextHolder does not exist tenant ID! see docs:"
                + DocumentEnum.TENANT.getUrl());
        }
        return tenantId;
    }

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static void setIgnore(Boolean ignore) {
        IGNORE.set(ignore);
    }

    /**
     * Whether tenant is currently ignored
     *
     * @return whether to ignore
     */
    public static boolean isIgnore() {
        return Boolean.TRUE.equals(IGNORE.get());
    }

    public static void clear() {
        TENANT_ID.remove();
        IGNORE.remove();
    }

}
