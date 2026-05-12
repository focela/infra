package com.focela.platform.module.system.controller.admin.permission.dto.permission;

import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.module.system.enums.permission.DataScopeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

@Schema(description = "Admin - grant role data permission Request VO")
@Data
public class PermissionAssignRoleDataScopeRequest {

    @Schema(description = "Role ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色编号不能为空")
    private Long roleId;

    @Schema(description = "Data scope, see DataScopeEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "数据范围不能为空")
    @InEnum(value = DataScopeEnum.class, message = "数据范围必须是 {value}")
    private Integer dataScope;

    @Schema(description = "department ID list, only has scope type as DEPT_CUSTOM when, this field is required", example = "1,3,5")
    private Set<Long> dataScopeDeptIds = Collections.emptySet(); // 兜底

}
