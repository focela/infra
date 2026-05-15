package com.focela.platform.tenant.core.db;

import com.focela.platform.mybatis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * BaseEntity extension that supports multi-tenancy
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TenantBaseEntity extends BaseEntity {

    /**
     * Tenant ID
     */
    private Long tenantId;

}
