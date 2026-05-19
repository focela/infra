package com.focela.platform.system.controller.admin.logger;

import com.focela.platform.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageParam;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.excel.core.utils.ExcelUtils;
import com.focela.platform.system.controller.admin.logger.dto.loginlog.LoginLogPageRequest;
import com.focela.platform.system.controller.admin.logger.dto.loginlog.LoginLogResponse;
import com.focela.platform.system.domain.entity.logger.LoginLogEntity;
import com.focela.platform.system.service.logger.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static com.focela.platform.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - Login log")
@RestController
@RequestMapping("/system/login-log")
@Validated
@RequiredArgsConstructor
public class LoginLogController {

    private final LoginLogService loginLogService;

    @GetMapping("/get")
    @Operation(summary = "get login log")
    @PreAuthorize("@ss.hasPermission('system:login-log:query')")
    public CommonResult<LoginLogResponse> getLoginLog(Long id) {
        LoginLogEntity loginLog = loginLogService.getLoginLog(id);
        return success(BeanUtils.toBean(loginLog, LoginLogResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get login log page list")
    @PreAuthorize("@ss.hasPermission('system:login-log:query')")
    public CommonResult<PageResult<LoginLogResponse>> getLoginLogPage(@Valid LoginLogPageRequest pageRequest) {
        PageResult<LoginLogEntity> pageResult = loginLogService.getLoginLogPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, LoginLogResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "export login log Excel")
    @PreAuthorize("@ss.hasPermission('system:login-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportLoginLog(HttpServletResponse response, @Valid LoginLogPageRequest exportRequest) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<LoginLogEntity> loginLogs = loginLogService.getLoginLogPage(exportRequest).getList();
        // output
        ExcelUtils.write(response, "Login Log.xls", "Data List", LoginLogResponse.class,
                BeanUtils.toBean(loginLogs, LoginLogResponse.class));
    }

}
