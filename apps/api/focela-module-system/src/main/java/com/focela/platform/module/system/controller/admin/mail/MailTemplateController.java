package com.focela.platform.module.system.controller.admin.mail;

import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.mail.dto.template.*;
import com.focela.platform.module.system.repository.entity.mail.MailTemplateEntity;
import com.focela.platform.module.system.service.mail.MailSendService;
import com.focela.platform.module.system.service.mail.MailTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.focela.platform.framework.common.model.CommonResult.success;
import static com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 邮件模版")
@RestController
@RequestMapping("/system/mail-template")
public class MailTemplateController {

    @Resource
    private MailTemplateService mailTempleService;
    @Resource
    private MailSendService mailSendService;

    @PostMapping("/create")
    @Operation(summary = "创建邮件模版")
    @PreAuthorize("@ss.hasPermission('system:mail-template:create')")
    public CommonResult<Long> createMailTemplate(@Valid @RequestBody MailTemplateSaveRequest createRequest){
        return success(mailTempleService.createMailTemplate(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "修改邮件模版")
    @PreAuthorize("@ss.hasPermission('system:mail-template:update')")
    public CommonResult<Boolean> updateMailTemplate(@Valid @RequestBody MailTemplateSaveRequest updateRequest){
        mailTempleService.updateMailTemplate(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除邮件模版")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:mail-template:delete')")
    public CommonResult<Boolean> deleteMailTemplate(@RequestParam("id") Long id) {
        mailTempleService.deleteMailTemplate(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除邮件模版")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:mail-template:delete')")
    public CommonResult<Boolean> deleteMailTemplateList(@RequestParam("ids") List<Long> ids) {
        mailTempleService.deleteMailTemplateList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得邮件模版")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:mail-template:query')")
    public CommonResult<MailTemplateResponse> getMailTemplate(@RequestParam("id") Long id) {
        MailTemplateEntity template = mailTempleService.getMailTemplate(id);
        return success(BeanUtils.toBean(template, MailTemplateResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得邮件模版分页")
    @PreAuthorize("@ss.hasPermission('system:mail-template:query')")
    public CommonResult<PageResult<MailTemplateResponse>> getMailTemplatePage(@Valid MailTemplatePageRequest pageRequest) {
        PageResult<MailTemplateEntity> pageResult = mailTempleService.getMailTemplatePage(pageRequest);
        return success(BeanUtils.toBean(pageResult, MailTemplateResponse.class));
    }

    @GetMapping({"/list-all-simple", "simple-list"})
    @Operation(summary = "获得邮件模版精简列表")
    public CommonResult<List<MailTemplateSimpleResponse>> getSimpleTemplateList() {
        List<MailTemplateEntity> list = mailTempleService.getMailTemplateList();
        return success(BeanUtils.toBean(list, MailTemplateSimpleResponse.class));
    }

    @PostMapping("/send-mail")
    @Operation(summary = "发送短信")
    @PreAuthorize("@ss.hasPermission('system:mail-template:send-mail')")
    public CommonResult<Long> sendMail(@Valid @RequestBody MailTemplateSendRequest sendRequest) {
        return success(mailSendService.sendSingleMailToAdmin(getLoginUserId(),
                sendRequest.getToMails(), sendRequest.getCcMails(), sendRequest.getBccMails(),
                sendRequest.getTemplateCode(), sendRequest.getTemplateParams()));
    }

}
