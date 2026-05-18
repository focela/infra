package com.focela.platform.system.controller.admin.permission;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.system.controller.admin.permission.dto.PermissionAssignRoleDataScopeRequest;
import com.focela.platform.system.controller.admin.permission.dto.PermissionAssignRoleMenuRequest;
import com.focela.platform.system.controller.admin.permission.dto.PermissionAssignUserRoleRequest;
import com.focela.platform.system.service.permission.PermissionService;
import com.focela.platform.system.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Set;

import static com.focela.platform.common.model.CommonResult.success;

/**
 * Permission Controller, provides API endpoints for assigning permissions to users and roles
 */
@Tag(name = "Admin - permission")
@RestController
@RequestMapping("/system/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;
    private final TenantService tenantService;

    @Operation(summary = "get role owned menu ID")
    @Parameter(name = "roleId", description = "Role ID", required = true)
    @GetMapping("/list-role-menus")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-menu')")
    public CommonResult<Set<Long>> getRoleMenuList(Long roleId) {
        return success(permissionService.getRoleMenuListByRoleId(roleId));
    }

    @PostMapping("/assign-role-menu")
    @Operation(summary = "grant role menus")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-menu')")
    public CommonResult<Boolean> assignRoleMenu(@Validated @RequestBody PermissionAssignRoleMenuRequest request) {
        // When multi-tenancy is enabled, filter out menus not opened for this tenant
        tenantService.handleTenantMenu(menuIds -> request.getMenuIds().removeIf(menuId -> !CollUtil.contains(menuIds, menuId)));

        // Perform menu assignment
        permissionService.assignRoleMenu(request.getRoleId(), request.getMenuIds());
        return success(true);
    }

    @PostMapping("/assign-role-data-scope")
    @Operation(summary = "grant role data permission")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-data-scope')")
    public CommonResult<Boolean> assignRoleDataScope(@Valid @RequestBody PermissionAssignRoleDataScopeRequest request) {
        permissionService.assignRoleDataScope(request.getRoleId(), request.getDataScope(), request.getDataScopeDeptIds());
        return success(true);
    }

    @Operation(summary = "get admin owned role ID list")
    @Parameter(name = "userId", description = "User ID", required = true)
    @GetMapping("/list-user-roles")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<Set<Long>> listAdminRoles(@RequestParam("userId") Long userId) {
        return success(permissionService.getUserRoleIdListByUserId(userId));
    }

    @Operation(summary = "grant user role")
    @PostMapping("/assign-user-role")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<Boolean> assignUserRole(@Validated @RequestBody PermissionAssignUserRoleRequest request) {
        permissionService.assignUserRole(request.getUserId(), request.getRoleIds());
        return success(true);
    }

}
