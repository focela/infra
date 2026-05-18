package com.focela.platform.system.entity.tenant;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.util.Set;

/**
 * Tenant package Entity
 */
@TableName(value = "system_tenant_package", autoResultMap = true)
@KeySequence("system_tenant_package_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
public class TenantPackageEntity extends BaseEntity {

    /**
     * Package ID, auto-increment
     */
    private Long id;
    /**
     * Package name; must be unique
     */
    private String name;
    /**
     * Tenant package status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * Remarks
     */
    private String remark;
    /**
     * Associated menu IDs
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<Long> menuIds;

}
