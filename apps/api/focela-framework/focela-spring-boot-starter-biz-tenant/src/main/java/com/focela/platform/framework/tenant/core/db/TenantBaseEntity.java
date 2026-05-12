package com.focela.platform.framework.tenant.core.db;

import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拓展多租户的 BaseEntity 基类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TenantBaseEntity extends BaseEntity {

    /**
     * 多租户编号
     */
    private Long tenantId;

}
