package com.focela.platform.module.system.controller.admin.auth.dto;

import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.module.system.enums.social.SocialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - social-bind login Request VO, use code authorization code + account password")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSocialLoginRequest {

    @Schema(description = "Social platform type, see UserSocialTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @InEnum(SocialTypeEnum.class)
    @NotNull(message = "social platform type must not be blank")
    private Integer type;

    @Schema(description = "Authorization code", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "authorization code must not be blank")
    private String code;

    @Schema(description = "state", requiredMode = Schema.RequiredMode.REQUIRED, example = "9b2ffbc1-7425-4155-9894-9d5c08541d62")
    @NotEmpty(message = "state must not be blank")
    private String state;

}
