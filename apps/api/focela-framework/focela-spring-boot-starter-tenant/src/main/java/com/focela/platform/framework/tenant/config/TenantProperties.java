package com.focela.platform.framework.tenant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Multi-tenant configuration
 */
@ConfigurationProperties(prefix = "focela.tenant")
@Data
public class TenantProperties {

    /**
     * Whether the tenant feature is enabled
     */
    private static final Boolean ENABLE_DEFAULT = true;

    /**
     * Whether enabled
     */
    private Boolean enable = ENABLE_DEFAULT;

    /**
     * Requests that should ignore multi-tenancy
     *
     * By default, every request must carry the tenant-id header. However, some requests do not need to carry it,
     * such as SMS callbacks, payment callbacks, and other Open APIs.
     */
    private Set<String> ignoreUrls = new HashSet<>();

    /**
     * Requests that should ignore cross-tenant (switching) access
     *
     * Reason: some endpoints access personal information which cannot be obtained across tenants.
     */
    private Set<String> ignoreVisitUrls = Collections.emptySet();

    /**
     * Tables that should ignore multi-tenancy
     *
     * By default, all tables have multi-tenancy enabled, so remember to add the corresponding tenant_id column.
     */
    private Set<String> ignoreTables = Collections.emptySet();

    /**
     * Spring Cache caches that should ignore multi-tenancy
     *
     * By default, all caches have multi-tenancy enabled, so remember to add the corresponding tenant_id field.
     */
    private Set<String> ignoreCaches = Collections.emptySet();

}
