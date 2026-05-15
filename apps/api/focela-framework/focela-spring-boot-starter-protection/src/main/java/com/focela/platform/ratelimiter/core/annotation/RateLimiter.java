package com.focela.platform.ratelimiter.core.annotation;

import com.focela.platform.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import com.focela.platform.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import com.focela.platform.ratelimiter.core.keyresolver.impl.ClientIpRateLimiterKeyResolver;
import com.focela.platform.ratelimiter.core.keyresolver.impl.DefaultRateLimiterKeyResolver;
import com.focela.platform.ratelimiter.core.keyresolver.impl.ServerNodeRateLimiterKeyResolver;
import com.focela.platform.ratelimiter.core.keyresolver.impl.UserRateLimiterKeyResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Rate limiter annotation.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    /**
     * Rate-limiting window, default 1 second.
     */
    int time() default 1;
    /**
     * Time unit, default SECONDS.
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * Maximum number of allowed invocations.
     */
    int count() default 100;

    /**
     * Message shown when requests come in too quickly.
     *
     * @see GlobalErrorCodeConstants#TOO_MANY_REQUESTS
     */
    String message() default ""; // When empty, the TOO_MANY_REQUESTS error message is used

    /**
     * Key resolver to use.
     *
     * @see DefaultRateLimiterKeyResolver global level
     * @see UserRateLimiterKeyResolver user ID level
     * @see ClientIpRateLimiterKeyResolver client IP level
     * @see ServerNodeRateLimiterKeyResolver server node level
     * @see ExpressionIdempotentKeyResolver custom expression, computed via {@link #keyArg()}
     */
    Class<? extends RateLimiterKeyResolver> keyResolver() default DefaultRateLimiterKeyResolver.class;
    /**
     * Key argument.
     */
    String keyArg() default "";

}
