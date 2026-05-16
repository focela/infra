package com.focela.platform.system.api.permission;

import com.focela.platform.system.service.permission.RoleService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Collection;

/**
 * Role API implementation class
 */
@Service
public class LocalRoleApi implements RoleApi {

    @Resource
    private RoleService roleService;

    @Override
    public void validateRoleList(Collection<Long> ids) {
        roleService.validateRoleList(ids);
    }
}
