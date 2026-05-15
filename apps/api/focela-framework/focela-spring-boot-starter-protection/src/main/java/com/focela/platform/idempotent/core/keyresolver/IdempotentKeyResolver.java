package com.focela.platform.idempotent.core.keyresolver;

import com.focela.platform.idempotent.core.annotation.Idempotent;
import org.aspectj.lang.JoinPoint;

/**
 * Idempotent key resolver interface.
 */
public interface IdempotentKeyResolver {

    /**
     * Resolve a key.
     *
     * @param idempotent the idempotent annotation
     * @param joinPoint  the AOP join point
     * @return resolved key
     */
    String resolver(JoinPoint joinPoint, Idempotent idempotent);

}
