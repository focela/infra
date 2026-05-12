package com.focela.platform.module.infra.controller.app.file.dto;

import com.focela.platform.module.infra.controller.admin.file.dto.file.FileUploadRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "User App - upload file Request VO")
@Data
public class AppFileUploadRequest {

    @Schema(description = "File attachments", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件附件不能为空")
    private MultipartFile file;

    @Schema(description = "File directory", example = "XXX/YYY")
    private String directory;

    @AssertTrue(message = "文件目录不正确")
    @JsonIgnore
    public boolean isDirectoryValid() {
        return FileUploadRequest.isDirectoryValid(directory);
    }

}
