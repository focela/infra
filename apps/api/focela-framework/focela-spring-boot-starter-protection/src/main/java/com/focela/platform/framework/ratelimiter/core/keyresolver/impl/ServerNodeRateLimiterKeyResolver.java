package com.focela.platform.framework.ratelimiter.core.keyresolver.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.system.SystemUtil;
import com.focela.platform.framework.common.utils.string.StrUtils;
import com.focela.platform.framework.ratelimiter.core.annotation.RateLimiter;
import com.focela.platform.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import org.aspectj.lang.JoinPoint;

/**
 * Server-node level rate limiter key resolver. Builds the key from method name + arguments + server node info.
 *
 * Uses MD5 to "compress" the key and avoid excessively long values.
 */
public class ServerNodeRateLimiterKeyResolver implements RateLimiterKeyResolver {

    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        String methodName = joinPoint.getSignature().toString();
        String argsStr = StrUtils.joinMethodArgs(joinPoint);
        String serverNode = String.format("%s@%d", SystemUtil.getHostInfo().getAddress(), SystemUtil.getCurrentPID());
        return SecureUtil.md5(methodName + argsStr + serverNode);
    }

}