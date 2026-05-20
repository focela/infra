package com.focela.platform.infra.controller.admin.config.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Admin - param config create/update Request")
@Data
public class ConfigSaveRequest {

    @Schema(description = "Param config order", example = "1024")
    private Long id;

    @Schema(description = "param group", requiredMode = Schema.RequiredMode.REQUIRED, example = "biz")
    @NotEmpty(message = "param group must not be blank")
    @Size(max = 50, message = "param category must not exceed 50 characters")
    private String category;

    @Schema(description = "Param name", requiredMode = Schema.RequiredMode.REQUIRED, example = "database name")
    @NotBlank(message = "param name must not be blank")
    @Size(max = 100, message = "param name must not exceed 100 characters")
    private String name;

    @Schema(description = "Param key", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela.db.username")
    @NotBlank(message = "param key length must not be blank")
    @Size(max = 100, message = "param key length must not exceed 100 characters")
    private String key;

    @Schema(description = "Param value", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotBlank(message = "param value must not be blank")
    @Size(max = 500, message = "param value length must not exceed 500 characters")
    private String value;

    @Schema(description = "Visible", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "visible must not be blank")
    private Boolean visible;

    @Schema(description = "Remarks", example = "remarks one next very cool!")
    private String remark;

}
