package com.focela.platform.module.system.api.sms;

import com.focela.platform.framework.common.exception.ServiceException;
import com.focela.platform.module.system.api.sms.dto.code.SmsCodeValidateRpcRequest;
import com.focela.platform.module.system.api.sms.dto.code.SmsCodeSendRpcRequest;
import com.focela.platform.module.system.api.sms.dto.code.SmsCodeUseRpcRequest;

import jakarta.validation.Valid;

/**
 * SMS verification code API interface
 */
public interface SmsCodeApi {

    /**
     * Create and send an SMS verification code
     *
     * @param reqDTO send request
     */
    void sendSmsCode(@Valid SmsCodeSendRpcRequest reqDTO);

    /**
     * Validate the SMS verification code and consume it
     * If correct, mark the code as used
     * If incorrect, throw {@link ServiceException}
     *
     * @param reqDTO use request
     */
    void useSmsCode(@Valid SmsCodeUseRpcRequest reqDTO);

    /**
     * Check whether the verification code is valid
     *
     * @param reqDTO validation request
     */
    void validateSmsCode(@Valid SmsCodeValidateRpcRequest reqDTO);

}
