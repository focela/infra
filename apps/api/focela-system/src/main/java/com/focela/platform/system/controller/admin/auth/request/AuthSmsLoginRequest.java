package com.focela.platform.system.controller.admin.auth.request;

import com.focela.platform.common.validation.Mobile;
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

    @Schema(description = "Mobile number", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela")
    @NotEmpty(message = "mobile number must not be blank")
    @Mobile
    private String mobile;

    @Schema(description = "SMS CAPTCHA", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "CAPTCHA must not be blank")
    private String code;

}
