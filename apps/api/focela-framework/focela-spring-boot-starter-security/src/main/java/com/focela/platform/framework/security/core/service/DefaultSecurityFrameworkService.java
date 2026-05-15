package com.focela.platform.framework.security.core.service;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.common.api.system.permission.PermissionContractApi;
import com.focela.platform.framework.security.core.LoginUser;
import com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils;
import lombok.AllArgsConstructor;

import java.util.Arrays;

import static com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils.getLoginUserId;
import static com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils.skipPermissionCheck;

/**
 * Default {@link SecurityFrameworkService} implementation class
 */
@AllArgsConstructor
public class DefaultSecurityFrameworkService implements SecurityFrameworkService {

    private final PermissionContractApi permissionApi;

    @Override
    public boolean hasPermission(String permission) {
        return hasAnyPermissions(permission);
    }

    @Override
    public boolean hasAnyPermissions(String... permissions) {
        // Special: cross-tenant access
        if (skipPermissionCheck()) {
            return true;
        }

        // Permission check
        Long userId = getLoginUserId();
        if (userId == null) {
            return false;
        }
        return permissionApi.hasAnyPermissions(userId, permissions);
    }

    @Override
    public boolean hasRole(String role) {
        return hasAnyRoles(role);
    }

    @Override
    public boolean hasAnyRoles(String... roles) {
        // Special: cross-tenant access
        if (skipPermissionCheck()) {
            return true;
        }

        // Permission check
        Long userId = getLoginUserId();
        if (userId == null) {
            return false;
        }
        return permissionApi.hasAnyRoles(userId, roles);
    }

    @Override
    public boolean hasScope(String scope) {
        return hasAnyScopes(scope);
    }

    @Override
    public boolean hasAnyScopes(String... scope) {
        // Special: cross-tenant access
        if (skipPermissionCheck()) {
            return true;
        }

        // Permission check
        LoginUser user = SecurityFrameworkUtils.getLoginUser();
        if (user == null) {
            return false;
        }
        return CollUtil.containsAny(user.getScopes(), Arrays.asList(scope));
    }

}
