package com.focela.platform.module.system.controller.admin.social.dto.user;

import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.module.system.enums.social.SocialTypeEnum;
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
    @NotNull(message = "社交平台的类型不能为空")
    private Integer type;

    @Schema(description = "social user openid", requiredMode = Schema.RequiredMode.REQUIRED, example = "IPRmJ0wvBptiPIlGEZiPewGwiEiE")
    @NotEmpty(message = "社交用户的 openid 不能为空")
    private String openid;

}
