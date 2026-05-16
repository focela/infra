package com.focela.platform.system.api.sms;

import com.focela.platform.system.api.sms.dto.send.SmsSendSingleToUserRpcRequest;
import com.focela.platform.system.service.sms.SmsSendService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * SMS send API implementation class
 */
@Service
@Validated
public class LocalSmsSendApi implements SmsSendApi {

    @Resource
    private SmsSendService smsSendService;

    @Override
    public Long sendSingleSmsToAdmin(SmsSendSingleToUserRpcRequest request) {
        return smsSendService.sendSingleSmsToAdmin(request.getMobile(), request.getUserId(),
                request.getTemplateCode(), request.getTemplateParams());
    }

    @Override
    public Long sendSingleSmsToMember(SmsSendSingleToUserRpcRequest request) {
        return smsSendService.sendSingleSmsToMember(request.getMobile(), request.getUserId(),
                request.getTemplateCode(), request.getTemplateParams());
    }

}
