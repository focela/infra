package com.focela.platform.infra.controller.admin.file.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Admin - file create Request VO")
@Data
public class FileCreateRequest {

    @NotNull(message = "file config ID must not be blank")
    @Schema(description = "file config ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long configId;

    @NotNull(message = "file path must not be blank")
    @Schema(description = "File path", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela.jpg")
    private String path;

    @NotNull(message = "original filename must not be blank")
    @Schema(description = "Original filename", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela.jpg")
    private String name;

    @NotNull(message = "file URLmust not be blank")
    @Schema(description = "File URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.example.com/sample.jpg")
    private String url;

    @Schema(description = "file MIME type", example = "application/octet-stream")
    private String type;

    @Schema(description = "File size", example = "2048", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long size;

}
