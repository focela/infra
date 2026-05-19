package com.focela.platform.system.controller.admin.department.request.post;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Admin - post create /update Request VO")
@Data
public class PostSaveRequest {

    @Schema(description = "Post ID", example = "1024")
    private Long id;

    @Schema(description = "Post name", requiredMode = Schema.RequiredMode.REQUIRED, example = "little potato")
    @NotBlank(message = "post name must not be blank")
    @Size(max = 50, message = "post name length must not exceed 50 characters")
    private String name;

    @Schema(description = "Post code", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela")
    @NotBlank(message = "post code must not be blank")
    @Size(max = 64, message = "post code length must not exceed 64characters")
    private String code;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "display order must not be blank")
    private Integer sort;

    @Schema(description = "Status", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

    @Schema(description = "Remarks", example = "happy remarks")
    private String remark;

}