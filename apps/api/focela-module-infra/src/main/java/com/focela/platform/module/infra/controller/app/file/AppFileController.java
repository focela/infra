package com.focela.platform.module.infra.controller.app.file;

import cn.hutool.core.io.IoUtil;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.module.infra.controller.admin.file.dto.file.FileCreateRequest;
import com.focela.platform.module.infra.controller.admin.file.dto.file.FilePresignedUrlResponse;
import com.focela.platform.module.infra.controller.app.file.dto.AppFileUploadRequest;
import com.focela.platform.module.infra.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "User App - File storage")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class AppFileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "Upload file")
    @Parameter(name = "file", description = "File attachments", required = true,
            schema = @Schema(type = "string", format = "binary"))
    @PermitAll
    public CommonResult<String> uploadFile(AppFileUploadRequest uploadRequest) throws Exception {
        MultipartFile file = uploadRequest.getFile();
        byte[] content = IoUtil.readBytes(file.getInputStream());
        return success(fileService.createFile(content, file.getOriginalFilename(),
                uploadRequest.getDirectory(), file.getContentType()));
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "Get file presigned URL (upload)", description = "Mode 2: frontend direct upload to OSS")
    @Parameters({
            @Parameter(name = "name", description = "File name", required = true),
            @Parameter(name = "directory", description = "File directory")
    })
    public CommonResult<FilePresignedUrlResponse> getFilePresignedUrl(
            @RequestParam("name") String name,
            @RequestParam(value = "directory", required = false) String directory) {
        return success(fileService.presignPutUrl(name, directory));
    }

    @PostMapping("/create")
    @Operation(summary = "Create file", description = "Mode 2: frontend upload via presigned-url")
    @PermitAll
    public CommonResult<Long> createFile(@Valid @RequestBody FileCreateRequest createRequest) {
        return success(fileService.createFile(createRequest));
    }

}
