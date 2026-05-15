package com.focela.platform.idempotent.core.keyresolver.impl;

import cn.hutool.crypto.SecureUtil;
import com.focela.platform.common.utils.string.StrUtils;
import com.focela.platform.idempotent.core.annotation.Idempotent;
import com.focela.platform.idempotent.core.keyresolver.IdempotentKeyResolver;
import com.focela.platform.web.core.utils.WebFrameworkUtils;
import org.aspectj.lang.JoinPoint;

/**
 * User-level idempotent key resolver. Builds the key from method name + arguments + userId + userType.
 *
 * Uses MD5 to "compress" the key and avoid excessively long values.
 */
public class UserIdempotentKeyResolver implements IdempotentKeyResolver {

    @Override
    public String resolver(JoinPoint joinPoint, Idempotent idempotent) {
        String methodName = joinPoint.getSignature().toString();
        String argsStr = StrUtils.joinMethodArgs(joinPoint);
        Long userId = WebFrameworkUtils.getLoginUserId();
        Integer userType = WebFrameworkUtils.getLoginUserType();
        return SecureUtil.md5(methodName + argsStr + userId + userType);
    }

}
