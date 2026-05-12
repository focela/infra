package com.focela.platform.module.infra.controller.admin.demo.demo01;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.module.infra.controller.admin.demo.demo01.dto.Demo01ContactPageRequest;
import com.focela.platform.module.infra.controller.admin.demo.demo01.dto.Demo01ContactResponse;
import com.focela.platform.module.infra.controller.admin.demo.demo01.dto.Demo01ContactSaveRequest;
import com.focela.platform.module.infra.repository.entity.demo.demo01.Demo01ContactEntity;
import com.focela.platform.module.infra.service.demo.demo01.Demo01ContactService;
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

@Tag(name = "管理后台 - 示例联系人")
@RestController
@RequestMapping("/infra/demo01-contact")
@Validated
public class Demo01ContactController {

    @Resource
    private Demo01ContactService demo01ContactService;

    @PostMapping("/create")
    @Operation(summary = "创建示例联系人")
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:create')")
    public CommonResult<Long> createDemo01Contact(@Valid @RequestBody Demo01ContactSaveRequest createRequest) {
        return success(demo01ContactService.createDemo01Contact(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "更新示例联系人")
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:update')")
    public CommonResult<Boolean> updateDemo01Contact(@Valid @RequestBody Demo01ContactSaveRequest updateRequest) {
        demo01ContactService.updateDemo01Contact(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除示例联系人")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:delete')")
    public CommonResult<Boolean> deleteDemo01Contact(@RequestParam("id") Long id) {
        demo01ContactService.deleteDemo01Contact(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除示例联系人")
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:delete')")
    public CommonResult<Boolean> deleteDemo0iContactList(@RequestParam("ids") List<Long> ids) {
        demo01ContactService.deleteDemo0iContactList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得示例联系人")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:query')")
    public CommonResult<Demo01ContactResponse> getDemo01Contact(@RequestParam("id") Long id) {
        Demo01ContactEntity demo01Contact = demo01ContactService.getDemo01Contact(id);
        return success(BeanUtils.toBean(demo01Contact, Demo01ContactResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得示例联系人分页")
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:query')")
    public CommonResult<PageResult<Demo01ContactResponse>> getDemo01ContactPage(@Valid Demo01ContactPageRequest pageRequest) {
        PageResult<Demo01ContactEntity> pageResult = demo01ContactService.getDemo01ContactPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, Demo01ContactResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出示例联系人 Excel")
    @PreAuthorize("@ss.hasPermission('infra:demo01-contact:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDemo01ContactExcel(@Valid Demo01ContactPageRequest pageRequest,
                                         HttpServletResponse response) throws IOException {
        pageRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<Demo01ContactEntity> list = demo01ContactService.getDemo01ContactPage(pageRequest).getList();
        // 导出 Excel
        ExcelUtils.write(response, "示例联系人.xls", "数据", Demo01ContactResponse.class,
                BeanUtils.toBean(list, Demo01ContactResponse.class));
    }

}