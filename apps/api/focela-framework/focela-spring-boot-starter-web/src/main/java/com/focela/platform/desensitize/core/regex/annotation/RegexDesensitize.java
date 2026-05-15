package com.focela.platform.desensitize.core.regex.annotation;

import com.focela.platform.desensitize.core.base.annotation.DesensitizeBy;
import com.focela.platform.desensitize.core.regex.handler.DefaultRegexDesensitizationHandler;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Regex desensitization annotation
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = DefaultRegexDesensitizationHandler.class)
public @interface RegexDesensitize {

    /**
     * Regular expression to match (defaults to matching all)
     */
    String regex() default "^[\\s\\S]*$";

    /**
     * Replacement rule: all matched strings are replaced with replacer
     *
     * Example: regex=123; replacer=******
     * Original string: 123456789
     * Desensitized string: ******456789
     */
    String replacer() default "******";

    /**
     * Whether to disable desensitization
     *
     * Supports Spring EL expressions; desensitization is skipped if the expression returns true
     */
    String disable() default "";

}
