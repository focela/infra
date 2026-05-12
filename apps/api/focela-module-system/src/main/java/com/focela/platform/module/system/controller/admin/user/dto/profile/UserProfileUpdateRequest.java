package com.focela.platform.module.system.controller.admin.user.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;


@Schema(description = "Admin - user personal info update Request VO")
@Data
public class UserProfileUpdateRequest {

    @Schema(description = "Nickname", example = "Alice")
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

    @Schema(description = "role avatar", example = "https://www.example.com/1.png")
    @URL(message = "头像地址格式不正确")
    private String avatar;

}
