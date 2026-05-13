package com.focela.platform.module.system.controller.admin.oauth2.dto.client;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.utils.json.JsonUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "Admin - OAuth2 client create /update Request VO")
@Data
public class OAuth2ClientSaveRequest {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "Client ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "tudou")
    @NotNull(message = "client ID must not be blank")
    private String clientId;

    @Schema(description = "Client secret", requiredMode = Schema.RequiredMode.REQUIRED, example = "fan")
    @NotNull(message = "client secret must not be blank")
    private String secret;

    @Schema(description = "Application name", requiredMode = Schema.RequiredMode.REQUIRED, example = "potato")
    @NotNull(message = "application name must not be blank")
    private String name;

    @Schema(description = "Application icon", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.example.com/xx.png")
    @NotNull(message = "application icon must not be blank")
    @URL(message = "application icon address is invalid")
    private String logo;

    @Schema(description = "Application description", example = "I am an application")
    private String description;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "status must not be blank")
    private Integer status;

    @Schema(description = "Access token TTL", requiredMode = Schema.RequiredMode.REQUIRED, example = "8640")
    @NotNull(message = "access token valid 期must not be blank")
    private Integer accessTokenValiditySeconds;

    @Schema(description = "Refresh token TTL", requiredMode = Schema.RequiredMode.REQUIRED, example = "8640000")
    @NotNull(message = "refresh token valid 期must not be blank")
    private Integer refreshTokenValiditySeconds;

    @Schema(description = "Allowed redirect URIs", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.example.com")
    @NotNull(message = "重定向 URI address must not be blank")
    private List<@NotEmpty(message = "重定向 URI must not be blank") @URL(message = "重定向 URI format is invalid") String> redirectUris;

    @Schema(description = "Grant type, see OAuth2GrantTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "password")
    @NotNull(message = "authorize type must not be blank")
    private List<String> authorizedGrantTypes;

    @Schema(description = "Scope", example = "user_info")
    private List<String> scopes;

    @Schema(description = "Auto-approve scope", example = "user_info")
    private List<String> autoApproveScopes;

    @Schema(description = "Permission", example = "system:user:query")
    private List<String> authorities;

    @Schema(description = "Resource", example = "1024")
    private List<String> resourceIds;

    @Schema(description = "Extra info", example = "{yunai: true}")
    private String additionalInformation;

    @AssertTrue(message = "附加信息must be JSON format")
    public boolean isAdditionalInformationJson() {
        return StrUtil.isEmpty(additionalInformation) || JsonUtils.isJson(additionalInformation);
    }

}
