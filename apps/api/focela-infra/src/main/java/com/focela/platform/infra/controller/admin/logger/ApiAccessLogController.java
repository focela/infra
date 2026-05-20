package com.focela.platform.infra.controller.admin.logger;

import com.focela.platform.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageParam;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.excel.core.utils.ExcelUtils;
import com.focela.platform.infra.controller.admin.logger.request.accesslog.ApiAccessLogPageRequest;
import com.focela.platform.infra.controller.admin.logger.response.accesslog.ApiAccessLogResponse;
import com.focela.platform.infra.domain.entity.logger.ApiAccessLogEntity;
import com.focela.platform.infra.service.logger.ApiAccessLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static com.focela.platform.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - API access log")
@RestController
@RequestMapping("/infra/api-access-log")
@Validated
@RequiredArgsConstructor
public class ApiAccessLogController {

    private final ApiAccessLogService apiAccessLogService;

    @GetMapping("/get")
    @Operation(summary = "get API access log")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:api-access-log:query')")
    public CommonResult<ApiAccessLogResponse> getApiAccessLog(@RequestParam("id") Long id) {
        ApiAccessLogEntity apiAccessLog = apiAccessLogService.getApiAccessLog(id);
        return success(BeanUtils.toBean(apiAccessLog, ApiAccessLogResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get API access log page")
    @PreAuthorize("@ss.hasPermission('infra:api-access-log:query')")
    public CommonResult<PageResult<ApiAccessLogResponse>> getApiAccessLogPage(@Valid ApiAccessLogPageRequest pageRequest) {
        PageResult<ApiAccessLogEntity> pageResult = apiAccessLogService.getApiAccessLogPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, ApiAccessLogResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "export API access log Excel")
    @PreAuthorize("@ss.hasPermission('infra:api-access-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportApiAccessLogExcel(@Valid ApiAccessLogPageRequest exportRequest,
                                        HttpServletResponse response) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ApiAccessLogEntity> apiAccessLogs = apiAccessLogService.getApiAccessLogPage(exportRequest).getList();
        // Export Excel
        ExcelUtils.write(response, "API access log.xls", "Data", ApiAccessLogResponse.class,
                BeanUtils.toBean(apiAccessLogs, ApiAccessLogResponse.class));
    }

}
