package com.focela.platform.module.system.controller.admin.dept;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.pojo.CommonResult;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.dept.dto.dept.DeptListRequest;
import com.focela.platform.module.system.controller.admin.dept.dto.dept.DeptResponse;
import com.focela.platform.module.system.controller.admin.dept.dto.dept.DeptSaveRequest;
import com.focela.platform.module.system.controller.admin.dept.dto.dept.DeptSimpleResponse;
import com.focela.platform.module.system.repository.entity.dept.DeptEntity;
import com.focela.platform.module.system.service.dept.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static com.focela.platform.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 部门")
@RestController
@RequestMapping("/system/dept")
@Validated
public class DeptController {

    @Resource
    private DeptService deptService;

    @PostMapping("create")
    @Operation(summary = "创建部门")
    @PreAuthorize("@ss.hasPermission('system:dept:create')")
    public CommonResult<Long> createDept(@Valid @RequestBody DeptSaveRequest createRequest) {
        Long deptId = deptService.createDept(createRequest);
        return success(deptId);
    }

    @PutMapping("update")
    @Operation(summary = "更新部门")
    @PreAuthorize("@ss.hasPermission('system:dept:update')")
    public CommonResult<Boolean> updateDept(@Valid @RequestBody DeptSaveRequest updateRequest) {
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
    public CommonResult<List<DeptResponse>> getDeptList(DeptListRequest reqVO) {
        List<DeptEntity> list = deptService.getDeptList(reqVO);
        return success(BeanUtils.toBean(list, DeptResponse.class));
    }

    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取部门精简信息列表", description = "只包含被开启的部门，主要用于前端的下拉选项")
    public CommonResult<List<DeptSimpleResponse>> getSimpleDeptList() {
        List<DeptEntity> list = deptService.getDeptList(
                new DeptListRequest().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        return success(BeanUtils.toBean(list, DeptSimpleResponse.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得部门信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<DeptResponse> getDept(@RequestParam("id") Long id) {
        DeptEntity dept = deptService.getDept(id);
        return success(BeanUtils.toBean(dept, DeptResponse.class));
    }

}
