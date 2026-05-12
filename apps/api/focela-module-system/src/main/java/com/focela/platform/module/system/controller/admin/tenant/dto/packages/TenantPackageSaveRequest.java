package com.focela.platform.module.system.controller.admin.tenant.dto.packages;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.validation.InEnum;
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
    @NotEmpty(message = "套餐名不能为空")
    private String name;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "状态必须是 {value}")
    private Integer status;

    @Schema(description = "Remarks", example = "good")
    private String remark;

    @Schema(description = "Related menu ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "关联的菜单编号不能为空")
    private Set<Long> menuIds;

}
