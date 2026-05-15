package com.focela.platform.module.infra.controller.admin.logger;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.module.infra.controller.admin.logger.dto.apierrorlog.ApiErrorLogPageRequest;
import com.focela.platform.module.infra.controller.admin.logger.dto.apierrorlog.ApiErrorLogResponse;
import com.focela.platform.module.infra.entity.logger.ApiErrorLogEntity;
import com.focela.platform.module.infra.service.logger.ApiErrorLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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
import static com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "Admin - API error log")
@RestController
@RequestMapping("/infra/api-error-log")
@Validated
public class ApiErrorLogController {

    @Resource
    private ApiErrorLogService apiErrorLogService;

    @PutMapping("/update-status")
    @Operation(summary = "update API error log status")
    @Parameters({
            @Parameter(name = "id", description = "ID", required = true, example = "1024"),
            @Parameter(name = "processStatus", description = "Process status", required = true, example = "1")
    })
    @PreAuthorize("@ss.hasPermission('infra:api-error-log:update-status')")
    public CommonResult<Boolean> updateApiErrorLogProcess(@RequestParam("id") Long id,
                                                          @RequestParam("processStatus") Integer processStatus) {
        apiErrorLogService.updateApiErrorLogProcess(id, processStatus, getLoginUserId());
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get API error log")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:api-error-log:query')")
    public CommonResult<ApiErrorLogResponse> getApiErrorLog(@RequestParam("id") Long id) {
        ApiErrorLogEntity apiErrorLog = apiErrorLogService.getApiErrorLog(id);
        return success(BeanUtils.toBean(apiErrorLog, ApiErrorLogResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get API error log page")
    @PreAuthorize("@ss.hasPermission('infra:api-error-log:query')")
    public CommonResult<PageResult<ApiErrorLogResponse>> getApiErrorLogPage(@Valid ApiErrorLogPageRequest pageRequest) {
        PageResult<ApiErrorLogEntity> pageResult = apiErrorLogService.getApiErrorLogPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, ApiErrorLogResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "export API error log Excel")
    @PreAuthorize("@ss.hasPermission('infra:api-error-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportApiErrorLogExcel(@Valid ApiErrorLogPageRequest exportRequest,
                                       HttpServletResponse response) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ApiErrorLogEntity> list = apiErrorLogService.getApiErrorLogPage(exportRequest).getList();
        // 导出 Excel
        ExcelUtils.write(response, "API 错误日志.xls", "数据", ApiErrorLogResponse.class,
                BeanUtils.toBean(list, ApiErrorLogResponse.class));
    }

}
