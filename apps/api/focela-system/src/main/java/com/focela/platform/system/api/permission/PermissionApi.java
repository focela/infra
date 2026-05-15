package com.focela.platform.system.api.permission;

import com.focela.platform.framework.common.api.system.permission.PermissionContractApi;

import java.util.Collection;
import java.util.Set;

/**
 * Permission API interface
 */
public interface PermissionApi extends PermissionContractApi {

    /**
     * Get the set of user IDs that hold any of the given roles
     *
     * @param roleIds role ID set
     * @return user ID set
     */
    Set<Long> getUserRoleIdListByRoleIds(Collection<Long> roleIds);

}
