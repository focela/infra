package com.focela.platform.system.service.tenant.handler;

import com.focela.platform.system.domain.entity.tenant.TenantEntity;

/**
 * Tenant info handler.
 * Purpose: minimize tenant-logic coupling into the system.
 */
public interface TenantInfoHandler {

    /**
     * Execute the relevant logic based on the tenant info passed in.
     * For example, exceeding the max account quota when creating a user.
     *
     * @param tenant tenant info
     */
    void handle(TenantEntity tenant);

}
