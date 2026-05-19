package com.focela.platform.system.controller.admin.permission.response.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Schema(description = "Admin - menu info Response")
@Data
public class MenuResponse {

    @Schema(description = "Menu ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "Menu name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    @NotBlank(message = "menu name must not be blank")
    @Size(max = 50, message = "menu name length must not exceed 50characters")
    private String name;

    @Schema(description = "Permission code (only required for button type)", example = "sys:menu:add")
    @Size(max = 100)
    private String permission;

    @Schema(description = "Type, see MenuTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "menu type must not be blank")
    private Integer type;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "display order must not be blank")
    private Integer sort;

    @Schema(description = "Parent menu ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "parent menu ID must not be blank")
    private Long parentId;

    @Schema(description = "Route path (only required for menu or directory type)", example = "post")
    @Size(max = 200, message = "route path must not exceed 200characters")
    private String path;

    @Schema(description = "Menu icon (only required for menu or directory type)", example = "/menu/list")
    private String icon;

    @Schema(description = "Component path (only required for menu type)", example = "system/post/index")
    @Size(max = 200, message = "component path must not exceed 255characters")
    private String component;

    @Schema(description = "Component name", example = "SystemUser")
    private String componentName;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "status must not be blank")
    private Integer status;

    @Schema(description = "Visible", example = "false")
    private Boolean visible;

    @Schema(description = "Cache enabled", example = "false")
    private Boolean keepAlive;

    @Schema(description = "Always show", example = "false")
    private Boolean alwaysShow;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED, example = "timestamp format")
    private LocalDateTime createTime;

}
