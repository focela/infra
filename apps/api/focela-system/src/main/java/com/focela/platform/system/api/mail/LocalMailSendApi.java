package com.focela.platform.system.api.mail;

import com.focela.platform.system.api.mail.dto.MailSendSingleToUserRpcRequest;
import com.focela.platform.system.service.mail.MailSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Mail send API implementation class
 */
@Service
@Validated
@RequiredArgsConstructor
public class LocalMailSendApi implements MailSendApi {

    private final MailSendService mailSendService;

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
