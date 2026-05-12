package com.focela.platform.module.system.controller.admin.department;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.department.dto.dept.DepartmentListRequest;
import com.focela.platform.module.system.controller.admin.department.dto.dept.DepartmentResponse;
import com.focela.platform.module.system.controller.admin.department.dto.dept.DepartmentSaveRequest;
import com.focela.platform.module.system.controller.admin.department.dto.dept.DepartmentSimpleResponse;
import com.focela.platform.module.system.repository.entity.department.DepartmentEntity;
import com.focela.platform.module.system.service.department.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "管理后台 - 部门")
@RestController
@RequestMapping("/system/dept")
@Validated
public class DepartmentController {

    @Resource
    private DepartmentService deptService;

    @PostMapping("create")
    @Operation(summary = "创建部门")
    @PreAuthorize("@ss.hasPermission('system:dept:create')")
    public CommonResult<Long> createDept(@Valid @RequestBody DepartmentSaveRequest createRequest) {
        Long deptId = deptService.createDept(createRequest);
        return success(deptId);
    }

    @PutMapping("update")
    @Operation(summary = "更新部门")
    @PreAuthorize("@ss.hasPermission('system:dept:update')")
    public CommonResult<Boolean> updateDept(@Valid @RequestBody DepartmentSaveRequest updateRequest) {
        deptService.updateDept(updateRequest);
        return success(true);
    }

    @DeleteMapping("delete")
    @Operation(summary = "删除部门")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public CommonResult<Boolean> deleteDept(@RequestParam("id") Long id) {
        deptService.deleteDept(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除部门")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public CommonResult<Boolean> deleteDeptList(@RequestParam("ids") List<Long> ids) {
        deptService.deleteDeptList(ids);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取部门列表")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<List<DepartmentResponse>> getDeptList(DepartmentListRequest request) {
        List<DepartmentEntity> list = deptService.getDeptList(request);
        return success(BeanUtils.toBean(list, DepartmentResponse.class));
    }

    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取部门精简信息列表", description = "只包含被开启的部门，主要用于前端的下拉选项")
    public CommonResult<List<DepartmentSimpleResponse>> getSimpleDeptList() {
        List<DepartmentEntity> list = deptService.getDeptList(
                new DepartmentListRequest().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        return success(BeanUtils.toBean(list, DepartmentSimpleResponse.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得部门信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<DepartmentResponse> getDept(@RequestParam("id") Long id) {
        DepartmentEntity dept = deptService.getDept(id);
        return success(BeanUtils.toBean(dept, DepartmentResponse.class));
    }

}
