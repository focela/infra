package com.focela.platform.system.controller.admin.mail;

import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.mail.dto.log.MailLogPageRequest;
import com.focela.platform.system.controller.admin.mail.dto.log.MailLogResponse;
import com.focela.platform.system.domain.entity.mail.MailLogEntity;
import com.focela.platform.system.service.mail.MailLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - email log")
@RestController
@RequestMapping("/system/mail-log")
@RequiredArgsConstructor
public class MailLogController {

        private final MailLogService mailLogService;

    @GetMapping("/page")
    @Operation(summary = "get email log page")
    @PreAuthorize("@ss.hasPermission('system:mail-log:query')")
    public CommonResult<PageResult<MailLogResponse>> getMailLogPage(@Valid MailLogPageRequest pageRequest) {
        PageResult<MailLogEntity> pageResult = mailLogService.getMailLogPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, MailLogResponse.class));
    }

    @GetMapping("/get")
    @Operation(summary = "get email log")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:mail-log:query')")
    public CommonResult<MailLogResponse> getMailTemplate(@RequestParam("id") Long id) {
        MailLogEntity log = mailLogService.getMailLog(id);
        return success(BeanUtils.toBean(log, MailLogResponse.class));
    }

}
