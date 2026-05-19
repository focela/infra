package com.focela.platform.system.controller.admin.social.request.user;

import com.focela.platform.system.enums.social.SocialTypeEnum;
import com.focela.platform.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - social bind Request VO, use code authorization code")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialUserBindRequest {

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
