package com.focela.platform.infra.controller.admin.database;

import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.infra.controller.admin.database.dto.DataSourceConfigResponse;
import com.focela.platform.infra.controller.admin.database.dto.DataSourceConfigSaveRequest;
import com.focela.platform.infra.entity.database.DataSourceConfigEntity;
import com.focela.platform.infra.service.database.DataSourceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - Datasource config")
@RestController
@RequestMapping("/infra/data-source-config")
@Validated
@RequiredArgsConstructor
public class DataSourceConfigController {

    private final DataSourceConfigService dataSourceConfigService;

    @PostMapping("/create")
    @Operation(summary = "create datasource config")
    @PreAuthorize("@ss.hasPermission('infra:data-source-config:create')")
    public CommonResult<Long> createDataSourceConfig(@Valid @RequestBody DataSourceConfigSaveRequest createRequest) {
        return success(dataSourceConfigService.createDataSourceConfig(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update datasource config")
    @PreAuthorize("@ss.hasPermission('infra:data-source-config:update')")
    public CommonResult<Boolean> updateDataSourceConfig(@Valid @RequestBody DataSourceConfigSaveRequest updateRequest) {
        dataSourceConfigService.updateDataSourceConfig(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete datasource config")
    @Parameter(name = "id", description = "ID", required = true)
    @PreAuthorize("@ss.hasPermission('infra:data-source-config:delete')")
    public CommonResult<Boolean> deleteDataSourceConfig(@RequestParam("id") Long id) {
        dataSourceConfigService.deleteDataSourceConfig(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete datasource config")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('infra:data-source-config:delete')")
    public CommonResult<Boolean> deleteDataSourceConfigList(@RequestParam("ids") List<Long> ids) {
        dataSourceConfigService.deleteDataSourceConfigList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get datasource config")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:data-source-config:query')")
    public CommonResult<DataSourceConfigResponse> getDataSourceConfig(@RequestParam("id") Long id) {
        DataSourceConfigEntity config = dataSourceConfigService.getDataSourceConfig(id);
        return success(BeanUtils.toBean(config, DataSourceConfigResponse.class));
    }

    @GetMapping("/list")
    @Operation(summary = "get datasource config list")
    @PreAuthorize("@ss.hasPermission('infra:data-source-config:query')")
    public CommonResult<List<DataSourceConfigResponse>> getDataSourceConfigList() {
        List<DataSourceConfigEntity> dataSourceConfigs = dataSourceConfigService.getDataSourceConfigList();
        return success(BeanUtils.toBean(dataSourceConfigs, DataSourceConfigResponse.class));
    }

}
