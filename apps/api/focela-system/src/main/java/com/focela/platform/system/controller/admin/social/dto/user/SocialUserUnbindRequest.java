package com.focela.platform.system.controller.admin.social.dto.user;

import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.system.enums.social.SocialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - unbind social Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialUserUnbindRequest {

    @Schema(description = "Social platform type, see UserSocialTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @InEnum(SocialTypeEnum.class)
    @NotNull(message = "social platform type must not be blank")
    private Integer type;

    @Schema(description = "social user openid", requiredMode = Schema.RequiredMode.REQUIRED, example = "IPRmJ0wvBptiPIlGEZiPewGwiEiE")
    @NotEmpty(message = "social user openid must not be blank")
    private String openid;

}
