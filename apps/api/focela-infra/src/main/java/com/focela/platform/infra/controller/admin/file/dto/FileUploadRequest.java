package com.focela.platform.infra.controller.admin.file.dto;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Admin - upload file Request VO")
@Data
public class FileUploadRequest {

    @Schema(description = "File attachments", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "file attachments must not be blank")
    private MultipartFile file;

    @Schema(description = "File directory", example = "XXX/YYY")
    private String directory;

    @AssertTrue(message = "file directory is invalid")
    @JsonIgnore
    public boolean isDirectoryValid() {
        return isDirectoryValid(directory);
    }

    public static boolean isDirectoryValid(String directory) {
        // 1. Must not contain ".." to prevent directory traversal
        // 2. Must not start with "/" or "\" to prevent uploading to the root directory
        return !StrUtil.contains(directory, "..")
                && !StrUtil.startWithAny(directory, "/", "\\");
    }

}
