package com.focela.platform.infra.controller.admin.file;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.focela.platform.infra.controller.admin.file.dto.*;
import com.focela.platform.infra.entity.file.FileEntity;
import com.focela.platform.infra.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.focela.platform.framework.common.model.CommonResult.success;
import static com.focela.platform.infra.config.file.core.utils.FileTypeUtils.writeAttachment;

@Tag(name = "Admin - File storage")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "Upload file", description = "Mode 1: backend upload")
    @Parameter(name = "file", description = "File attachments", required = true,
            schema = @Schema(type = "string", format = "binary"))
    public CommonResult<String> uploadFile(@Valid FileUploadRequest uploadRequest) throws Exception {
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
    public CommonResult<Long> createFile(@Valid @RequestBody FileCreateRequest createRequest) {
        return success(fileService.createFile(createRequest));
    }

    @GetMapping("/get")
    @Operation(summary = "get file")
    @Parameter(name = "id", description = "ID", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public CommonResult<FileResponse> getFile(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(fileService.getFile(id), FileResponse.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete file")
    @Parameter(name = "id", description = "ID", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public CommonResult<Boolean> deleteFile(@RequestParam("id") Long id) throws Exception {
        fileService.deleteFile(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete file")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public CommonResult<Boolean> deleteFileList(@RequestParam("ids") List<Long> ids) throws Exception {
        fileService.deleteFileList(ids);
        return success(true);
    }

    @GetMapping("/{configId}/get/**")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "download file")
    @Parameter(name = "configId", description = "Config ID", required = true)
    public void getFileContent(HttpServletRequest request,
                               HttpServletResponse response,
                               @PathVariable("configId") Long configId) throws Exception {
        // 获取请求的路径
        String path = StrUtil.subAfter(request.getRequestURI(), "/get/", false);
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("结尾 path path is required");
        }
        // 解码，解决中文路径的问题
        // https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/807/
        // https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/1432/
        path = URLUtil.decode(path, StandardCharsets.UTF_8, false);

        // 读取内容
        byte[] content = fileService.getFileContent(configId, path);
        if (content == null) {
            log.warn("[getFileContent][configId({}) path({}) file does not exist]", configId, path);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        writeAttachment(response, path, content);
    }

    @GetMapping("/page")
    @Operation(summary = "get file page")
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public CommonResult<PageResult<FileResponse>> getFilePage(@Valid FilePageRequest pageVO) {
        PageResult<FileEntity> pageResult = fileService.getFilePage(pageVO);
        return success(BeanUtils.toBean(pageResult, FileResponse.class));
    }

}
