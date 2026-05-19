package com.focela.platform.system.controller.admin.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - user update password Request")
@Data
public class UserUpdatePasswordRequest {

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "user ID must not be blank")
    private Long id;

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "password must not be blank")
    @Length(min = 4, max = 16, message = "password length must be 4-16 characters")
    private String password;

}
