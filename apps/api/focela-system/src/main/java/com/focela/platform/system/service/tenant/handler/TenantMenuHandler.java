package com.focela.platform.system.service.tenant.handler;

import java.util.Set;

/**
 * Tenant menu handler.
 * Purpose: minimize tenant-logic coupling into the system.
 */
public interface TenantMenuHandler {

    /**
     * Execute the relevant logic based on the [full] list of tenant menus passed in.
     * For example, when returning assignable menus, extras can be removed.
     *
     * @param menuIds menu list
     */
    void handle(Set<Long> menuIds);

}
