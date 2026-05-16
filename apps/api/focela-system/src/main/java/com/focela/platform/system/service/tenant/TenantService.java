package com.focela.platform.system.service.tenant;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import com.focela.platform.system.controller.admin.tenant.dto.TenantPageRequest;
import com.focela.platform.system.controller.admin.tenant.dto.TenantSaveRequest;
import com.focela.platform.system.entity.tenant.TenantEntity;
import com.focela.platform.system.service.tenant.handler.TenantInfoHandler;
import com.focela.platform.system.service.tenant.handler.TenantMenuHandler;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Set;

/**
 * Tenant Service interface
 */
public interface TenantService {

    /**
     * Create tenant
     *
     * @param createRequest create info
     * @return ID
     */
    Long createTenant(@Valid TenantSaveRequest createRequest);

    /**
     * Update tenant
     *
     * @param updateRequest update info
     */
    void updateTenant(@Valid TenantSaveRequest updateRequest);

    /**
     * Update tenant role menus
     *
     * @param tenantId tenant ID
     * @param menuIds  menu ID array
     */
    void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds);

    /**
     * Delete tenant
     *
     * @param id ID
     */
    void deleteTenant(Long id);

    /**
     * Batch delete tenants
     *
     * @param ids ID array
     */
    void deleteTenantList(List<Long> ids);

    /**
     * Get tenant
     *
     * @param id ID
     * @return tenant
     */
    TenantEntity getTenant(Long id);

    /**
     * Get tenant page
     *
     * @param pageRequest page query
     * @return tenant page
     */
    PageResult<TenantEntity> getTenantPage(TenantPageRequest pageRequest);

    /**
     * Get tenant by name
     *
     * @param name tenant name
     * @return tenant
     */
    TenantEntity getTenantByName(String name);

    /**
     * Get tenant by website
     *
     * @param website website domain
     * @return tenant
     */
    TenantEntity getTenantByWebsite(String website);

    /**
     * Get the number of tenants using the specified package
     *
     * @param packageId tenant package ID
     * @return tenant count
     */
    Long getTenantCountByPackageId(Long packageId);

    /**
     * Get the list of tenants using the specified package
     *
     * @param packageId tenant package ID
     * @return tenant array
     */
    List<TenantEntity> getTenantListByPackageId(Long packageId);

    /**
     * Get the tenant list of the specified status
     *
     * @param status status
     * @return tenant list
     */
    List<TenantEntity> getTenantListByStatus(Integer status);

    /**
     * Run tenant info handling logic.
     * The tenant ID is obtained from the {@link TenantContextHolder} context.
     *
     * @param handler handler
     */
    void handleTenantInfo(TenantInfoHandler handler);

    /**
     * Run tenant menu handling logic.
     * The tenant ID is obtained from the {@link TenantContextHolder} context.
     *
     * @param handler handler
     */
    void handleTenantMenu(TenantMenuHandler handler);

    /**
     * Get all tenants
     *
     * @return tenant ID array
     */
    List<Long> getTenantIdList();

    /**
     * Validate the tenant
     *
     * @param id tenant ID
     */
    void validTenant(Long id);

}
