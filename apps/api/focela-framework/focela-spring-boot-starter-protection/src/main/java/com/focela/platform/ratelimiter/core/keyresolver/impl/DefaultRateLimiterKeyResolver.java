package com.focela.platform.ratelimiter.core.keyresolver.impl;

import cn.hutool.crypto.SecureUtil;
import com.focela.platform.common.utils.string.StrUtils;
import com.focela.platform.ratelimiter.core.annotation.RateLimiter;
import com.focela.platform.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import org.aspectj.lang.JoinPoint;

/**
 * Default (global level) rate limiter key resolver. Builds the key from method name and arguments.
 *
 * Uses MD5 to "compress" the key and avoid excessively long values.
 */
public class DefaultRateLimiterKeyResolver implements RateLimiterKeyResolver {

    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        String methodName = joinPoint.getSignature().toString();
        String argsStr = StrUtils.joinMethodArgs(joinPoint);
        return SecureUtil.md5(methodName + argsStr);
    }

}
