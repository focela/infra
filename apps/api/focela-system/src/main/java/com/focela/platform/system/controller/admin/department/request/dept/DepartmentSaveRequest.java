package com.focela.platform.system.controller.admin.department.request.dept;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Admin - department create /update Request")
@Data
public class DepartmentSaveRequest {

    @Schema(description = "Department ID", example = "1024")
    private Long id;

    @Schema(description = "Department name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    @NotBlank(message = "department name must not be blank")
    @Size(max = 30, message = "department name length must not exceed 30 characters")
    private String name;

    @Schema(description = "Parent department ID", example = "1024")
    private Long parentId;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "display order must not be blank")
    private Integer sort;

    @Schema(description = "Owner user ID", example = "2048")
    private Long leaderUserId;

    @Schema(description = "Contact phone", example = "15601691000")
    @Size(max = 11, message = "contact phone length must not exceed 11 characters")
    private String phone;

    @Schema(description = "Email", example = "user@example.com")
    @Email(message = "email format is invalid")
    @Size(max = 50, message = "email length must not exceed 50 characters")
    private String email;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "status must not be blank")
    @InEnum(value = CommonStatusEnum.class, message = "update status must be {value}")
    private Integer status;

}
