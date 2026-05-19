package com.focela.platform.system.domain.entity.tenant;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.mybatis.core.type.StringListTypeHandler;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.system.domain.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tenant Entity
 */
@TableName(value = "system_tenant", autoResultMap = true)
@KeySequence("system_tenant_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
public class TenantEntity extends BaseEntity {

    /**
     * Package ID - system
     */
    public static final Long PACKAGE_ID_SYSTEM = 0L;

    /**
     * Tenant ID, auto-increment
     */
    private Long id;
    /**
     * Tenant name; must be unique
     */
    private String name;
    /**
     * Contact user ID
     *
     * Associated with {@link UserEntity#getId()}
     */
    private Long contactUserId;
    /**
     * Contact name
     */
    private String contactName;
    /**
     * Contact mobile
     */
    private String contactMobile;
    /**
     * Tenant status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * Bound domain list
     *
     * 1. To be compatible with WeChat mini-programs, appid is also allowed.
     * 2. Why an array? The admin and member frontends may have separate domains, or there may be multiple admin backends.
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> websites;
    /**
     * Tenant package ID
     *
     * Associated with {@link TenantPackageEntity#getId()}.
     * Special logic: built-in system tenants do not use a package; {@link #PACKAGE_ID_SYSTEM} is used as a marker for now.
     */
    private Long packageId;
    /**
     * Expiration time
     */
    private LocalDateTime expireTime;
    /**
     * Account count
     */
    private Integer accountCount;

}
