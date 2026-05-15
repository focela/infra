package com.focela.platform.system.api.permission;

import com.focela.platform.framework.common.contract.system.permission.dto.DepartmentDataPermissionRpcResponse;
import com.focela.platform.system.service.permission.PermissionService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Set;

/**
 * Permission API implementation class
 */
@Service
public class LocalPermissionApi implements PermissionApi {

    @Resource
    private PermissionService permissionService;

    @Override
    public Set<Long> getUserRoleIdListByRoleIds(Collection<Long> roleIds) {
        return permissionService.getUserRoleIdListByRoleId(roleIds);
    }

    @Override
    public boolean hasAnyPermissions(Long userId, String... permissions) {
        return permissionService.hasAnyPermissions(userId, permissions);
    }

    @Override
    public boolean hasAnyRoles(Long userId, String... roles) {
        return permissionService.hasAnyRoles(userId, roles);
    }

    @Override
    public DepartmentDataPermissionRpcResponse getDeptDataPermission(Long userId) {
        return permissionService.getDeptDataPermission(userId);
    }

}
