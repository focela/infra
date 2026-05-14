package com.focela.platform.framework.idempotent.core.keyresolver.impl;

import cn.hutool.crypto.SecureUtil;
import com.focela.platform.framework.common.utils.string.StrUtils;
import com.focela.platform.framework.idempotent.core.annotation.Idempotent;
import com.focela.platform.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import org.aspectj.lang.JoinPoint;

/**
 * Default (global level) idempotent key resolver. Builds the key from the method name and arguments.
 *
 * Uses MD5 to "compress" the key and avoid excessively long values.
 */
public class DefaultIdempotentKeyResolver implements IdempotentKeyResolver {

    @Override
    public String resolver(JoinPoint joinPoint, Idempotent idempotent) {
        String methodName = joinPoint.getSignature().toString();
        String argsStr = StrUtils.joinMethodArgs(joinPoint);
        return SecureUtil.md5(methodName + argsStr);
    }

}
