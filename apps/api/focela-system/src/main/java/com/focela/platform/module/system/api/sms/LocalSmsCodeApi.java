package com.focela.platform.module.system.api.sms;

import com.focela.platform.module.system.api.sms.dto.code.SmsCodeValidateRpcRequest;
import com.focela.platform.module.system.api.sms.dto.code.SmsCodeSendRpcRequest;
import com.focela.platform.module.system.api.sms.dto.code.SmsCodeUseRpcRequest;
import com.focela.platform.module.system.service.sms.SmsCodeService;
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
    public void sendSmsCode(SmsCodeSendRpcRequest reqDTO) {
        smsCodeService.sendSmsCode(reqDTO);
    }

    @Override
    public void useSmsCode(SmsCodeUseRpcRequest reqDTO) {
        smsCodeService.useSmsCode(reqDTO);
    }

    @Override
    public void validateSmsCode(SmsCodeValidateRpcRequest reqDTO) {
        smsCodeService.validateSmsCode(reqDTO);
    }

}
