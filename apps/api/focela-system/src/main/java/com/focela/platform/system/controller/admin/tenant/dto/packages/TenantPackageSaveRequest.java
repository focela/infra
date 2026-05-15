package com.focela.platform.system.controller.admin.tenant.dto.packages;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

@Schema(description = "Admin - tenant package create /update Request VO")
@Data
public class TenantPackageSaveRequest {

    @Schema(description = "Package ID", example = "1024")
    private Long id;

    @Schema(description = "Package name", requiredMode = Schema.RequiredMode.REQUIRED, example = "VIP")
    @NotEmpty(message = "package name must not be blank")
    private String name;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "status must not be blank")
    @InEnum(value = CommonStatusEnum.class, message = "status must be {value}")
    private Integer status;

    @Schema(description = "Remarks", example = "good")
    private String remark;

    @Schema(description = "Related menu ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "related menu ID must not be blank")
    private Set<Long> menuIds;

}
