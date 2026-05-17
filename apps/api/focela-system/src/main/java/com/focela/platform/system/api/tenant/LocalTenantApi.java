package com.focela.platform.system.api.tenant;

import com.focela.platform.common.api.system.tenant.TenantContractApi;
import com.focela.platform.system.service.tenant.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Multi-tenant API implementation class
 */
@Service
@RequiredArgsConstructor
public class LocalTenantApi implements TenantContractApi {

    private final TenantService tenantService;

    @Override
    public List<Long> getTenantIdList() {
        return tenantService.getTenantIdList();
    }

    @Override
    public void validateTenant(Long id) {
        tenantService.validateTenant(id);
    }

}
