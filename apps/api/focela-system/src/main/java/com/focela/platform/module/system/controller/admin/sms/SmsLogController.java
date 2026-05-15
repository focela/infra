package com.focela.platform.module.system.controller.admin.sms;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.module.system.controller.admin.sms.dto.log.SmsLogPageRequest;
import com.focela.platform.module.system.controller.admin.sms.dto.log.SmsLogResponse;
import com.focela.platform.module.system.entity.sms.SmsLogEntity;
import com.focela.platform.module.system.service.sms.SmsLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static com.focela.platform.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "Admin - SMS log")
@RestController
@RequestMapping("/system/sms-log")
@Validated
public class SmsLogController {

    @Resource
    private SmsLogService smsLogService;

    @GetMapping("/page")
    @Operation(summary = "get SMS log page")
    @PreAuthorize("@ss.hasPermission('system:sms-log:query')")
    public CommonResult<PageResult<SmsLogResponse>> getSmsLogPage(@Valid SmsLogPageRequest pageRequest) {
        PageResult<SmsLogEntity> pageResult = smsLogService.getSmsLogPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, SmsLogResponse.class));
    }

    @GetMapping("/get")
    @Operation(summary = "get SMS log")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:sms-log:query')")
    public CommonResult<SmsLogResponse> getSmsLog(@RequestParam("id") Long id) {
        SmsLogEntity smsLog = smsLogService.getSmsLog(id);
        return success(BeanUtils.toBean(smsLog, SmsLogResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "export SMS log Excel")
    @PreAuthorize("@ss.hasPermission('system:sms-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSmsLogExcel(@Valid SmsLogPageRequest exportRequest,
                                  HttpServletResponse response) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SmsLogEntity> list = smsLogService.getSmsLogPage(exportRequest).getList();
        // 导出 Excel
        ExcelUtils.write(response, "短信日志.xls", "数据", SmsLogResponse.class,
                BeanUtils.toBean(list, SmsLogResponse.class));
    }

}
