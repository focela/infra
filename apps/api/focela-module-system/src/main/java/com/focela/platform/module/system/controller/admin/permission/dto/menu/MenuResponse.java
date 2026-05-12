package com.focela.platform.module.system.controller.admin.permission.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Schema(description = "Admin - menu info Response VO")
@Data
public class MenuResponse {

    @Schema(description = "Menu ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "Menu name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String name;

    @Schema(description = "Permission code (only required for button type)", example = "sys:menu:add")
    @Size(max = 100)
    private String permission;

    @Schema(description = "Type, see MenuTypeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "菜单类型不能为空")
    private Integer type;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "Parent menu ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "父菜单 ID 不能为空")
    private Long parentId;

    @Schema(description = "Route path (only required for menu or directory type)", example = "post")
    @Size(max = 200, message = "路由地址不能超过200个字符")
    private String path;

    @Schema(description = "Menu icon (only required for menu or directory type)", example = "/menu/list")
    private String icon;

    @Schema(description = "Component path (only required for menu type)", example = "system/post/index")
    @Size(max = 200, message = "组件路径不能超过255个字符")
    private String component;

    @Schema(description = "Component name", example = "SystemUser")
    private String componentName;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
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
