package com.focela.platform.system.api.mail;

import com.focela.platform.system.api.mail.dto.MailSendSingleToUserRpcRequest;
import com.focela.platform.system.service.mail.MailSendService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * Mail send API implementation class
 */
@Service
@Validated
public class LocalMailSendApi implements MailSendApi {

    @Resource
    private MailSendService mailSendService;

    @Override
    public Long sendSingleMailToAdmin(MailSendSingleToUserRpcRequest request) {
        return mailSendService.sendSingleMailToAdmin(request.getUserId(),
                request.getToMails(), request.getCcMails(), request.getBccMails(),
                request.getTemplateCode(), request.getTemplateParams(), request.getAttachments());
    }

    @Override
    public Long sendSingleMailToMember(MailSendSingleToUserRpcRequest request) {
        return mailSendService.sendSingleMailToMember(request.getUserId(),
                request.getToMails(), request.getCcMails(), request.getBccMails(),
                request.getTemplateCode(), request.getTemplateParams(), request.getAttachments());
    }

}
