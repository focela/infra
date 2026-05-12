package com.focela.platform.module.system.controller.admin.auth.dto;

import com.focela.platform.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Admin - SMS CAPTCHA login Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSmsLoginRequest {

    @Schema(description = "Mobile number", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudaoyuanma")
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @Schema(description = "SMS CAPTCHA", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "验证码不能为空")
    private String code;

}
