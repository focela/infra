package com.focela.platform.infra.controller.admin.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - file Response VO,exclude content field, too large")
@Data
public class FileResponse {

    @Schema(description = "file ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "Config ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long configId;

    @Schema(description = "File path", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela.jpg")
    private String path;

    @Schema(description = "Original filename", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela.jpg")
    private String name;

    @Schema(description = "File URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.example.com/sample.jpg")
    private String url;

    @Schema(description = "file MIMEtype", example = "application/octet-stream")
    private String type;

    @Schema(description = "File size", example = "2048", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long size;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
