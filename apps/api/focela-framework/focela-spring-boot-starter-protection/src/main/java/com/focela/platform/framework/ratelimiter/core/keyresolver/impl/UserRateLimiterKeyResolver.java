package com.focela.platform.framework.ratelimiter.core.keyresolver.impl;

import cn.hutool.crypto.SecureUtil;
import com.focela.platform.framework.common.utils.string.StrUtils;
import com.focela.platform.framework.ratelimiter.core.annotation.RateLimiter;
import com.focela.platform.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import com.focela.platform.framework.web.core.utils.WebFrameworkUtils;
import org.aspectj.lang.JoinPoint;

/**
 * User-level rate limiter key resolver. Builds the key from method name + arguments + userId + userType.
 *
 * Uses MD5 to "compress" the key and avoid excessively long values.
 */
public class UserRateLimiterKeyResolver implements RateLimiterKeyResolver {

    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        String methodName = joinPoint.getSignature().toString();
        String argsStr = StrUtils.joinMethodArgs(joinPoint);
        Long userId = WebFrameworkUtils.getLoginUserId();
        Integer userType = WebFrameworkUtils.getLoginUserType();
        return SecureUtil.md5(methodName + argsStr + userId + userType);
    }

}
