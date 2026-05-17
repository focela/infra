package com.focela.platform.system.api.sms;

import com.focela.platform.system.api.sms.dto.send.SmsSendSingleToUserRpcRequest;
import com.focela.platform.system.service.sms.SmsSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * SMS send API implementation class
 */
@Service
@Validated
@RequiredArgsConstructor
public class LocalSmsSendApi implements SmsSendApi {

    private final SmsSendService smsSendService;

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
