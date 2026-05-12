package com.focela.platform.module.system.controller.admin.department.dto.post;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.validation.InEnum;
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
    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 50, message = "岗位名称长度不能超过 50 个字符")
    private String name;

    @Schema(description = "Post code", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 64, message = "岗位编码长度不能超过64个字符")
    private String code;

    @Schema(description = "Display order", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "Status", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

    @Schema(description = "Remarks", example = "happy remarks")
    private String remark;

}