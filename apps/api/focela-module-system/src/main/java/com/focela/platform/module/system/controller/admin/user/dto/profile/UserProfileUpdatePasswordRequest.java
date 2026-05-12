package com.focela.platform.module.system.controller.admin.user.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Admin - user profile update password Request VO")
@Data
public class UserProfileUpdatePasswordRequest {

    @Schema(description = "old password", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "旧密码不能为空")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String oldPassword;

    @Schema(description = "new password", requiredMode = Schema.RequiredMode.REQUIRED, example = "654321")
    @NotEmpty(message = "新密码不能为空")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String newPassword;

}
