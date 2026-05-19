package com.focela.platform.infra.controller.app.file.request;

import com.focela.platform.infra.controller.admin.file.request.FileUploadRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "User App - upload file Request")
@Data
public class AppFileUploadRequest {

    @Schema(description = "File attachments", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "file attachments must not be blank")
    private MultipartFile file;

    @Schema(description = "File directory", example = "XXX/YYY")
    private String directory;

    @AssertTrue(message = "file directory is invalid")
    @JsonIgnore
    public boolean isDirectoryValid() {
        return FileUploadRequest.isDirectoryValid(directory);
    }

}
