package com.focela.platform.common.api.system.permission;

import com.focela.platform.common.api.system.permission.dto.DepartmentDataPermissionRpcResponse;

/**
 * Permission API interface.
 */
public interface PermissionContractApi {

    /**
     * Check whether the user has any of the given permissions (any one is sufficient).
     *
     * @param userId user ID
     * @param permissions permissions
     * @return whether the user has any of the permissions
     */
    boolean hasAnyPermissions(Long userId, String... permissions);

    /**
     * Check whether the user has any of the given roles (any one is sufficient).
     *
     * @param userId user ID
     * @param roles role array
     * @return whether the user has any of the roles
     */
    boolean hasAnyRoles(Long userId, String... roles);

    /**
     * Get the department data permission for the logged-in user.
     *
     * @param userId user ID
     * @return department data permission
     */
    @SuppressWarnings("deprecation")
    default DepartmentDataPermissionRpcResponse getDepartmentDataPermission(Long userId) {
        return getDeptDataPermission(userId);
    }

    /**
     * Get the department data permission for the logged-in user.
     *
     * @param userId user ID
     * @return department data permission
     * @deprecated use {@link #getDepartmentDataPermission(Long)}
     */
    @Deprecated
    DepartmentDataPermissionRpcResponse getDeptDataPermission(Long userId);

}
