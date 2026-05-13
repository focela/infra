package com.focela.platform.module.system.controller.admin.permission;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.module.system.controller.admin.permission.dto.PermissionAssignRoleDataScopeRequest;
import com.focela.platform.module.system.controller.admin.permission.dto.PermissionAssignRoleMenuRequest;
import com.focela.platform.module.system.controller.admin.permission.dto.PermissionAssignUserRoleRequest;
import com.focela.platform.module.system.service.permission.PermissionService;
import com.focela.platform.module.system.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Set;

import static com.focela.platform.framework.common.model.CommonResult.success;

/**
 * 权限 Controller，提供赋予用户、角色的权限的 API 接口
 */
@Tag(name = "Admin - permission")
@RestController
@RequestMapping("/system/permission")
public class PermissionController {

    @Resource
    private PermissionService permissionService;
    @Resource
    private TenantService tenantService;

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
        // 开启多租户的情况下，需要过滤掉未开通的菜单
        tenantService.handleTenantMenu(menuIds -> request.getMenuIds().removeIf(menuId -> !CollUtil.contains(menuIds, menuId)));

        // 执行菜单的分配
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
