package com.focela.platform.module.infra.controller.admin.demo.demo02;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.module.infra.controller.admin.demo.demo02.dto.Demo02CategoryListRequest;
import com.focela.platform.module.infra.controller.admin.demo.demo02.dto.Demo02CategoryResponse;
import com.focela.platform.module.infra.controller.admin.demo.demo02.dto.Demo02CategorySaveRequest;
import com.focela.platform.module.infra.repository.entity.demo.demo02.Demo02CategoryEntity;
import com.focela.platform.module.infra.service.demo.demo02.Demo02CategoryService;
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
import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "管理后台 - 示例分类")
@RestController
@RequestMapping("/infra/demo02-category")
@Validated
public class Demo02CategoryController {

    @Resource
    private Demo02CategoryService demo02CategoryService;

    @PostMapping("/create")
    @Operation(summary = "创建示例分类")
    @PreAuthorize("@ss.hasPermission('infra:demo02-category:create')")
    public CommonResult<Long> createDemo02Category(@Valid @RequestBody Demo02CategorySaveRequest createRequest) {
        return success(demo02CategoryService.createDemo02Category(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "更新示例分类")
    @PreAuthorize("@ss.hasPermission('infra:demo02-category:update')")
    public CommonResult<Boolean> updateDemo02Category(@Valid @RequestBody Demo02CategorySaveRequest updateRequest) {
        demo02CategoryService.updateDemo02Category(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除示例分类")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:demo02-category:delete')")
    public CommonResult<Boolean> deleteDemo02Category(@RequestParam("id") Long id) {
        demo02CategoryService.deleteDemo02Category(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得示例分类")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:demo02-category:query')")
    public CommonResult<Demo02CategoryResponse> getDemo02Category(@RequestParam("id") Long id) {
        Demo02CategoryEntity demo02Category = demo02CategoryService.getDemo02Category(id);
        return success(BeanUtils.toBean(demo02Category, Demo02CategoryResponse.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得示例分类列表")
    @PreAuthorize("@ss.hasPermission('infra:demo02-category:query')")
    public CommonResult<List<Demo02CategoryResponse>> getDemo02CategoryList(@Valid Demo02CategoryListRequest listRequest) {
        List<Demo02CategoryEntity> list = demo02CategoryService.getDemo02CategoryList(listRequest);
        return success(BeanUtils.toBean(list, Demo02CategoryResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出示例分类 Excel")
    @PreAuthorize("@ss.hasPermission('infra:demo02-category:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDemo02CategoryExcel(@Valid Demo02CategoryListRequest listRequest,
                                          HttpServletResponse response) throws IOException {
        List<Demo02CategoryEntity> list = demo02CategoryService.getDemo02CategoryList(listRequest);
        // 导出 Excel
        ExcelUtils.write(response, "示例分类.xls", "数据", Demo02CategoryResponse.class,
                BeanUtils.toBean(list, Demo02CategoryResponse.class));
    }

}