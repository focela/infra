package com.focela.platform.module.infra.controller.admin.config.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Admin - param config create /update Request VO")
@Data
public class ConfigSaveRequest {

    @Schema(description = "Param config order", example = "1024")
    private Long id;

    @Schema(description = "param group", requiredMode = Schema.RequiredMode.REQUIRED, example = "biz")
    @NotEmpty(message = "参数分组不能为空")
    @Size(max = 50, message = "参数名称不能超过 50 个字符")
    private String category;

    @Schema(description = "Param name", requiredMode = Schema.RequiredMode.REQUIRED, example = "database name")
    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称不能超过 100 个字符")
    private String name;

    @Schema(description = "Param key", requiredMode = Schema.RequiredMode.REQUIRED, example = "yunai.db.username")
    @NotBlank(message = "参数键名长度不能为空")
    @Size(max = 100, message = "参数键名长度不能超过 100 个字符")
    private String key;

    @Schema(description = "Param value", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值长度不能超过 500 个字符")
    private String value;

    @Schema(description = "Visible", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否可见不能为空")
    private Boolean visible;

    @Schema(description = "Remarks", example = "remarks one next very cool!")
    private String remark;

}
