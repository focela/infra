package com.focela.platform.framework.datapermission.core.aop;

import com.focela.platform.framework.datapermission.core.annotation.DataPermission;
import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.LinkedList;
import java.util.List;

/**
 * Context holder for the {@link DataPermission} annotation.
 */
public class DataPermissionContextHolder {

    /**
     * A List is used because nested method calls are possible.
     */
    private static final ThreadLocal<LinkedList<DataPermission>> DATA_PERMISSIONS =
            TransmittableThreadLocal.withInitial(LinkedList::new);

    /**
     * Get the current DataPermission annotation.
     *
     * @return DataPermission annotation
     */
    public static DataPermission get() {
        return DATA_PERMISSIONS.get().peekLast();
    }

    /**
     * Push a DataPermission annotation onto the stack.
     *
     * @param dataPermission DataPermission annotation
     */
    public static void add(DataPermission dataPermission) {
        DATA_PERMISSIONS.get().addLast(dataPermission);
    }

    /**
     * Pop a DataPermission annotation off the stack.
     *
     * @return DataPermission annotation
     */
    public static DataPermission remove() {
        DataPermission dataPermission = DATA_PERMISSIONS.get().removeLast();
        // Clear the ThreadLocal when empty
        if (DATA_PERMISSIONS.get().isEmpty()) {
            DATA_PERMISSIONS.remove();
        }
        return dataPermission;
    }

    /**
     * Get all DataPermission annotations.
     *
     * @return DataPermission queue
     */
    public static List<DataPermission> getAll() {
        return DATA_PERMISSIONS.get();
    }

    /**
     * Clear the context.
     *
     * Currently only used for unit tests.
     */
    public static void clear() {
        DATA_PERMISSIONS.remove();
    }

}
