package com.focela.platform.module.infra.controller.admin.file;

import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.infra.controller.admin.file.dto.config.FileConfigPageRequest;
import com.focela.platform.module.infra.controller.admin.file.dto.config.FileConfigResponse;
import com.focela.platform.module.infra.controller.admin.file.dto.config.FileConfigSaveRequest;
import com.focela.platform.module.infra.repository.entity.file.FileConfigEntity;
import com.focela.platform.module.infra.service.file.FileConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "Admin - File config")
@RestController
@RequestMapping("/infra/file-config")
@Validated
public class FileConfigController {

    @Resource
    private FileConfigService fileConfigService;

    @PostMapping("/create")
    @Operation(summary = "create file config")
    @PreAuthorize("@ss.hasPermission('infra:file-config:create')")
    public CommonResult<Long> createFileConfig(@Valid @RequestBody FileConfigSaveRequest createRequest) {
        return success(fileConfigService.createFileConfig(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update file config")
    @PreAuthorize("@ss.hasPermission('infra:file-config:update')")
    public CommonResult<Boolean> updateFileConfig(@Valid @RequestBody FileConfigSaveRequest updateRequest) {
        fileConfigService.updateFileConfig(updateRequest);
        return success(true);
    }

    @PutMapping("/update-master")
    @Operation(summary = "update file config as Master")
    @PreAuthorize("@ss.hasPermission('infra:file-config:update')")
    public CommonResult<Boolean> updateFileConfigMaster(@RequestParam("id") Long id) {
        fileConfigService.updateFileConfigMaster(id);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete file config")
    @Parameter(name = "id", description = "ID", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file-config:delete')")
    public CommonResult<Boolean> deleteFileConfig(@RequestParam("id") Long id) {
        fileConfigService.deleteFileConfig(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete file config")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file-config:delete')")
    public CommonResult<Boolean> deleteFileConfigList(@RequestParam("ids") List<Long> ids) {
        fileConfigService.deleteFileConfigList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get file config")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:file-config:query')")
    public CommonResult<FileConfigResponse> getFileConfig(@RequestParam("id") Long id) {
        FileConfigEntity config = fileConfigService.getFileConfig(id);
        return success(BeanUtils.toBean(config, FileConfigResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get file config page")
    @PreAuthorize("@ss.hasPermission('infra:file-config:query')")
    public CommonResult<PageResult<FileConfigResponse>> getFileConfigPage(@Valid FileConfigPageRequest pageVO) {
        PageResult<FileConfigEntity> pageResult = fileConfigService.getFileConfigPage(pageVO);
        return success(BeanUtils.toBean(pageResult, FileConfigResponse.class));
    }

    @GetMapping("/test")
    @Operation(summary = "test file config correct")
    @PreAuthorize("@ss.hasPermission('infra:file-config:query')")
    public CommonResult<String> testFileConfig(@RequestParam("id") Long id) throws Exception {
        String url = fileConfigService.testFileConfig(id);
        return success(url);
    }
}
