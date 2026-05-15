package com.focela.platform.datapermission.core.annotation;

import com.focela.platform.datapermission.core.rule.DataPermissionRule;

import java.lang.annotation.*;

/**
 * Data permission annotation.
 * Can be declared on a class or method to indicate the data permission rules in use.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    /**
     * Whether data permission is enabled for the current class or method.
     * Even without an @DataPermission annotation it defaults to enabled.
     * Set enable to false to disable it.
     */
    boolean enable() default true;

    /**
     * Array of data permission rules to apply. Takes priority over {@link #excludeRules()}.
     */
    Class<? extends DataPermissionRule>[] includeRules() default {};

    /**
     * Array of data permission rules to exclude. Lowest priority.
     */
    Class<? extends DataPermissionRule>[] excludeRules() default {};

}
