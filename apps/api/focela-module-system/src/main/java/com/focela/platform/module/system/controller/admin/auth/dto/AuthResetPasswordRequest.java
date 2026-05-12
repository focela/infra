package com.focela.platform.module.system.controller.admin.auth.dto;

import com.focela.platform.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Admin - SMS reset account password Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResetPasswordRequest {

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    @NotEmpty(message = "password must not be blank")
    @Length(min = 4, max = 16, message = "password length must be 4-16 characters")
    private String password;

    @Schema(description = "Mobile number", requiredMode = Schema.RequiredMode.REQUIRED, example = "13312341234")
    @NotEmpty(message = "mobile number must not be blank")
    @Mobile
    private String mobile;

    @Schema(description = "mobile SMS CAPTCHA", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "mobile mobile SMS CAPTCHA must not be blank")
    private String code;
}