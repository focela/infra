package com.focela.platform.infra.controller.admin.config;

import com.focela.platform.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageParam;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.excel.core.utils.ExcelUtils;
import com.focela.platform.infra.controller.admin.config.dto.ConfigPageRequest;
import com.focela.platform.infra.controller.admin.config.dto.ConfigResponse;
import com.focela.platform.infra.controller.admin.config.dto.ConfigSaveRequest;
import com.focela.platform.infra.converter.config.ConfigConverter;
import com.focela.platform.infra.entity.config.ConfigEntity;
import com.focela.platform.infra.constants.InfraErrorCodeConstants;
import com.focela.platform.infra.service.config.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.focela.platform.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - System config")
@RestController
@RequestMapping("/infra/config")
@Validated
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @PostMapping("/create")
    @Operation(summary = "create param config")
    @PreAuthorize("@ss.hasPermission('infra:config:create')")
    public CommonResult<Long> createConfig(@Valid @RequestBody ConfigSaveRequest createRequest) {
        return success(configService.createConfig(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update param config")
    @PreAuthorize("@ss.hasPermission('infra:config:update')")
    public CommonResult<Boolean> updateConfig(@Valid @RequestBody ConfigSaveRequest updateRequest) {
        configService.updateConfig(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete param config")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:config:delete')")
    public CommonResult<Boolean> deleteConfig(@RequestParam("id") Long id) {
        configService.deleteConfig(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete param config")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('infra:config:delete')")
    public CommonResult<Boolean> deleteConfigList(@RequestParam("ids") List<Long> ids) {
        configService.deleteConfigList(ids);
        return success(true);
    }

    @GetMapping(value = "/get")
    @Operation(summary = "get param config")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:config:query')")
    public CommonResult<ConfigResponse> getConfig(@RequestParam("id") Long id) {
        return success(ConfigConverter.INSTANCE.convert(configService.getConfig(id)));
    }

    @GetMapping(value = "/get-value-by-key")
    @Operation(summary = "by param key query param value", description = "hidden config, not returned to frontend")
    @Parameter(name = "key", description = "param key", required = true, example = "yunai.biz.username")
    public CommonResult<String> getConfigKey(@RequestParam("key") String key) {
        ConfigEntity config = configService.getConfigByKey(key);
        if (config == null) {
            return success(null);
        }
        if (!config.getVisible()) {
            throw exception(InfraErrorCodeConstants.CONFIG_GET_VALUE_ERROR_IF_VISIBLE);
        }
        return success(config.getValue());
    }

    @GetMapping("/page")
    @Operation(summary = "get param config page")
    @PreAuthorize("@ss.hasPermission('infra:config:query')")
    public CommonResult<PageResult<ConfigResponse>> getConfigPage(@Valid ConfigPageRequest pageRequest) {
        PageResult<ConfigEntity> page = configService.getConfigPage(pageRequest);
        return success(ConfigConverter.INSTANCE.convertPage(page));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "export param config")
    @PreAuthorize("@ss.hasPermission('infra:config:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportConfig(ConfigPageRequest exportRequest,
                             HttpServletResponse response) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ConfigEntity> list = configService.getConfigPage(exportRequest).getList();
        // Output
        ExcelUtils.write(response, "Param config.xls", "Data", ConfigResponse.class,
                ConfigConverter.INSTANCE.convertList(list));
    }

}
