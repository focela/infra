package com.focela.platform.system.controller.admin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Schema(description = "Admin - login user permission info Response VO, additional include user info and role list")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthPermissionInfoResponse {

    @Schema(description = "user info", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserVO user;

    @Schema(description = "role code array", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<String> roles;

    @Schema(description = "operation permission array", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<String> permissions;

    @Schema(description = "menu tree", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MenuVO> menus;

    @Schema(description = "user info VO")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserVO {

        @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
        private Long id;

        @Schema(description = "Nickname", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
        private String nickname;

        @Schema(description = "Avatar", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.example.com/xx.jpg")
        private String avatar;

        @Schema(description = "Department ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
        private Long deptId;

        @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
        private String username;

        @Schema(description = "Email", example = "user@example.com")
        private String email;

    }

    @Schema(description = "Admin - Login user menu info response")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MenuVO {

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
         * child routes
         */
        private List<MenuVO> children;

    }

}
