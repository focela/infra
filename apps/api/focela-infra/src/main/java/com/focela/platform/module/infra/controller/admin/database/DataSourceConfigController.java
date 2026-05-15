package com.focela.platform.module.infra.controller.admin.database;

import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.infra.controller.admin.database.dto.DataSourceConfigResponse;
import com.focela.platform.module.infra.controller.admin.database.dto.DataSourceConfigSaveRequest;
import com.focela.platform.module.infra.entity.database.DataSourceConfigEntity;
import com.focela.platform.module.infra.service.database.DataSourceConfigService;
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

@Tag(name = "Admin - Datasource config")
@RestController
@RequestMapping("/infra/data-source-config")
@Validated
public class DataSourceConfigController {

    @Resource
    private DataSourceConfigService dataSourceConfigService;

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
        List<DataSourceConfigEntity> list = dataSourceConfigService.getDataSourceConfigList();
        return success(BeanUtils.toBean(list, DataSourceConfigResponse.class));
    }

}
