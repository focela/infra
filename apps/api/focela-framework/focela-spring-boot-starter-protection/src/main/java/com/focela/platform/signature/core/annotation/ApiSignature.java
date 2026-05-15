package com.focela.platform.signature.core.annotation;

import com.focela.platform.common.exception.enums.GlobalErrorCodeConstants;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * HTTP API signature annotation.
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSignature {

    /**
     * Validity window for the same request, default 60 seconds.
     */
    int timeout() default 60;

    /**
     * Time unit, default SECONDS.
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    // ========================== Signature parameters ==========================

    /**
     * Message shown when signature verification fails.
     *
     * @see GlobalErrorCodeConstants#BAD_REQUEST
     */
    String message() default "Invalid signature"; // When empty, the BAD_REQUEST error message is used

    /**
     * Signature field: appId (application ID).
     */
    String appId() default "appId";

    /**
     * Signature field: timestamp.
     */
    String timestamp() default "timestamp";

    /**
     * Signature field: nonce (random value, at least 10 characters).
     */
    String nonce() default "nonce";

    /**
     * sign: the client-side signature.
     */
    String sign() default "sign";

}
