package com.focela.platform.module.system.api.sms.dto.code;

import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.framework.common.validation.Mobile;
import com.focela.platform.module.system.enums.sms.SmsSceneEnum;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 短信验证码的使用 Request DTO
 */
@Data
public class SmsCodeUseRpcRequest {

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
    /**
     * 使用 IP
     */
    @NotEmpty(message = "use IP must not be blank")
    private String usedIp;

}
