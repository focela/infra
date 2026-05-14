package com.focela.platform.framework.datapermission.core.aop;

import com.focela.platform.framework.datapermission.core.annotation.DataPermission;
import lombok.Getter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.MethodClassKey;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interceptor for the {@link DataPermission} annotation.
 * 1. Before method execution, push the @DataPermission annotation onto the stack.
 * 2. After method execution, pop the @DataPermission annotation off the stack.
 */
@DataPermission // This annotation is used as the {@link DATA_PERMISSION_NULL} placeholder
public class DataPermissionAnnotationInterceptor implements MethodInterceptor {

    /**
     * Empty DataPermission placeholder. Used when a method has no {@link DataPermission} annotation,
     * stored as DATA_PERMISSION_NULL to indicate absence.
     */
    static final DataPermission DATA_PERMISSION_NULL = DataPermissionAnnotationInterceptor.class.getAnnotation(DataPermission.class);

    @Getter
    private final Map<MethodClassKey, DataPermission> dataPermissionCache = new ConcurrentHashMap<>();

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        // Push
        DataPermission dataPermission = this.findAnnotation(methodInvocation);
        if (dataPermission != null) {
            DataPermissionContextHolder.add(dataPermission);
        }
        try {
            // Execute logic
            return methodInvocation.proceed();
        } finally {
            // Pop
            if (dataPermission != null) {
                DataPermissionContextHolder.remove();
            }
        }
    }

    private DataPermission findAnnotation(MethodInvocation methodInvocation) {
        // 1. Try the cache first
        Method method = methodInvocation.getMethod();
        Object targetObject = methodInvocation.getThis();
        Class<?> clazz = targetObject != null ? targetObject.getClass() : method.getDeclaringClass();
        MethodClassKey methodClassKey = new MethodClassKey(method, clazz);
        DataPermission dataPermission = dataPermissionCache.get(methodClassKey);
        if (dataPermission != null) {
            return dataPermission != DATA_PERMISSION_NULL ? dataPermission : null;
        }

        // 2.1 Look it up from the method
        dataPermission = AnnotationUtils.findAnnotation(method, DataPermission.class);
        // 2.2 Look it up from the class
        if (dataPermission == null) {
            dataPermission = AnnotationUtils.findAnnotation(clazz, DataPermission.class);
        }
        // 2.3 Store the result in the cache
        dataPermissionCache.put(methodClassKey, dataPermission != null ? dataPermission : DATA_PERMISSION_NULL);
        return dataPermission;
    }

}
