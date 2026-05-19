package com.focela.platform.system.controller.admin.user.response.profile;

import com.focela.platform.system.controller.admin.department.response.dept.DepartmentSimpleResponse;
import com.focela.platform.system.controller.admin.department.response.post.PostSimpleResponse;
import com.focela.platform.system.controller.admin.permission.response.role.RoleSimpleResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Admin - user profile info Response")
public class UserProfileResponse {

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela")
    private String username;

    @Schema(description = "Nickname", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    private String nickname;

    @Schema(description = "Email", example = "user@example.com")
    private String email;

    @Schema(description = "Mobile number", example = "15601691300")
    private String mobile;

    @Schema(description = "Gender, see SexEnum", example = "1")
    private Integer sex;

    @Schema(description = "Avatar", example = "https://www.example.com/xxx.png")
    private String avatar;

    @Schema(description = "Last login IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "192.168.1.1")
    private String loginIp;

    @Schema(description = "Last login time", requiredMode = Schema.RequiredMode.REQUIRED, example = "timestamp format")
    private LocalDateTime loginDate;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED, example = "timestamp format")
    private LocalDateTime createTime;

    /**
     * Roles the user belongs to
     */
    private List<RoleSimpleResponse> roles;
    /**
     * Department the user belongs to
     */
    private DepartmentSimpleResponse dept;
    /**
     * Post array the user belongs to
     */
    private List<PostSimpleResponse> posts;

}
