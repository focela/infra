package com.focela.platform.system.controller.admin.oauth2.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Admin - OAuth2 update user basic info Request")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserUpdateRequest {

    @Schema(description = "Nickname", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @Size(max = 30, message = "user nickname length must not exceed 30 characters")
    private String nickname;

    @Schema(description = "Email", example = "user@example.com")
    @Email(message = "email format is invalid")
    @Size(max = 50, message = "email length must not exceed 50 characters")
    private String email;

    @Schema(description = "Mobile number", example = "15601691300")
    @Length(min = 11, max = 11, message = "mobile number length must 11 characters")
    private String mobile;

    @Schema(description = "Gender, see SexEnum", example = "1")
    private Integer sex;

}
