package com.focela.platform.system.controller.admin.permission.request;

import com.focela.platform.common.validation.InEnum;
import com.focela.platform.system.enums.permission.DataScopeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

@Schema(description = "Admin - grant role data permission Request VO")
@Data
public class PermissionAssignRoleDataScopeRequest {

    @Schema(description = "Role ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "role ID must not be blank")
    private Long roleId;

    @Schema(description = "Data scope, see DataScopeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "data scope must not be blank")
    @InEnum(value = DataScopeEnum.class, message = "data scope must be {value}")
    private Integer dataScope;

    @Schema(description = "department ID list, only has scope type as DEPT_CUSTOM when, this field is required", example = "1,3,5")
    private Set<Long> dataScopeDeptIds = Collections.emptySet(); // fallback

}
