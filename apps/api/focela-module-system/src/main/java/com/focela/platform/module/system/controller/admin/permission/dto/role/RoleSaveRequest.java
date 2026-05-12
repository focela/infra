package com.focela.platform.module.system.controller.admin.permission.dto.role;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.validation.InEnum;
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
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过 30 个字符")
    @DiffLogField(name = "Role name")
    private String name;

    @NotBlank(message = "角色标志不能为空")
    @Size(max = 100, message = "角色标志长度不能超过 100 个字符")
    @Schema(description = "Role code", requiredMode = Schema.RequiredMode.REQUIRED, example = "ADMIN")
    @DiffLogField(name = "Role code")
    private String code;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "显示顺序不能为空")
    @DiffLogField(name = "Display order")
    private Integer sort;

    @Schema(description = "Status", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @DiffLogField(name = "Status")
    @NotNull(message = "状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "状态必须是 {value}")
    private Integer status;

    @Schema(description = "Remarks", example = "I am a role")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    @DiffLogField(name = "Remarks")
    private String remark;

}
