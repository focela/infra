package com.focela.platform.system.api.sms;

import com.focela.platform.system.api.sms.dto.code.SmsCodeValidateRpcRequest;
import com.focela.platform.system.api.sms.dto.code.SmsCodeSendRpcRequest;
import com.focela.platform.system.api.sms.dto.code.SmsCodeUseRpcRequest;
import com.focela.platform.system.service.sms.SmsCodeService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * SMS verification code API implementation class
 */
@Service
@Validated
public class LocalSmsCodeApi implements SmsCodeApi {

    @Resource
    private SmsCodeService smsCodeService;

    @Override
    public void sendSmsCode(SmsCodeSendRpcRequest request) {
        smsCodeService.sendSmsCode(request);
    }

    @Override
    public void useSmsCode(SmsCodeUseRpcRequest request) {
        smsCodeService.useSmsCode(request);
    }

    @Override
    public void validateSmsCode(SmsCodeValidateRpcRequest request) {
        smsCodeService.validateSmsCode(request);
    }

}
