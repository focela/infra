package com.focela.platform.framework.desensitize.core.slider.annotation;

import com.focela.platform.framework.desensitize.core.base.annotation.DesensitizeBy;
import com.focela.platform.framework.desensitize.core.slider.handler.IdCardDesensitization;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ID card
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = IdCardDesensitization.class)
public @interface IdCardDesensitize {

    /**
     * Prefix kept length
     */
    int prefixKeep() default 6;

    /**
     * Suffix kept length
     */
    int suffixKeep() default 2;

    /**
     * Replacement rule for ID card number; e.g. 530321199204074611 becomes 530321**********11 after desensitization
     */
    String replacer() default "*";

    /**
     * Whether to disable desensitization
     *
     * Supports Spring EL expressions; desensitization is skipped if the expression returns true
     */
    String disable() default "";

}
