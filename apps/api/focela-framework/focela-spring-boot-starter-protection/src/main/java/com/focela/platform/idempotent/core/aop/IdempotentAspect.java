package com.focela.platform.idempotent.core.aop;

import com.focela.platform.common.exception.ServiceException;
import com.focela.platform.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.idempotent.core.annotation.Idempotent;
import com.focela.platform.idempotent.core.keyresolver.IdempotentKeyResolver;
import com.focela.platform.idempotent.core.redis.IdempotentRedisDAO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Intercept methods annotated with {@link Idempotent} to enforce idempotent operations.
 */
@Aspect
@Slf4j
public class IdempotentAspect {

    /**
     * IdempotentKeyResolver collection.
     */
    private final Map<Class<? extends IdempotentKeyResolver>, IdempotentKeyResolver> keyResolvers;

    private final IdempotentRedisDAO idempotentRedisDAO;

    public IdempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        this.keyResolvers = CollectionUtils.convertMap(keyResolvers, IdempotentKeyResolver::getClass);
        this.idempotentRedisDAO = idempotentRedisDAO;
    }

    @Around(value = "@annotation(idempotent)")
    public Object aroundPointCut(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        // Resolve the IdempotentKeyResolver
        IdempotentKeyResolver keyResolver = keyResolvers.get(idempotent.keyResolver());
        Assert.notNull(keyResolver, "Could not find the corresponding IdempotentKeyResolver");
        // Resolve the key
        String key = keyResolver.resolver(joinPoint, idempotent);

        // 1. Lock the key
        boolean success = idempotentRedisDAO.setIfAbsent(key, idempotent.timeout(), idempotent.timeUnit());
        // If locking fails, throw an exception
        if (!success) {
            log.info("[aroundPointCut][method({}) args({}) exists duplicate request]", joinPoint.getSignature().toString(), joinPoint.getArgs());
            throw new ServiceException(GlobalErrorCodeConstants.REPEATED_REQUESTS.getCode(), idempotent.message());
        }

        // 2. Execute the business logic
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            // 3. On exception, delete the key
            // Reference: Meituan GTIS approach - https://tech.meituan.com/2016/09/29/distributed-system-mutually-exclusive-idempotence-cerberus-gtis.html
            if (idempotent.deleteKeyWhenException()) {
                idempotentRedisDAO.delete(key);
            }
            throw throwable;
        }
    }

}
