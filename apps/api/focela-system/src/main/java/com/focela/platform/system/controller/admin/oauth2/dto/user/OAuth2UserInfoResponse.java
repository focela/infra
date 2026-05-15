package com.focela.platform.system.controller.admin.oauth2.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Admin - OAuth2 get user basic info Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserInfoResponse {

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    private String username;

    @Schema(description = "Nickname", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    private String nickname;

    @Schema(description = "Email", example = "user@example.com")
    private String email;
    @Schema(description = "Mobile number", example = "15601691300")
    private String mobile;

    @Schema(description = "Gender, see SexEnum", example = "1")
    private Integer sex;

    @Schema(description = "Avatar", example = "https://www.example.com/xxx.png")
    private String avatar;

    /**
     * 所在部门
     */
    private Department dept;

    /**
     * 所属岗位数组
     */
    private List<Post> posts;

    @Schema(description = "Department")
    @Data
    public static class Department {

        @Schema(description = "Department ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "Department name", requiredMode = Schema.RequiredMode.REQUIRED, example = "R&D dept")
        private String name;

    }

    @Schema(description = "Post")
    @Data
    public static class Post {

        @Schema(description = "Post ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "Post name", requiredMode = Schema.RequiredMode.REQUIRED, example = "dev")
        private String name;

    }

}
