package com.focela.platform.system.controller.admin.department;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.department.request.department.DepartmentListRequest;
import com.focela.platform.system.controller.admin.department.response.department.DepartmentResponse;
import com.focela.platform.system.controller.admin.department.request.department.DepartmentSaveRequest;
import com.focela.platform.system.controller.admin.department.response.department.DepartmentSimpleResponse;
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
@RequestMapping({"/system/department", "/system/dept"})
@Validated
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/create")
    @Operation(summary = "create department")
    @PreAuthorize("@ss.hasPermission('system:dept:create')")
    public CommonResult<Long> createDepartment(@Valid @RequestBody DepartmentSaveRequest createRequest) {
        Long departmentId = departmentService.createDepartment(createRequest);
        return success(departmentId);
    }

    @PutMapping("/update")
    @Operation(summary = "update department")
    @PreAuthorize("@ss.hasPermission('system:dept:update')")
    public CommonResult<Boolean> updateDepartment(@Valid @RequestBody DepartmentSaveRequest updateRequest) {
        departmentService.updateDepartment(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete department")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public CommonResult<Boolean> deleteDepartment(@RequestParam("id") Long id) {
        departmentService.deleteDepartment(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete department")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public CommonResult<Boolean> deleteDepartmentList(@RequestParam("ids") List<Long> ids) {
        departmentService.deleteDepartmentList(ids);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "get department list")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<List<DepartmentResponse>> getDepartmentList(DepartmentListRequest request) {
        List<DepartmentEntity> departments = departmentService.getDepartmentList(request);
        return success(BeanUtils.toBean(departments, DepartmentResponse.class));
    }

    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "get department simplified info list", description = "only include enabled department, for frontend dropdown options")
    public CommonResult<List<DepartmentSimpleResponse>> getSimpleDepartmentList() {
        List<DepartmentEntity> departments = departmentService.getDepartmentList(
                new DepartmentListRequest().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        return success(BeanUtils.toBean(departments, DepartmentSimpleResponse.class));
    }

    @GetMapping("/get")
    @Operation(summary = "get department info")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<DepartmentResponse> getDepartment(@RequestParam("id") Long id) {
        DepartmentEntity department = departmentService.getDepartment(id);
        return success(BeanUtils.toBean(department, DepartmentResponse.class));
    }

}
