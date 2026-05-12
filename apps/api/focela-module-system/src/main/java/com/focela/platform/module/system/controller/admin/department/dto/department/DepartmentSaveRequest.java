package com.focela.platform.module.system.controller.admin.department.dto.dept;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Admin - department create /update Request VO")
@Data
public class DepartmentSaveRequest {

    @Schema(description = "Department ID", example = "1024")
    private Long id;

    @Schema(description = "Department name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 30, message = "部门名称长度不能超过 30 个字符")
    private String name;

    @Schema(description = "Parent department ID", example = "1024")
    private Long parentId;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "Owner user ID", example = "2048")
    private Long leaderUserId;

    @Schema(description = "Contact phone", example = "15601691000")
    @Size(max = 11, message = "联系电话长度不能超过11个字符")
    private String phone;

    @Schema(description = "Email", example = "user@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    private Integer status;

}
