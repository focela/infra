package com.focela.platform.security.core.service;

/**
 * Security framework Service interface, defining permission-related validation operations.
 */
public interface SecurityFrameworkService {

    /**
     * Check whether the user has the given permission.
     *
     * @param permission permission
     * @return whether the user has it
     */
    boolean hasPermission(String permission);

    /**
     * Check whether the user has any one of the given permissions.
     *
     * @param permissions permissions
     * @return whether the user has any of them
     */
    boolean hasAnyPermissions(String... permissions);

    /**
     * Check whether the user has the given role.
     *
     * Note: roles are identified by the SysRoleDO code.
     *
     * @param role role
     * @return whether the user has it
     */
    boolean hasRole(String role);

    /**
     * Check whether the user has any one of the given roles.
     *
     * @param roles roles array
     * @return whether the user has any of them
     */
    boolean hasAnyRoles(String... roles);

    /**
     * Check whether the user has the given authorization scope.
     *
     * @param scope authorization scope
     * @return whether the user has it
     */
    boolean hasScope(String scope);

    /**
     * Check whether the user has any one of the given authorization scopes.
     *
     * @param scope authorization scopes array
     * @return whether the user has any of them
     */
    boolean hasAnyScopes(String... scope);
}
