package com.focela.platform.module.infra.controller.admin.logger;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.module.infra.controller.admin.logger.dto.apiaccesslog.ApiAccessLogPageRequest;
import com.focela.platform.module.infra.controller.admin.logger.dto.apiaccesslog.ApiAccessLogResponse;
import com.focela.platform.module.infra.repository.entity.logger.ApiAccessLogEntity;
import com.focela.platform.module.infra.service.logger.ApiAccessLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static com.focela.platform.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "Admin - API access log")
@RestController
@RequestMapping("/infra/api-access-log")
@Validated
public class ApiAccessLogController {

    @Resource
    private ApiAccessLogService apiAccessLogService;

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
        List<ApiAccessLogEntity> list = apiAccessLogService.getApiAccessLogPage(exportRequest).getList();
        // 导出 Excel
        ExcelUtils.write(response, "API 访问日志.xls", "数据", ApiAccessLogResponse.class,
                BeanUtils.toBean(list, ApiAccessLogResponse.class));
    }

}
