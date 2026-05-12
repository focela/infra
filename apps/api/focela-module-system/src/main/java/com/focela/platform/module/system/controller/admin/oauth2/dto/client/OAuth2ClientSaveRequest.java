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
    @NotNull(message = "客户端编号不能为空")
    private String clientId;

    @Schema(description = "Client secret", requiredMode = Schema.RequiredMode.REQUIRED, example = "fan")
    @NotNull(message = "客户端密钥不能为空")
    private String secret;

    @Schema(description = "Application name", requiredMode = Schema.RequiredMode.REQUIRED, example = "potato")
    @NotNull(message = "应用名不能为空")
    private String name;

    @Schema(description = "Application icon", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.example.com/xx.png")
    @NotNull(message = "应用图标不能为空")
    @URL(message = "应用图标的地址不正确")
    private String logo;

    @Schema(description = "Application description", example = "I am an application")
    private String description;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "Access token TTL", requiredMode = Schema.RequiredMode.REQUIRED, example = "8640")
    @NotNull(message = "访问令牌的有效期不能为空")
    private Integer accessTokenValiditySeconds;

    @Schema(description = "Refresh token TTL", requiredMode = Schema.RequiredMode.REQUIRED, example = "8640000")
    @NotNull(message = "刷新令牌的有效期不能为空")
    private Integer refreshTokenValiditySeconds;

    @Schema(description = "Allowed redirect URIs", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.example.com")
    @NotNull(message = "可重定向的 URI 地址不能为空")
    private List<@NotEmpty(message = "重定向的 URI 不能为空") @URL(message = "重定向的 URI 格式不正确") String> redirectUris;

    @Schema(description = "Grant type, see OAuth2GrantTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "password")
    @NotNull(message = "授权类型不能为空")
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

    @AssertTrue(message = "附加信息必须是 JSON 格式")
    public boolean isAdditionalInformationJson() {
        return StrUtil.isEmpty(additionalInformation) || JsonUtils.isJson(additionalInformation);
    }

}
