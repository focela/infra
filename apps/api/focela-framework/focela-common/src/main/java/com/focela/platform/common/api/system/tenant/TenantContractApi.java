package com.focela.platform.common.api.system.tenant;

import java.util.List;

/**
 * Multi-tenant API interface.
 */
public interface TenantContractApi {

    /**
     * Get all tenants.
     *
     * @return tenant ID array
     */
    List<Long> getTenantIdList();

    /**
     * Validate whether a tenant is legitimate.
     *
     * @param id tenant ID
     */
    void validateTenant(Long id);

}
