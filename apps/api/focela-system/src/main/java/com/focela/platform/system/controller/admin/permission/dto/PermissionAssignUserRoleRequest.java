package com.focela.platform.system.controller.admin.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

@Schema(description = "Admin - grant user role Request VO")
@Data
public class PermissionAssignUserRoleRequest {

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "user ID must not be blank")
    private Long userId;

    @Schema(description = "role ID list", example = "1,3,5")
    private Set<Long> roleIds = Collections.emptySet(); // fallback

}
