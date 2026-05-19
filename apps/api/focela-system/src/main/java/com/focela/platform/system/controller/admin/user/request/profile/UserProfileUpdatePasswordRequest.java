package com.focela.platform.system.controller.admin.user.request.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Admin - user profile update password Request VO")
@Data
public class UserProfileUpdatePasswordRequest {

    @Schema(description = "old password", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "old password must not be blank")
    @Length(min = 4, max = 16, message = "password length must be 4-16 characters")
    private String oldPassword;

    @Schema(description = "new password", requiredMode = Schema.RequiredMode.REQUIRED, example = "654321")
    @NotEmpty(message = "new password must not be blank")
    @Length(min = 4, max = 16, message = "password length must be 4-16 characters")
    private String newPassword;

}
