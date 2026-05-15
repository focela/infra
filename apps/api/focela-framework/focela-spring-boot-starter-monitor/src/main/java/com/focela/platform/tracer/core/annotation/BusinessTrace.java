package com.focela.platform.tracer.core.annotation;

import java.lang.annotation.*;

/**
 * Annotation that prints the business ID / business type.
 *
 * To use it, update the SkyWalking OAP Server application.yaml: edit the SW_SEARCHABLE_TAG_KEYS
 * setting to include biz.type and biz.id, then restart the SkyWalking OAP Server.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface BusinessTrace {

    /**
     * Business ID tag name.
     */
    String ID_TAG = "biz.id";
    /**
     * Business type tag name.
     */
    String TYPE_TAG = "biz.type";

    /**
     * @return operation name
     */
    String operationName() default "";

    /**
     * @return business ID
     */
    String id();

    /**
     * @return business type
     */
    String type();

}
