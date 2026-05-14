package com.focela.platform.framework.lock4j.core;

import com.focela.platform.framework.common.exception.ServiceException;
import com.focela.platform.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.baomidou.lock.LockFailureStrategy;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Custom lock-acquisition failure strategy that throws a {@link ServiceException}.
 */
@Slf4j
public class DefaultLockFailureStrategy implements LockFailureStrategy {

    @Override
    public void onLockFailure(String key, Method method, Object[] arguments) {
        log.debug("[onLockFailure][thread:{} failed to acquire lock, key:{} args:{}]", Thread.currentThread().getName(), key, arguments);
        throw new ServiceException(GlobalErrorCodeConstants.LOCKED);
    }
}
