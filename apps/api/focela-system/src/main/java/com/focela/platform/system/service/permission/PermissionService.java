package com.focela.platform.system.service.permission;

import com.focela.platform.common.api.system.permission.dto.DepartmentDataPermissionRpcResponse;

import java.util.Collection;
import java.util.Set;

import static java.util.Collections.singleton;

/**
 * Permission Service interface
 * <p>
 * Provides user-role, role-menu, and role-department associated permission handling
 */
public interface PermissionService {

    /**
     * Check whether the user has any of the given permissions
     *
     * @param userId      user ID
     * @param permissions permissions
     * @return whether granted
     */
    boolean hasAnyPermissions(Long userId, String... permissions);

    /**
     * Check whether the user has any of the given roles
     *
     * @param roles role array
     * @return whether granted
     */
    boolean hasAnyRoles(Long userId, String... roles);

    // ========== Role-menu related methods ==========

    /**
     * Assign menus to a role
     *
     * @param roleId  role ID
     * @param menuIds menu ID set
     */
    void assignRoleMenu(Long roleId, Set<Long> menuIds);

    /**
     * Handle removal of related authorization data when a role is deleted
     *
     * @param roleId role ID
     */
    void processRoleDeleted(Long roleId);

    /**
     * Handle removal of related authorization data when a menu is deleted
     *
     * @param menuId menu ID
     */
    void processMenuDeleted(Long menuId);

    /**
     * Get the menu ID set owned by a role
     *
     * @param roleId role ID
     * @return menu ID set
     */
    default Set<Long> getRoleMenuListByRoleId(Long roleId) {
        return getRoleMenuListByRoleId(singleton(roleId));
    }

    /**
     * Get the menu ID set owned by the given roles
     *
     * @param roleIds role ID array
     * @return menu ID set
     */
    Set<Long> getRoleMenuListByRoleId(Collection<Long> roleIds);

    /**
     * Get the role ID array that owns the specified menu, from cache
     *
     * @param menuId menu ID
     * @return role ID array
     */
    Set<Long> getMenuRoleIdListByMenuIdFromCache(Long menuId);

    // ========== User-role related methods ==========

    /**
     * Assign roles to a user
     *
     * @param userId  user ID
     * @param roleIds role ID set
     */
    void assignUserRole(Long userId, Set<Long> roleIds);

    /**
     * Handle removal of related authorization data when a user is deleted
     *
     * @param userId user ID
     */
    void processUserDeleted(Long userId);

    /**
     * Get the user ID set that owns the given roles
     *
     * @param roleIds role ID set
     * @return user ID set
     */
    Set<Long> getUserRoleIdListByRoleId(Collection<Long> roleIds);

    /**
     * Get the role ID set owned by a user
     *
     * @param userId user ID
     * @return role ID set
     */
    Set<Long> getUserRoleIdListByUserId(Long userId);

    /**
     * Get the role ID set owned by a user, from cache
     *
     * @param userId user ID
     * @return role ID set
     */
    Set<Long> getUserRoleIdListByUserIdFromCache(Long userId);

    // ========== User-department related methods ==========

    /**
     * Set the data permission for a role
     *
     * @param roleId           role ID
     * @param dataScope        data scope
     * @param dataScopeDeptIds department ID array
     */
    void assignRoleDataScope(Long roleId, Integer dataScope, Set<Long> dataScopeDeptIds);

    /**
     * Get the department data permission for the logged-in user
     *
     * @param userId user ID
     * @return department data permission
     */
    DepartmentDataPermissionRpcResponse getDeptDataPermission(Long userId);

}
