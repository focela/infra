package com.focela.platform.framework.desensitize.core.slider.annotation;

import com.focela.platform.framework.desensitize.core.base.annotation.DesensitizeBy;
import com.focela.platform.framework.desensitize.core.slider.handler.FixedPhoneDesensitization;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fixed phone
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = FixedPhoneDesensitization.class)
public @interface FixedPhoneDesensitize {

    /**
     * Prefix kept length
     */
    int prefixKeep() default 4;

    /**
     * Suffix kept length
     */
    int suffixKeep() default 2;

    /**
     * Replacement rule for fixed phone; e.g. 01086551122 becomes 0108*****22 after desensitization
     */
    String replacer() default "*";

    /**
     * Whether to disable desensitization
     *
     * Supports Spring EL expressions; desensitization is skipped if the expression returns true
     */
    String disable() default "";

}
