package com.focela.platform.system.controller.admin.oauth2.response.open;

import com.focela.platform.common.core.KeyValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Admin - authorize page info Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2OpenAuthorizeInfoResponse {

    /**
     * Client
     */
    private Client client;

    @Schema(description = "scope selection info,use List ensure ordering, Key is scope, Value as selected", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<KeyValue<String, Boolean>> scopes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Client {

        @Schema(description = "Application name", requiredMode = Schema.RequiredMode.REQUIRED, example = "potato")
        private String name;

        @Schema(description = "Application icon", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.example.com/xx.png")
        private String logo;

    }

}
