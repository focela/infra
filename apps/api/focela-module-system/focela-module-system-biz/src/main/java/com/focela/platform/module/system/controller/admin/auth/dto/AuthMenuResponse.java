package com.focela.platform.module.system.controller.admin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Admin - Login user menu info response")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthMenuResponse {

    @Schema(description = "Menu name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    private Long id;

    @Schema(description = "Parent menu ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long parentId;

    @Schema(description = "Menu name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    private String name;

    @Schema(description = "Route path (only required for menu or directory type)", example = "post")
    private String path;

    @Schema(description = "Component path (only required for menu type)", example = "system/post/index")
    private String component;

    @Schema(description = "Component name", example = "SystemUser")
    private String componentName;

    @Schema(description = "Menu icon (only required for menu or directory type)", example = "/menu/list")
    private String icon;

    @Schema(description = "Visible", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean visible;

    @Schema(description = "Cache enabled", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean keepAlive;

    @Schema(description = "Always show", example = "false")
    private Boolean alwaysShow;

    /**
     * 子路由
     */
    private List<AuthMenuResponse> children;

}
