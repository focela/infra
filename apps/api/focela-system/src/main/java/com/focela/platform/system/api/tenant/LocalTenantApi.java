package com.focela.platform.system.api.tenant;

import com.focela.platform.common.api.system.tenant.TenantContractApi;
import com.focela.platform.system.service.tenant.TenantService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * Multi-tenant API implementation class
 */
@Service
public class LocalTenantApi implements TenantContractApi {

    @Resource
    private TenantService tenantService;

    @Override
    public List<Long> getTenantIdList() {
        return tenantService.getTenantIdList();
    }

    @Override
    public void validateTenant(Long id) {
        tenantService.validTenant(id);
    }

}
