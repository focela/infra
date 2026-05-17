package com.focela.platform.system.api.permission;

import com.focela.platform.system.service.permission.RoleService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

/**
 * Role API implementation class
 */
@Service
@RequiredArgsConstructor
public class LocalRoleApi implements RoleApi {

        private final RoleService roleService;

    @Override
    public void validateRoleList(Collection<Long> ids) {
        roleService.validateRoleList(ids);
    }
}
