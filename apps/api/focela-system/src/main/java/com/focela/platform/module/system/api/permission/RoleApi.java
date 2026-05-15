package com.focela.platform.module.system.api.permission;

import java.util.Collection;

/**
 * Role API interface
 */
public interface RoleApi {

    /**
     * Validate whether the roles are valid. The following cases are considered invalid:
     * 1. role ID does not exist
     * 2. role is disabled
     *
     * @param ids role IDs
     */
    void validRoleList(Collection<Long> ids);

}
