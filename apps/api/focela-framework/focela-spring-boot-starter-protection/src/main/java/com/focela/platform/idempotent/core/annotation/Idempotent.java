package com.focela.platform.idempotent.core.annotation;

import com.focela.platform.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import com.focela.platform.idempotent.core.keyresolver.IdempotentKeyResolver;
import com.focela.platform.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import com.focela.platform.idempotent.core.keyresolver.impl.UserIdempotentKeyResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Idempotent annotation.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * Idempotency timeout, default 1 second.
     *
     * Note: if execution exceeds this timeout, subsequent requests will still be accepted.
     */
    int timeout() default 1;
    /**
     * Time unit, default SECONDS.
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * Message shown while an execution is in progress.
     */
    String message() default "Duplicate request, please retry later";

    /**
     * Key resolver to use.
     *
     * @see DefaultIdempotentKeyResolver global level
     * @see UserIdempotentKeyResolver user level
     * @see ExpressionIdempotentKeyResolver custom expression, computed via {@link #keyArg()}
     */
    Class<? extends IdempotentKeyResolver> keyResolver() default DefaultIdempotentKeyResolver.class;
    /**
     * Key argument.
     */
    String keyArg() default "";

    /**
     * Whether to delete the key when an exception occurs.
     *
     * Q: Why delete the key on exception?
     * A: An exception means the business call failed, so the key must be removed so subsequent
     *    requests can execute normally.
     *
     * Q: Why not also add a {@code deleteWhenSuccess} option for successful execution?
     * A: That scenario is essentially a distributed lock; use the {@code @Lock4j} annotation instead.
     */
    boolean deleteKeyWhenException() default true;

}
