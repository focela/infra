package com.focela.platform.module.system.controller.admin.tenant.dto.packages;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - tenant package simplified Response VO")
@Data
public class TenantPackageSimpleResponse {

    @Schema(description = "Package ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "套餐编号不能为空")
    private Long id;

    @Schema(description = "Package name", requiredMode = Schema.RequiredMode.REQUIRED, example = "VIP")
    @NotNull(message = "套餐名不能为空")
    private String name;

}
