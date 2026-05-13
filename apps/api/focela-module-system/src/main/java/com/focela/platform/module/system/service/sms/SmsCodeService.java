package com.focela.platform.module.system.service.sms;

import com.focela.platform.framework.common.exception.ServiceException;
import com.focela.platform.module.system.api.sms.dto.code.SmsCodeValidateRpcRequest;
import com.focela.platform.module.system.api.sms.dto.code.SmsCodeSendRpcRequest;
import com.focela.platform.module.system.api.sms.dto.code.SmsCodeUseRpcRequest;

import jakarta.validation.Valid;

/**
 * 短信验证码 Service 接口
 */
public interface SmsCodeService {

    /**
     * 创建短信验证码，并进行发送
     *
     * @param reqDTO 发送请求
     */
    void sendSmsCode(@Valid SmsCodeSendRpcRequest reqDTO);

    /**
     * 验证短信验证码，并进行使用
     * 如果正确，则将验证码标记成已使用
     * 如果错误，则抛出 {@link ServiceException} 异常
     *
     * @param reqDTO 使用请求
     */
    void useSmsCode(@Valid SmsCodeUseRpcRequest reqDTO);

    /**
     * 检查验证码是否有效
     *
     * @param reqDTO 校验请求
     */
    void validateSmsCode(@Valid SmsCodeValidateRpcRequest reqDTO);

}
