package com.focela.platform.module.system.controller.admin.tenant;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.focela.platform.module.system.controller.admin.tenant.dto.tenant.TenantPageRequest;
import com.focela.platform.module.system.controller.admin.tenant.dto.tenant.TenantResponse;
import com.focela.platform.module.system.controller.admin.tenant.dto.tenant.TenantSaveRequest;
import com.focela.platform.module.system.entity.tenant.TenantEntity;
import com.focela.platform.module.system.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.focela.platform.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.framework.common.model.CommonResult.success;
import static com.focela.platform.framework.common.utils.collection.CollectionUtils.convertList;

@Tag(name = "Admin - Tenant")
@RestController
@RequestMapping("/system/tenant")
@Validated
public class TenantController {

    @Resource
    private TenantService tenantService;

    @GetMapping("/get-id-by-name")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "use tenant name, get tenant ID", description = "login page, by user tenant name, get tenant ID")
    @Parameter(name = "name", description = "Tenant name", required = true, example = "1024")
    public CommonResult<Long> getTenantIdByName(@RequestParam("name") String name) {
        TenantEntity tenant = tenantService.getTenantByName(name);
        return success(tenant != null ? tenant.getId() : null);
    }

    @GetMapping({ "simple-list" })
    @PermitAll
    @TenantIgnore
    @Operation(summary = "get tenant simplified info list", description = "only include enabled tenant, for [home]feature select tenant options")
    public CommonResult<List<TenantResponse>> getTenantSimpleList() {
        List<TenantEntity> list = tenantService.getTenantListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return success(convertList(list, tenantDO ->
                new TenantResponse().setId(tenantDO.getId()).setName(tenantDO.getName())));
    }

    @GetMapping("/get-by-website")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "Get tenant info by domain", description = "login page, by user domain, get tenant info")
    @Parameter(name = "website", description = "Domain", required = true, example = "www.example.com")
    public CommonResult<TenantResponse> getTenantByWebsite(
            @RequestParam("website") @Pattern(regexp = "^[a-zA-Z0-9.-]+$", message = "website domain format is invalid") String website) {
        TenantEntity tenant = tenantService.getTenantByWebsite(website);
        if (tenant == null || CommonStatusEnum.isDisable(tenant.getStatus())) {
            return success(null);
        }
        return success(new TenantResponse().setId(tenant.getId()).setName(tenant.getName()));
    }

    @PostMapping("/create")
    @Operation(summary = "create tenant")
    @PreAuthorize("@ss.hasPermission('system:tenant:create')")
    public CommonResult<Long> createTenant(@Valid @RequestBody TenantSaveRequest createRequest) {
        return success(tenantService.createTenant(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update tenant")
    @PreAuthorize("@ss.hasPermission('system:tenant:update')")
    public CommonResult<Boolean> updateTenant(@Valid @RequestBody TenantSaveRequest updateRequest) {
        tenantService.updateTenant(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete tenant")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:delete')")
    public CommonResult<Boolean> deleteTenant(@RequestParam("id") Long id) {
        tenantService.deleteTenant(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "ID list", required = true)
    @Operation(summary = "batch delete tenant")
    @PreAuthorize("@ss.hasPermission('system:tenant:delete')")
    public CommonResult<Boolean> deleteTenantList(@RequestParam("ids") List<Long> ids) {
        tenantService.deleteTenantList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get tenant")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<TenantResponse> getTenant(@RequestParam("id") Long id) {
        TenantEntity tenant = tenantService.getTenant(id);
        return success(BeanUtils.toBean(tenant, TenantResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get tenant page")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<PageResult<TenantResponse>> getTenantPage(@Valid TenantPageRequest pageVO) {
        PageResult<TenantEntity> pageResult = tenantService.getTenantPage(pageVO);
        return success(BeanUtils.toBean(pageResult, TenantResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "export tenant Excel")
    @PreAuthorize("@ss.hasPermission('system:tenant:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTenantExcel(@Valid TenantPageRequest exportRequest, HttpServletResponse response) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TenantEntity> list = tenantService.getTenantPage(exportRequest).getList();
        // 导出 Excel
        ExcelUtils.write(response, "租户.xls", "数据", TenantResponse.class,
                BeanUtils.toBean(list, TenantResponse.class));
    }

}
