package com.focela.platform.system.controller.admin.permission.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Admin - role simplified info Response VO")
@Data
public class RoleSimpleResponse {

    @Schema(description = "Role ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "Role name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    private String name;

}
