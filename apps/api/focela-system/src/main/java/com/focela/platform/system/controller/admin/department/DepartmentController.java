package com.focela.platform.system.controller.admin.department;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.department.request.dept.DepartmentListRequest;
import com.focela.platform.system.controller.admin.department.response.dept.DepartmentResponse;
import com.focela.platform.system.controller.admin.department.request.dept.DepartmentSaveRequest;
import com.focela.platform.system.controller.admin.department.response.dept.DepartmentSimpleResponse;
import com.focela.platform.system.domain.entity.department.DepartmentEntity;
import com.focela.platform.system.service.department.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - Department")
@RestController
@RequestMapping("/system/dept")
@Validated
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService deptService;

    @PostMapping("create")
    @Operation(summary = "create department")
    @PreAuthorize("@ss.hasPermission('system:dept:create')")
    public CommonResult<Long> createDept(@Valid @RequestBody DepartmentSaveRequest createRequest) {
        Long deptId = deptService.createDept(createRequest);
        return success(deptId);
    }

    @PutMapping("update")
    @Operation(summary = "update department")
    @PreAuthorize("@ss.hasPermission('system:dept:update')")
    public CommonResult<Boolean> updateDept(@Valid @RequestBody DepartmentSaveRequest updateRequest) {
        deptService.updateDept(updateRequest);
        return success(true);
    }

    @DeleteMapping("delete")
    @Operation(summary = "delete department")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public CommonResult<Boolean> deleteDept(@RequestParam("id") Long id) {
        deptService.deleteDept(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete department")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public CommonResult<Boolean> deleteDeptList(@RequestParam("ids") List<Long> ids) {
        deptService.deleteDeptList(ids);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "get department list")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<List<DepartmentResponse>> getDeptList(DepartmentListRequest request) {
        List<DepartmentEntity> departments = deptService.getDeptList(request);
        return success(BeanUtils.toBean(departments, DepartmentResponse.class));
    }

    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "get department simplified info list", description = "only include enabled department, for frontend dropdown options")
    public CommonResult<List<DepartmentSimpleResponse>> getSimpleDeptList() {
        List<DepartmentEntity> departments = deptService.getDeptList(
                new DepartmentListRequest().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        return success(BeanUtils.toBean(departments, DepartmentSimpleResponse.class));
    }

    @GetMapping("/get")
    @Operation(summary = "get department info")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<DepartmentResponse> getDept(@RequestParam("id") Long id) {
        DepartmentEntity dept = deptService.getDept(id);
        return success(BeanUtils.toBean(dept, DepartmentResponse.class));
    }

}
