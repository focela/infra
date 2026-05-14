package com.focela.platform.framework.ratelimiter.core.keyresolver.impl;

import cn.hutool.crypto.SecureUtil;
import com.focela.platform.framework.common.utils.servlet.ServletUtils;
import com.focela.platform.framework.common.utils.string.StrUtils;
import com.focela.platform.framework.ratelimiter.core.annotation.RateLimiter;
import com.focela.platform.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import org.aspectj.lang.JoinPoint;

/**
 * IP-level rate limiter key resolver. Builds the key from method name + arguments + client IP.
 *
 * Uses MD5 to "compress" the key and avoid excessively long values.
 */
public class ClientIpRateLimiterKeyResolver implements RateLimiterKeyResolver {

    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        String methodName = joinPoint.getSignature().toString();
        String argsStr = StrUtils.joinMethodArgs(joinPoint);
        String clientIp = ServletUtils.getClientIP();
        return SecureUtil.md5(methodName + argsStr + clientIp);
    }

}
