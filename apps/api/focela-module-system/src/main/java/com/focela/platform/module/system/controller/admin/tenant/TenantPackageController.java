package com.focela.platform.module.system.controller.admin.tenant;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.tenant.dto.packages.TenantPackagePageRequest;
import com.focela.platform.module.system.controller.admin.tenant.dto.packages.TenantPackageResponse;
import com.focela.platform.module.system.controller.admin.tenant.dto.packages.TenantPackageSaveRequest;
import com.focela.platform.module.system.controller.admin.tenant.dto.packages.TenantPackageSimpleResponse;
import com.focela.platform.module.system.repository.entity.tenant.TenantPackageEntity;
import com.focela.platform.module.system.service.tenant.TenantPackageService;
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

@Tag(name = "Admin - Tenant package")
@RestController
@RequestMapping("/system/tenant-package")
@Validated
public class TenantPackageController {

    @Resource
    private TenantPackageService tenantPackageService;

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
    public CommonResult<PageResult<TenantPackageResponse>> getTenantPackagePage(@Valid TenantPackagePageRequest pageVO) {
        PageResult<TenantPackageEntity> pageResult = tenantPackageService.getTenantPackagePage(pageVO);
        return success(BeanUtils.toBean(pageResult, TenantPackageResponse.class));
    }

    @GetMapping({"/get-simple-list", "simple-list"})
    @Operation(summary = "get tenant package simplified info list", description = "only include enabled tenant package, for frontend dropdown options")
    public CommonResult<List<TenantPackageSimpleResponse>> getTenantPackageList() {
        List<TenantPackageEntity> list = tenantPackageService.getTenantPackageListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return success(BeanUtils.toBean(list, TenantPackageSimpleResponse.class));
    }

}
