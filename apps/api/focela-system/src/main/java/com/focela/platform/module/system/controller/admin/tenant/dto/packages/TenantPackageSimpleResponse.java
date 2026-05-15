package com.focela.platform.module.system.controller.admin.tenant.dto.packages;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - tenant package simplified Response VO")
@Data
public class TenantPackageSimpleResponse {

    @Schema(description = "Package ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "package ID must not be blank")
    private Long id;

    @Schema(description = "Package name", requiredMode = Schema.RequiredMode.REQUIRED, example = "VIP")
    @NotNull(message = "package name must not be blank")
    private String name;

}
