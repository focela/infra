package com.focela.platform.system.controller.admin.user.request.profile;

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

    @Schema(description = "role avatar", example = "https://www.example.com/1.png")
    @URL(message = "avatar address format is invalid")
    private String avatar;

}
