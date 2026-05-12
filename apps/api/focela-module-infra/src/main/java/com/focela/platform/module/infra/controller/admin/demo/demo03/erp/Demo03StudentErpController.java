package com.focela.platform.module.infra.controller.admin.demo.demo03.erp;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.pojo.CommonResult;
import com.focela.platform.framework.common.pojo.PageParam;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.framework.excel.core.util.ExcelUtils;
import com.focela.platform.module.infra.controller.admin.demo.demo03.erp.dto.Demo03StudentErpPageRequest;
import com.focela.platform.module.infra.controller.admin.demo.demo03.erp.dto.Demo03StudentErpResponse;
import com.focela.platform.module.infra.controller.admin.demo.demo03.erp.dto.Demo03StudentErpSaveRequest;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03CourseEntity;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03GradeEntity;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03StudentEntity;
import com.focela.platform.module.infra.service.demo.demo03.erp.Demo03StudentErpService;
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
import java.util.List;

import static com.focela.platform.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 学生")
@RestController
@RequestMapping("/infra/demo03-student-erp")
@Validated
public class Demo03StudentErpController {

    @Resource
    private Demo03StudentErpService demo03StudentErpService;

    @PostMapping("/create")
    @Operation(summary = "创建学生")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:create')")
    public CommonResult<Long> createDemo03Student(@Valid @RequestBody Demo03StudentErpSaveRequest createRequest) {
        return success(demo03StudentErpService.createDemo03Student(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "更新学生")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:update')")
    public CommonResult<Boolean> updateDemo03Student(@Valid @RequestBody Demo03StudentErpSaveRequest updateRequest) {
        demo03StudentErpService.updateDemo03Student(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学生")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:delete')")
    public CommonResult<Boolean> deleteDemo03Student(@RequestParam("id") Long id) {
        demo03StudentErpService.deleteDemo03Student(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除学生")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:delete')")
    public CommonResult<Boolean> deleteDemo03StudentList(@RequestParam("ids") List<Long> ids) {
        demo03StudentErpService.deleteDemo03StudentList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得学生")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:query')")
    public CommonResult<Demo03StudentErpResponse> getDemo03Student(@RequestParam("id") Long id) {
        Demo03StudentEntity demo03Student = demo03StudentErpService.getDemo03Student(id);
        return success(BeanUtils.toBean(demo03Student, Demo03StudentErpResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得学生分页")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:query')")
    public CommonResult<PageResult<Demo03StudentErpResponse>> getDemo03StudentPage(@Valid Demo03StudentErpPageRequest pageRequest) {
        PageResult<Demo03StudentEntity> pageResult = demo03StudentErpService.getDemo03StudentPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, Demo03StudentErpResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出学生 Excel")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDemo03StudentExcel(@Valid Demo03StudentErpPageRequest pageRequest,
                                         HttpServletResponse response) throws IOException {
        pageRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<Demo03StudentEntity> list = demo03StudentErpService.getDemo03StudentPage(pageRequest).getList();
        // 导出 Excel
        ExcelUtils.write(response, "学生.xls", "数据", Demo03StudentErpResponse.class,
                BeanUtils.toBean(list, Demo03StudentErpResponse.class));
    }

    // ==================== 子表（学生课程） ====================

    @GetMapping("/demo03-course/page")
    @Operation(summary = "获得学生课程分页")
    @Parameter(name = "studentId", description = "学生编号")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:query')")
    public CommonResult<PageResult<Demo03CourseEntity>> getDemo03CoursePage(PageParam pageRequest,
                                                                        @RequestParam("studentId") Long studentId) {
        return success(demo03StudentErpService.getDemo03CoursePage(pageRequest, studentId));
    }

    @PostMapping("/demo03-course/create")
    @Operation(summary = "创建学生课程")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:create')")
    public CommonResult<Long> createDemo03Course(@Valid @RequestBody Demo03CourseEntity demo03Course) {
        return success(demo03StudentErpService.createDemo03Course(demo03Course));
    }

    @PutMapping("/demo03-course/update")
    @Operation(summary = "更新学生课程")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:update')")
    public CommonResult<Boolean> updateDemo03Course(@Valid @RequestBody Demo03CourseEntity demo03Course) {
        demo03StudentErpService.updateDemo03Course(demo03Course);
        return success(true);
    }

    @DeleteMapping("/demo03-course/delete")
    @Parameter(name = "id", description = "编号", required = true)
    @Operation(summary = "删除学生课程")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:delete')")
    public CommonResult<Boolean> deleteDemo03Course(@RequestParam("id") Long id) {
        demo03StudentErpService.deleteDemo03Course(id);
        return success(true);
    }

    @DeleteMapping("/demo03-course/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除学生课程")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:delete')")
    public CommonResult<Boolean> deleteDemo03CourseList(@RequestParam("ids") List<Long> ids) {
        demo03StudentErpService.deleteDemo03CourseList(ids);
        return success(true);
    }

    @GetMapping("/demo03-course/get")
    @Operation(summary = "获得学生课程")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:query')")
    public CommonResult<Demo03CourseEntity> getDemo03Course(@RequestParam("id") Long id) {
        return success(demo03StudentErpService.getDemo03Course(id));
    }

    // ==================== 子表（学生班级） ====================

    @GetMapping("/demo03-grade/page")
    @Operation(summary = "获得学生班级分页")
    @Parameter(name = "studentId", description = "学生编号")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:query')")
    public CommonResult<PageResult<Demo03GradeEntity>> getDemo03GradePage(PageParam pageRequest,
                                                                      @RequestParam("studentId") Long studentId) {
        return success(demo03StudentErpService.getDemo03GradePage(pageRequest, studentId));
    }

    @PostMapping("/demo03-grade/create")
    @Operation(summary = "创建学生班级")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:create')")
    public CommonResult<Long> createDemo03Grade(@Valid @RequestBody Demo03GradeEntity demo03Grade) {
        return success(demo03StudentErpService.createDemo03Grade(demo03Grade));
    }

    @PutMapping("/demo03-grade/update")
    @Operation(summary = "更新学生班级")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:update')")
    public CommonResult<Boolean> updateDemo03Grade(@Valid @RequestBody Demo03GradeEntity demo03Grade) {
        demo03StudentErpService.updateDemo03Grade(demo03Grade);
        return success(true);
    }

    @DeleteMapping("/demo03-grade/delete")
    @Parameter(name = "id", description = "编号", required = true)
    @Operation(summary = "删除学生班级")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:delete')")
    public CommonResult<Boolean> deleteDemo03Grade(@RequestParam("id") Long id) {
        demo03StudentErpService.deleteDemo03Grade(id);
        return success(true);
    }

    @DeleteMapping("/demo03-grade/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除学生班级")
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:delete')")
    public CommonResult<Boolean> deleteDemo03GradeList(@RequestParam("ids") List<Long> ids) {
        demo03StudentErpService.deleteDemo03GradeList(ids);
        return success(true);
    }

    @GetMapping("/demo03-grade/get")
    @Operation(summary = "获得学生班级")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:demo03-student:query')")
    public CommonResult<Demo03GradeEntity> getDemo03Grade(@RequestParam("id") Long id) {
        return success(demo03StudentErpService.getDemo03Grade(id));
    }

}