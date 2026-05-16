package com.focela.platform.system.service.sms;

import com.focela.platform.common.exception.ServiceException;
import com.focela.platform.system.api.sms.dto.code.SmsCodeValidateRpcRequest;
import com.focela.platform.system.api.sms.dto.code.SmsCodeSendRpcRequest;
import com.focela.platform.system.api.sms.dto.code.SmsCodeUseRpcRequest;

import jakarta.validation.Valid;

/**
 * SMS verification code Service interface
 */
public interface SmsCodeService {

    /**
     * Create an SMS verification code and send it
     *
     * @param reqDTO send request
     */
    void sendSmsCode(@Valid SmsCodeSendRpcRequest reqDTO);

    /**
     * Validate the SMS verification code and consume it.
     * If correct, mark the code as used.
     * If incorrect, throw {@link ServiceException}.
     *
     * @param reqDTO use request
     */
    void useSmsCode(@Valid SmsCodeUseRpcRequest reqDTO);

    /**
     * Check whether the verification code is valid
     *
     * @param reqDTO validate request
     */
    void validateSmsCode(@Valid SmsCodeValidateRpcRequest reqDTO);

}
