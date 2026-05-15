package com.focela.platform.desensitize.core.slider.annotation;

import com.focela.platform.desensitize.core.base.annotation.DesensitizeBy;
import com.focela.platform.desensitize.core.slider.handler.CarLicenseDesensitization;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Car license plate
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = CarLicenseDesensitization.class)
public @interface CarLicenseDesensitize {

    /**
     * Prefix kept length
     */
    int prefixKeep() default 3;

    /**
     * Suffix kept length
     */
    int suffixKeep() default 1;

    /**
     * Replacement rule for car license plate; e.g. 粤A66666 becomes 粤A6***6 after desensitization
     */
    String replacer() default "*";

    /**
     * Whether to disable desensitization
     *
     * Supports Spring EL expressions; desensitization is skipped if the expression returns true
     */
    String disable() default "";

}
