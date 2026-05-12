package com.focela.platform.module.system.controller.admin.social.dto.client;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.module.system.enums.social.SocialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - social client create /update Request VO")
@Data
public class SocialClientSaveRequest {

    @Schema(description = "ID", example = "27162")
    private Long id;

    @Schema(description = "Application name", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudaomall")
    @NotNull(message = "应用名不能为空")
    private String name;

    @Schema(description = "Social platform type", requiredMode = Schema.RequiredMode.REQUIRED, example = "31")
    @NotNull(message = "社交平台的类型不能为空")
    @InEnum(SocialTypeEnum.class)
    private Integer socialType;

    @Schema(description = "User type", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "用户类型不能为空")
    @InEnum(UserTypeEnum.class)
    private Integer userType;

    @Schema(description = "Client ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "wwd411c69a39ad2e54")
    @NotNull(message = "客户端编号不能为空")
    private String clientId;

    @Schema(description = "Client secret", requiredMode = Schema.RequiredMode.REQUIRED, example = "peter")
    @NotNull(message = "客户端密钥不能为空")
    private String clientSecret;

    @Schema(description = "Web application agent ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2000045")
    private String agentId;

    @Schema(description = "Public key", example = "2000045")
    private String publicKey;

    @Schema(description = "Status", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

}
