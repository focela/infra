package com.focela.platform.module.system.controller.admin.logger;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.module.system.controller.admin.logger.dto.loginlog.LoginLogPageRequest;
import com.focela.platform.module.system.controller.admin.logger.dto.loginlog.LoginLogResponse;
import com.focela.platform.module.system.entity.logger.LoginLogEntity;
import com.focela.platform.module.system.service.logger.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static com.focela.platform.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "Admin - Login log")
@RestController
@RequestMapping("/system/login-log")
@Validated
public class LoginLogController {

    @Resource
    private LoginLogService loginLogService;

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
        List<LoginLogEntity> list = loginLogService.getLoginLogPage(exportRequest).getList();
        // 输出
        ExcelUtils.write(response, "登录日志.xls", "数据列表", LoginLogResponse.class,
                BeanUtils.toBean(list, LoginLogResponse.class));
    }

}
