package com.focela.platform.framework.datapermission.core.utils;

import com.focela.platform.framework.datapermission.core.annotation.DataPermission;
import com.focela.platform.framework.datapermission.core.aop.DataPermissionContextHolder;
import lombok.SneakyThrows;

import java.util.concurrent.Callable;

/**
 * Data permission utilities.
 */
public class DataPermissionUtils {

    private static DataPermission DATA_PERMISSION_DISABLE;

    @DataPermission(enable = false)
    @SneakyThrows
    private static DataPermission getDisableDataPermissionDisable() {
        if (DATA_PERMISSION_DISABLE == null) {
            DATA_PERMISSION_DISABLE = DataPermissionUtils.class
                    .getDeclaredMethod("getDisableDataPermissionDisable")
                    .getAnnotation(DataPermission.class);
        }
        return DATA_PERMISSION_DISABLE;
    }

    /**
     * Run the given logic while ignoring data permission.
     *
     * @param runnable logic to run
     */
    public static void executeIgnore(Runnable runnable) {
        addDisableDataPermission();
        try {
            // Execute runnable
            runnable.run();
        } finally {
            removeDataPermission();
        }
    }

    /**
     * Run the given logic while ignoring data permission.
     *
     * @param callable logic to run
     * @return the execution result
     */
    @SneakyThrows
    public static <T> T executeIgnore(Callable<T> callable) {
        addDisableDataPermission();
        try {
            // Execute callable
            return callable.call();
        } finally {
            removeDataPermission();
        }
    }

    /**
     * Push a "disable data permission" entry onto the stack.
     */
    public static void addDisableDataPermission(){
        DataPermission dataPermission = getDisableDataPermissionDisable();
        DataPermissionContextHolder.add(dataPermission);
    }

    public static void removeDataPermission(){
        DataPermissionContextHolder.remove();
    }

}
