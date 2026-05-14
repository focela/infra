package com.focela.platform.framework.desensitize.core.slider.annotation;

import com.focela.platform.framework.desensitize.core.base.annotation.DesensitizeBy;
import com.focela.platform.framework.desensitize.core.slider.handler.PasswordDesensitization;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Password
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = PasswordDesensitization.class)
public @interface PasswordDesensitize {

    /**
     * Prefix kept length
     */
    int prefixKeep() default 0;

    /**
     * Suffix kept length
     */
    int suffixKeep() default 0;

    /**
     * Replacement rule for password
     *
     * e.g. 123456 becomes ****** after desensitization
     */
    String replacer() default "*";

    /**
     * Whether to disable desensitization
     *
     * Supports Spring EL expressions; desensitization is skipped if the expression returns true
     */
    String disable() default "";

}
