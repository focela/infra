package com.focela.platform.infra.controller.admin.file.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "Admin - file config create /update Request VO")
@Data
public class FileConfigSaveRequest {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "Config name", requiredMode = Schema.RequiredMode.REQUIRED, example = "S3 - Aliyun")
    @NotNull(message = "config name must not be blank")
    private String name;

    @Schema(description = "Storage, see FileStorageEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "storage must not be blank")
    private Integer storage;

    @Schema(description = "storage config,config is dynamic params, so use Map receive", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "storage config must not be blank")
    private Map<String, Object> config;

    @Schema(description = "Remarks", example = "I am remarks")
    private String remark;

}
