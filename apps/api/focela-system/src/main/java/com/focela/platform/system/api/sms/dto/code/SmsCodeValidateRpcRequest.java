package com.focela.platform.system.api.sms.dto.code;

import com.focela.platform.common.validation.InEnum;
import com.focela.platform.common.validation.Mobile;
import com.focela.platform.system.enums.sms.SmsSceneEnum;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * SMS verification code validation request
 */
@Data
public class SmsCodeValidateRpcRequest {

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
     * Verification code
     */
    @NotEmpty(message = "verification code must not be blank")
    private String code;

}
