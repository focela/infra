package com.focela.platform.module.infra.controller.admin.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Admin - file create Request VO")
@Data
public class FileCreateRequest {

    @NotNull(message = "文件配置编号不能为空")
    @Schema(description = "file config ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long configId;

    @NotNull(message = "文件路径不能为空")
    @Schema(description = "File path", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela.jpg")
    private String path;

    @NotNull(message = "原文件名不能为空")
    @Schema(description = "Original filename", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela.jpg")
    private String name;

    @NotNull(message = "文件 URL不能为空")
    @Schema(description = "File URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.example.com/sample.jpg")
    private String url;

    @Schema(description = "file MIME type", example = "application/octet-stream")
    private String type;

    @Schema(description = "File size", example = "2048", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long size;

}
