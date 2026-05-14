package com.focela.platform.framework.ratelimiter.core.aop;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.exception.ServiceException;
import com.focela.platform.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.framework.common.utils.collection.CollectionUtils;
import com.focela.platform.framework.ratelimiter.core.annotation.RateLimiter;
import com.focela.platform.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import com.focela.platform.framework.ratelimiter.core.redis.RateLimiterRedisDAO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Intercept methods annotated with {@link RateLimiter} to enforce rate limiting.
 */
@Aspect
@Slf4j
public class RateLimiterAspect {

    /**
     * RateLimiterKeyResolver collection.
     */
    private final Map<Class<? extends RateLimiterKeyResolver>, RateLimiterKeyResolver> keyResolvers;

    private final RateLimiterRedisDAO rateLimiterRedisDAO;

    public RateLimiterAspect(List<RateLimiterKeyResolver> keyResolvers, RateLimiterRedisDAO rateLimiterRedisDAO) {
        this.keyResolvers = CollectionUtils.convertMap(keyResolvers, RateLimiterKeyResolver::getClass);
        this.rateLimiterRedisDAO = rateLimiterRedisDAO;
    }

    @Before("@annotation(rateLimiter)")
    public void beforePointCut(JoinPoint joinPoint, RateLimiter rateLimiter) {
        // Resolve the RateLimiterKeyResolver
        RateLimiterKeyResolver keyResolver = keyResolvers.get(rateLimiter.keyResolver());
        Assert.notNull(keyResolver, "Could not find the corresponding RateLimiterKeyResolver");
        // Resolve the key
        String key = keyResolver.resolver(joinPoint, rateLimiter);

        // Try to acquire one permit
        boolean success = rateLimiterRedisDAO.tryAcquire(key,
                rateLimiter.count(), rateLimiter.time(), rateLimiter.timeUnit());
        if (!success) {
            log.info("[beforePointCut][method({}) args({}) requests too frequent]", joinPoint.getSignature().toString(), joinPoint.getArgs());
            String message = StrUtil.blankToDefault(rateLimiter.message(),
                    GlobalErrorCodeConstants.TOO_MANY_REQUESTS.getMsg());
            throw new ServiceException(GlobalErrorCodeConstants.TOO_MANY_REQUESTS.getCode(), message);
        }
    }

}

