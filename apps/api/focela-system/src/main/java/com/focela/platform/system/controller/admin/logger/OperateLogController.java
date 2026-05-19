package com.focela.platform.system.controller.admin.logger;

import com.focela.platform.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageParam;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.excel.core.utils.ExcelUtils;
import com.focela.platform.translate.core.TranslateUtils;
import com.focela.platform.system.controller.admin.logger.dto.operatelog.OperateLogPageRequest;
import com.focela.platform.system.controller.admin.logger.dto.operatelog.OperateLogResponse;
import com.focela.platform.system.domain.entity.logger.OperateLogEntity;
import com.focela.platform.system.service.logger.OperateLogService;
import com.fhs.core.trans.anno.TransMethodResult;
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

@Tag(name = "Admin - Operation log")
@RestController
@RequestMapping("/system/operate-log")
@Validated
@RequiredArgsConstructor
public class OperateLogController {

    private final OperateLogService operateLogService;

    @GetMapping("/get")
    @Operation(summary = "view operation log")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:operate-log:query')")
    public CommonResult<OperateLogResponse> getOperateLog(@RequestParam("id") Long id) {
        OperateLogEntity operateLog = operateLogService.getOperateLog(id);
        return success(BeanUtils.toBean(operateLog, OperateLogResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "view operation log page list")
    @PreAuthorize("@ss.hasPermission('system:operate-log:query')")
    @TransMethodResult
    public CommonResult<PageResult<OperateLogResponse>> pageOperateLog(@Valid OperateLogPageRequest pageRequest) {
        PageResult<OperateLogEntity> pageResult = operateLogService.getOperateLogPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, OperateLogResponse.class));
    }

    @Operation(summary = "export operation log")
    @GetMapping("/export-excel")
    @PreAuthorize("@ss.hasPermission('system:operate-log:export')")
    @TransMethodResult
    @ApiAccessLog(operateType = EXPORT)
    public void exportOperateLog(HttpServletResponse response, @Valid OperateLogPageRequest exportRequest) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OperateLogEntity> operateLogs = operateLogService.getOperateLogPage(exportRequest).getList();
        ExcelUtils.write(response, "Operate Log.xls", "Data List", OperateLogResponse.class,
                TranslateUtils.translate(BeanUtils.toBean(operateLogs, OperateLogResponse.class)));
    }

}
