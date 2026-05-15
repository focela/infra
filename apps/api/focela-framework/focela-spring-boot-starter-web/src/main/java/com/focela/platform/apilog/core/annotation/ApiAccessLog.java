package com.focela.platform.apilog.core.annotation;

import com.focela.platform.apilog.core.enums.OperateTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Access log annotation
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiAccessLog {

    // ========== Switch fields ==========

    /**
     * Whether to record access log
     */
    boolean enable() default true;
    /**
     * Whether to record request parameters
     *
     * Recorded by default, mainly because request data is usually not large. Can be manually set to false to disable
     */
    boolean requestEnable() default true;
    /**
     * Whether to record response result
     *
     * Not recorded by default, mainly because response data can be relatively large. Can be manually set to true to enable
     */
    boolean responseEnable() default false;
    /**
     * Sensitive parameter array
     *
     * Once added, request parameters and response results will not record these parameters
     */
    String[] sanitizeKeys() default {};

    // ========== Module fields ==========

    /**
     * Operation module
     *
     * When empty, the {@link io.swagger.v3.oas.annotations.tags.Tag#name()} attribute will be attempted
     */
    String operateModule() default "";
    /**
     * Operation name
     *
     * When empty, the {@link io.swagger.v3.oas.annotations.Operation#summary()} attribute will be attempted
     */
    String operateName() default "";
    /**
     * Operation category
     *
     * Not actually an array; enums cannot use null as a default value
     */
    OperateTypeEnum[] operateType() default {};

}
