package com.focela.platform.module.system.controller.admin.permission;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.module.system.controller.admin.permission.dto.role.RolePageRequest;
import com.focela.platform.module.system.controller.admin.permission.dto.role.RoleResponse;
import com.focela.platform.module.system.controller.admin.permission.dto.role.RoleSaveRequest;
import com.focela.platform.module.system.repository.entity.permission.RoleEntity;
import com.focela.platform.module.system.service.permission.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static com.focela.platform.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.framework.common.model.CommonResult.success;
import static java.util.Collections.singleton;

@Tag(name = "Admin - Role")
@RestController
@RequestMapping("/system/role")
@Validated
public class RoleController {

    @Resource
    private RoleService roleService;

    @PostMapping("/create")
    @Operation(summary = "create role")
    @PreAuthorize("@ss.hasPermission('system:role:create')")
    public CommonResult<Long> createRole(@Valid @RequestBody RoleSaveRequest createRequest) {
        return success(roleService.createRole(createRequest, null));
    }

    @PutMapping("/update")
    @Operation(summary = "update role")
    @PreAuthorize("@ss.hasPermission('system:role:update')")
    public CommonResult<Boolean> updateRole(@Valid @RequestBody RoleSaveRequest updateRequest) {
        roleService.updateRole(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete role")
    @Parameter(name = "id", description = "Role ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:role:delete')")
    public CommonResult<Boolean> deleteRole(@RequestParam("id") Long id) {
        roleService.deleteRole(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete role")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('system:role:delete')")
    public CommonResult<Boolean> deleteRoleList(@RequestParam("ids") List<Long> ids) {
        roleService.deleteRoleList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get role info")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<RoleResponse> getRole(@RequestParam("id") Long id) {
        RoleEntity role = roleService.getRole(id);
        return success(BeanUtils.toBean(role, RoleResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get role page")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<PageResult<RoleResponse>> getRolePage(RolePageRequest pageRequest) {
        PageResult<RoleEntity> pageResult = roleService.getRolePage(pageRequest);
        return success(BeanUtils.toBean(pageResult, RoleResponse.class));
    }

    @GetMapping({"/list-all-simple", "/simple-list"})
    @Operation(summary = "get role simplified info list", description = "only include enabled role, for frontend dropdown options")
    public CommonResult<List<RoleResponse>> getSimpleRoleList() {
        List<RoleEntity> list = roleService.getRoleListByStatus(singleton(CommonStatusEnum.ENABLE.getStatus()));
        list.sort(Comparator.comparing(RoleEntity::getSort));
        return success(BeanUtils.toBean(list, RoleResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "export role Excel")
    @ApiAccessLog(operateType = EXPORT)
    @PreAuthorize("@ss.hasPermission('system:role:export')")
    public void export(HttpServletResponse response, @Validated RolePageRequest exportRequest) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<RoleEntity> list = roleService.getRolePage(exportRequest).getList();
        // 输出
        ExcelUtils.write(response, "角色数据.xls", "数据", RoleResponse.class,
                BeanUtils.toBean(list, RoleResponse.class));
    }

}
