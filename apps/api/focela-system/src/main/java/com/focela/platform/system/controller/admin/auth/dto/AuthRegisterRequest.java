package com.focela.platform.system.controller.admin.auth.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.*;

@Schema(description = "Admin - Register Request VO")
@Data
public class AuthRegisterRequest extends CaptchaVerificationRequest {

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @NotBlank(message = "user account must not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,30}$", message = "user account consists of digit, letter consists of")
    @Size(min = 4, max = 30, message = "user account length must be 4-30 characters")
    private String username;

    @Schema(description = "Nickname", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @NotBlank(message = "user nickname must not be blank")
    @Size(max = 30, message = "user nickname length must not exceed 30 characters")
    private String nickname;

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "password must not be blank")
    @Length(min = 4, max = 16, message = "password length must be 4-16 characters")
    private String password;
}