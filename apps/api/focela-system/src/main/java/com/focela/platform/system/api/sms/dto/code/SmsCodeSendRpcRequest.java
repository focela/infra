package com.focela.platform.system.api.sms.dto.code;

import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.framework.common.validation.Mobile;
import com.focela.platform.system.enums.sms.SmsSceneEnum;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * SMS verification code send Request DTO
 */
@Data
public class SmsCodeSendRpcRequest {

    /**
     * Mobile number
     */
    @Mobile
    @NotEmpty(message = "mobile number must not be blank")
    private String mobile;
    /**
     * Send scenario
     */
    @NotNull(message = "send scenario must not be blank")
    @InEnum(SmsSceneEnum.class)
    private Integer scene;
    /**
     * Send IP
     */
    @NotEmpty(message = "send IP must not be blank")
    private String createIp;

}
