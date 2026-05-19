package com.focela.platform.system.controller.admin.social.request.client;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.validation.InEnum;
import com.focela.platform.system.enums.social.SocialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - social client create/update Request")
@Data
public class SocialClientSaveRequest {

    @Schema(description = "ID", example = "27162")
    private Long id;

    @Schema(description = "Application name", requiredMode = Schema.RequiredMode.REQUIRED, example = "focelamall")
    @NotNull(message = "application name must not be blank")
    private String name;

    @Schema(description = "Social platform type", requiredMode = Schema.RequiredMode.REQUIRED, example = "31")
    @NotNull(message = "social platform type must not be blank")
    @InEnum(SocialTypeEnum.class)
    private Integer socialType;

    @Schema(description = "User type", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "user type must not be blank")
    @InEnum(UserTypeEnum.class)
    private Integer userType;

    @Schema(description = "Client ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "wwd411c69a39ad2e54")
    @NotNull(message = "client ID must not be blank")
    private String clientId;

    @Schema(description = "Client secret", requiredMode = Schema.RequiredMode.REQUIRED, example = "peter")
    @NotNull(message = "client secret must not be blank")
    private String clientSecret;

    @Schema(description = "Web application agent ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2000045")
    private String agentId;

    @Schema(description = "Public key", example = "2000045")
    private String publicKey;

    @Schema(description = "Status", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "status must not be blank")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

}
