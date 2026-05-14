package com.focela.platform.framework.tenant.core.service;

import java.util.List;

/**
 * Tenant framework Service interface, defining how to obtain tenant information.
 */
public interface TenantFrameworkService {

    /**
     * Get all tenants.
     *
     * @return array of tenant IDs
     */
    List<Long> getTenantIds();

    /**
     * Verify whether the tenant is valid.
     *
     * @param id tenant ID
     */
    void validTenant(Long id);

}
