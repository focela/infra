package com.focela.platform.module.system.controller.admin.oauth2.dto.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Admin - [Open API]access token Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2OpenAccessTokenResponse {

    @Schema(description = "Access token", requiredMode = Schema.RequiredMode.REQUIRED, example = "tudou")
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "Refresh token", requiredMode = Schema.RequiredMode.REQUIRED, example = "nice")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @Schema(description = "token type", requiredMode = Schema.RequiredMode.REQUIRED, example = "bearer")
    @JsonProperty("token_type")
    private String tokenType;

    @Schema(description = "expires at,unit: seconds", requiredMode = Schema.RequiredMode.REQUIRED, example = "42430")
    @JsonProperty("expires_in")
    private Long expiresIn;

    @Schema(description = "scope,if multi scope, use space-separated", example = "user_info")
    private String scope;

}
