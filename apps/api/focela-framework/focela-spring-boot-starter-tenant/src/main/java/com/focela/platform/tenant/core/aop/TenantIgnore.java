package com.focela.platform.tenant.core.aop;

import com.focela.platform.tenant.config.TenantProperties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ignore tenant: mark the specified method so that automatic tenant filtering is skipped.
 *
 * Note: only the DB scenario performs filtering; other scenarios are not filtered for now:
 * 1. Redis scenario: multi-tenancy is implemented based on Key, so ignoring is meaningless, unlike DB which uses a column.
 * 2. MQ scenario: somewhat hard to decide; currently the Consumer can manually add @TenantIgnore on the consume method to ignore.
 *
 * Special:
 * 1. If added to a Controller class, the URL is automatically added to {@link TenantProperties#getIgnoreUrls()}.
 * 2. If added to an entity class, the corresponding table name is effectively automatically added to {@link TenantProperties#getIgnoreTables()}.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TenantIgnore {

    /**
     * Whether to enable tenant ignore; default is true (enabled).
     *
     * Supports Spring EL expressions. If it returns true, the condition is met and the tenant is ignored.
     */
    String enable() default "true";

}
