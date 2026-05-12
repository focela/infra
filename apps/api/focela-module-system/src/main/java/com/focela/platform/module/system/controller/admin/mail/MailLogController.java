package com.focela.platform.module.system.controller.admin.mail;

import com.focela.platform.framework.common.pojo.CommonResult;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.mail.dto.log.MailLogPageRequest;
import com.focela.platform.module.system.controller.admin.mail.dto.log.MailLogResponse;
import com.focela.platform.module.system.repository.entity.mail.MailLogEntity;
import com.focela.platform.module.system.service.mail.MailLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static com.focela.platform.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 邮件日志")
@RestController
@RequestMapping("/system/mail-log")
public class MailLogController {

    @Resource
    private MailLogService mailLogService;

    @GetMapping("/page")
    @Operation(summary = "获得邮箱日志分页")
    @PreAuthorize("@ss.hasPermission('system:mail-log:query')")
    public CommonResult<PageResult<MailLogResponse>> getMailLogPage(@Valid MailLogPageRequest pageVO) {
        PageResult<MailLogEntity> pageResult = mailLogService.getMailLogPage(pageVO);
        return success(BeanUtils.toBean(pageResult, MailLogResponse.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得邮箱日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:mail-log:query')")
    public CommonResult<MailLogResponse> getMailTemplate(@RequestParam("id") Long id) {
        MailLogEntity log = mailLogService.getMailLog(id);
        return success(BeanUtils.toBean(log, MailLogResponse.class));
    }

}
