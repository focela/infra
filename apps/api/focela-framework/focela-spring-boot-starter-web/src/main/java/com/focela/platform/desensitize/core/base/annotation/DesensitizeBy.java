package com.focela.platform.desensitize.core.base.annotation;

import com.focela.platform.desensitize.core.base.handler.DesensitizationHandler;
import com.focela.platform.desensitize.core.base.serializer.StringDesensitizeSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Top-level desensitization annotation; custom annotations must use this annotation
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside // this is the meta-annotation for all other Jackson annotations; an annotation marked with it is treated as a Jackson annotation
@JsonSerialize(using = StringDesensitizeSerializer.class) // specify the serializer
public @interface DesensitizeBy {

    /**
     * Desensitization handler
     */
    @SuppressWarnings("rawtypes")
    Class<? extends DesensitizationHandler> handler();

}
