package com.focela.platform.system.controller.admin.auth.dto;

import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.framework.common.validation.Mobile;
import com.focela.platform.system.enums.sms.SmsSceneEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - send mobile CAPTCHA Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSmsSendRequest extends CaptchaVerificationRequest {

    @Schema(description = "Mobile number", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudaoyuanma")
    @NotEmpty(message = "mobile number must not be blank")
    @Mobile
    private String mobile;

    @Schema(description = "SMS scenario", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "send scenario must not be blank")
    @InEnum(SmsSceneEnum.class)
    private Integer scene;

}
