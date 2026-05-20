package com.focela.platform.system.controller.admin.tenant;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.tenant.request.plan.TenantPackagePageRequest;
import com.focela.platform.system.controller.admin.tenant.response.plan.TenantPackageResponse;
import com.focela.platform.system.controller.admin.tenant.request.plan.TenantPackageSaveRequest;
import com.focela.platform.system.controller.admin.tenant.response.plan.TenantPackageSimpleResponse;
import com.focela.platform.system.domain.entity.tenant.TenantPackageEntity;
import com.focela.platform.system.service.tenant.TenantPackageService;
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

@Tag(name = "Admin - Tenant package")
@RestController
@RequestMapping("/system/tenant-package")
@Validated
@RequiredArgsConstructor
public class TenantPackageController {

    private final TenantPackageService tenantPackageService;

    @PostMapping("/create")
    @Operation(summary = "create tenant package")
    @PreAuthorize("@ss.hasPermission('system:tenant-package:create')")
    public CommonResult<Long> createTenantPackage(@Valid @RequestBody TenantPackageSaveRequest createRequest) {
        return success(tenantPackageService.createTenantPackage(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update tenant package")
    @PreAuthorize("@ss.hasPermission('system:tenant-package:update')")
    public CommonResult<Boolean> updateTenantPackage(@Valid @RequestBody TenantPackageSaveRequest updateRequest) {
        tenantPackageService.updateTenantPackage(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete tenant package")
    @Parameter(name = "id", description = "ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:tenant-package:delete')")
    public CommonResult<Boolean> deleteTenantPackage(@RequestParam("id") Long id) {
        tenantPackageService.deleteTenantPackage(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "ID list", required = true)
    @Operation(summary = "batch delete tenant package")
    @PreAuthorize("@ss.hasPermission('system:tenant-package:delete')")
    public CommonResult<Boolean> deleteTenantPackageList(@RequestParam("ids") List<Long> ids) {
        tenantPackageService.deleteTenantPackageList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get tenant package")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant-package:query')")
    public CommonResult<TenantPackageResponse> getTenantPackage(@RequestParam("id") Long id) {
        TenantPackageEntity tenantPackage = tenantPackageService.getTenantPackage(id);
        return success(BeanUtils.toBean(tenantPackage, TenantPackageResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get tenant package page")
    @PreAuthorize("@ss.hasPermission('system:tenant-package:query')")
    public CommonResult<PageResult<TenantPackageResponse>> getTenantPackagePage(@Valid TenantPackagePageRequest pageRequest) {
        PageResult<TenantPackageEntity> pageResult = tenantPackageService.getTenantPackagePage(pageRequest);
        return success(BeanUtils.toBean(pageResult, TenantPackageResponse.class));
    }

    @GetMapping({"/get-simple-list", "/simple-list"})
    @Operation(summary = "get tenant package simplified info list", description = "only include enabled tenant package, for frontend dropdown options")
    public CommonResult<List<TenantPackageSimpleResponse>> getTenantPackageList() {
        List<TenantPackageEntity> tenantPackages = tenantPackageService.getTenantPackageListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return success(BeanUtils.toBean(tenantPackages, TenantPackageSimpleResponse.class));
    }

}
