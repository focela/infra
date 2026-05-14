package com.focela.platform.framework.desensitize.core.slider.annotation;

import com.focela.platform.framework.desensitize.core.base.annotation.DesensitizeBy;
import com.focela.platform.framework.desensitize.core.slider.handler.DefaultDesensitizationHandler;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Slider desensitization annotation
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = DefaultDesensitizationHandler.class)
public @interface SliderDesensitize {

    /**
     * Suffix kept length
     */
    int suffixKeep() default 0;

    /**
     * Replacement rule: keeps prefix and suffix, replaces everything else with replacer
     *
     * Example: prefixKeep = 1; suffixKeep = 2; replacer = "*";
     * Original string: 123456
     * After desensitization: 1***56
     */
    String replacer() default "*";

    /**
     * Prefix kept length
     */
    int prefixKeep() default 0;

    /**
     * Whether to disable desensitization
     *
     * Supports Spring EL expressions; desensitization is skipped if the expression returns true
     */
    String disable() default "";

}
