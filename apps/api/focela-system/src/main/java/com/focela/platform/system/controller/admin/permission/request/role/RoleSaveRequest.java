package com.focela.platform.system.controller.admin.permission.request.role;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.validation.InEnum;
import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Admin - role create /update Request VO")
@Data
public class RoleSaveRequest {

    @Schema(description = "Role ID", example = "1")
    private Long id;

    @Schema(description = "Role name", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    @NotBlank(message = "role name must not be blank")
    @Size(max = 30, message = "role name length must not exceed 30 characters")
    @DiffLogField(name = "Role name")
    private String name;

    @NotBlank(message = "role code must not be blank")
    @Size(max = 100, message = "role code length must not exceed 100 characters")
    @Schema(description = "Role code", requiredMode = Schema.RequiredMode.REQUIRED, example = "ADMIN")
    @DiffLogField(name = "Role code")
    private String code;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "display order must not be blank")
    @DiffLogField(name = "Display order")
    private Integer sort;

    @Schema(description = "Status", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @DiffLogField(name = "Status")
    @NotNull(message = "status must not be blank")
    @InEnum(value = CommonStatusEnum.class, message = "status must be {value}")
    private Integer status;

    @Schema(description = "Remarks", example = "I am a role")
    @Size(max = 500, message = "remarks length must not exceed 500 characters")
    @DiffLogField(name = "Remarks")
    private String remark;

}
