package com.focela.platform.module.system.api.sms.dto.code;

import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.framework.common.validation.Mobile;
import com.focela.platform.module.system.api.sms.enums.SmsSceneEnum;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 短信验证码的校验 Request DTO
 */
@Data
public class SmsCodeValidateReqDTO {

    /**
     * 手机号
     */
    @Mobile
    @NotEmpty(message = "mobile number must not be blank")
    private String mobile;
    /**
     * 发送场景
     */
    @NotNull(message = "send scenario must not be blank")
    @InEnum(SmsSceneEnum.class)
    private Integer scene;
    /**
     * 验证码
     */
    @NotEmpty(message = "CAPTCHA")
    private String code;

}
