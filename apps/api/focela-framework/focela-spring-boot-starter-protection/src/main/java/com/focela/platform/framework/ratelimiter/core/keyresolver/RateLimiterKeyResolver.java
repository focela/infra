package com.focela.platform.framework.ratelimiter.core.keyresolver;

import com.focela.platform.framework.ratelimiter.core.annotation.RateLimiter;
import org.aspectj.lang.JoinPoint;

/**
 * Rate limiter key resolver interface.
 */
public interface RateLimiterKeyResolver {

    /**
     * Resolve a key.
     *
     * @param rateLimiter the rate limiter annotation
     * @param joinPoint   the AOP join point
     * @return resolved key
     */
    String resolver(JoinPoint joinPoint, RateLimiter rateLimiter);

}
