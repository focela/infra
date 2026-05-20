package com.focela.platform.system.service.tenant;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.tenant.request.plan.TenantPackagePageRequest;
import com.focela.platform.system.controller.admin.tenant.request.plan.TenantPackageSaveRequest;
import com.focela.platform.system.domain.entity.tenant.TenantPackageEntity;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Tenant package Service interface
 */
public interface TenantPackageService {

    /**
     * Create tenant package
     *
     * @param createRequest create info
     * @return ID
     */
    Long createTenantPackage(@Valid TenantPackageSaveRequest createRequest);

    /**
     * Update tenant package
     *
     * @param updateRequest update info
     */
    void updateTenantPackage(@Valid TenantPackageSaveRequest updateRequest);

    /**
     * Delete tenant package
     *
     * @param id ID
     */
    void deleteTenantPackage(Long id);

    /**
     * Batch delete tenant packages
     *
     * @param ids ID array
     */
    void deleteTenantPackageList(List<Long> ids);

    /**
     * Get tenant package
     *
     * @param id ID
     * @return tenant package
     */
    TenantPackageEntity getTenantPackage(Long id);

    /**
     * Get tenant package page
     *
     * @param pageRequest page query
     * @return tenant package page
     */
    PageResult<TenantPackageEntity> getTenantPackagePage(TenantPackagePageRequest pageRequest);

    /**
     * Validate tenant package
     *
     * @param id ID
     * @return tenant package
     */
    TenantPackageEntity validateTenantPackage(Long id);

    /**
     * Get the tenant package list of the specified status
     *
     * @param status status
     * @return tenant packages
     */
    List<TenantPackageEntity> getTenantPackageListByStatus(Integer status);

}
