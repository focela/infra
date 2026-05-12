package com.focela.platform.module.system.controller.admin.permission.dto.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

@Schema(description = "Admin - grant role menus Request VO")
@Data
public class PermissionAssignRoleMenuRequest {

    @Schema(description = "Role ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "role ID must not be blank")
    private Long roleId;

    @Schema(description = "menu ID list", example = "1,3,5")
    private Set<Long> menuIds = Collections.emptySet(); // 兜底

}
