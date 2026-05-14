package com.focela.platform.module.system.controller.admin.auth.dto;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.module.system.enums.social.SocialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Admin - account/password login Request VO, if login and bind social user, required social starting with param")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthLoginRequest extends CaptchaVerificationRequest {

    @Schema(description = "account", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudaoyuanma")
    @NotEmpty(message = "login account must not be blank")
    @Length(min = 4, max = 30, message = "account length must be 4-30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,30}$", message = "account format must be digits and letters")
    private String username;

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "buzhidao")
    @NotEmpty(message = "password must not be blank")
    @Length(min = 4, max = 16, message = "password length must be 4-16 characters")
    private String password;

    // ========== when binding social login, the following parameters must be passed ==========

    @Schema(description = "social platform type, see SocialTypeEnum enum value", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @InEnum(SocialTypeEnum.class)
    private Integer socialType;

    @Schema(description = "Authorization code", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private String socialCode;

    @Schema(description = "state", requiredMode = Schema.RequiredMode.REQUIRED, example = "9b2ffbc1-7425-4155-9894-9d5c08541d62")
    private String socialState;

    @AssertTrue(message = "authorization code must not be blank")
    public boolean isSocialCodeValid() {
        return socialType == null || StrUtil.isNotEmpty(socialCode);
    }

    @AssertTrue(message = "authorize state must not be blank")
    public boolean isSocialState() {
        return socialType == null || StrUtil.isNotEmpty(socialState);
    }

}