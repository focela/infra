package com.focela.platform.system.controller.admin.mail;

import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.mail.request.template.MailTemplatePageRequest;
import com.focela.platform.system.controller.admin.mail.response.template.MailTemplateResponse;
import com.focela.platform.system.controller.admin.mail.request.template.MailTemplateSaveRequest;
import com.focela.platform.system.controller.admin.mail.request.template.MailTemplateSendRequest;
import com.focela.platform.system.controller.admin.mail.response.template.MailTemplateSimpleResponse;
import com.focela.platform.system.domain.entity.mail.MailTemplateEntity;
import com.focela.platform.system.service.mail.MailSendService;
import com.focela.platform.system.service.mail.MailTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;
import static com.focela.platform.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "Admin - Email template")
@RestController
@RequestMapping("/system/mail-template")
@RequiredArgsConstructor
public class MailTemplateController {

    private final MailTemplateService mailTemplateService;
    private final MailSendService mailSendService;

    @PostMapping("/create")
    @Operation(summary = "create email template")
    @PreAuthorize("@ss.hasPermission('system:mail-template:create')")
    public CommonResult<Long> createMailTemplate(@Valid @RequestBody MailTemplateSaveRequest createRequest){
        return success(mailTemplateService.createMailTemplate(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update email template")
    @PreAuthorize("@ss.hasPermission('system:mail-template:update')")
    public CommonResult<Boolean> updateMailTemplate(@Valid @RequestBody MailTemplateSaveRequest updateRequest){
        mailTemplateService.updateMailTemplate(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete email template")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:mail-template:delete')")
    public CommonResult<Boolean> deleteMailTemplate(@RequestParam("id") Long id) {
        mailTemplateService.deleteMailTemplate(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete email template")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('system:mail-template:delete')")
    public CommonResult<Boolean> deleteMailTemplateList(@RequestParam("ids") List<Long> ids) {
        mailTemplateService.deleteMailTemplateList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get email template")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:mail-template:query')")
    public CommonResult<MailTemplateResponse> getMailTemplate(@RequestParam("id") Long id) {
        MailTemplateEntity template = mailTemplateService.getMailTemplate(id);
        return success(BeanUtils.toBean(template, MailTemplateResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get email template page")
    @PreAuthorize("@ss.hasPermission('system:mail-template:query')")
    public CommonResult<PageResult<MailTemplateResponse>> getMailTemplatePage(@Valid MailTemplatePageRequest pageRequest) {
        PageResult<MailTemplateEntity> pageResult = mailTemplateService.getMailTemplatePage(pageRequest);
        return success(BeanUtils.toBean(pageResult, MailTemplateResponse.class));
    }

    @GetMapping({"/list-all-simple", "simple-list"})
    @Operation(summary = "get email template simplified list")
    public CommonResult<List<MailTemplateSimpleResponse>> getSimpleTemplateList() {
        List<MailTemplateEntity> mailTemplates = mailTemplateService.getMailTemplateList();
        return success(BeanUtils.toBean(mailTemplates, MailTemplateSimpleResponse.class));
    }

    @PostMapping("/send-mail")
    @Operation(summary = "send email")
    @PreAuthorize("@ss.hasPermission('system:mail-template:send-mail')")
    public CommonResult<Long> sendMail(@Valid @RequestBody MailTemplateSendRequest sendRequest) {
        return success(mailSendService.sendSingleMailToAdmin(getLoginUserId(),
                sendRequest.getToMails(), sendRequest.getCcMails(), sendRequest.getBccMails(),
                sendRequest.getTemplateCode(), sendRequest.getTemplateParams()));
    }

}
