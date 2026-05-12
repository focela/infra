package com.focela.platform.module.system.controller.admin.oauth2.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Admin - OAuth2 update user basic info Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserUpdateRequest {

    @Schema(description = "Nickname", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @Size(max = 30, message = "用户昵称长度不能超过 30 个字符")
    private String nickname;

    @Schema(description = "Email", example = "user@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;

    @Schema(description = "Mobile number", example = "15601691300")
    @Length(min = 11, max = 11, message = "手机号长度必须 11 位")
    private String mobile;

    @Schema(description = "Gender, see SexEnum", example = "1")
    private Integer sex;

}
