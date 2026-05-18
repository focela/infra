package com.focela.platform.system.entity.permission;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.tenant.core.db.TenantBaseEntity;
import com.focela.platform.system.enums.permission.DataScopeEnum;
import com.focela.platform.system.enums.permission.RoleTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * Role Entity
 */
@TableName(value = "system_role", autoResultMap = true)
@KeySequence("system_role_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleEntity extends TenantBaseEntity {

    /**
     * Role ID
     */
    @TableId
    private Long id;
    /**
     * Role name
     */
    private String name;
    /**
     * Role code
     *
     * Enum
     */
    private String code;
    /**
     * Role sort order
     */
    private Integer sort;
    /**
     * Role status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * Role type
     *
     * Enum {@link RoleTypeEnum}
     */
    private Integer type;
    /**
     * Remarks
     */
    private String remark;

    /**
     * Data scope
     *
     * Enum {@link DataScopeEnum}
     */
    private Integer dataScope;
    /**
     * Data scope (custom department ID array)
     *
     * Applies when {@link #dataScope} is {@link DataScopeEnum#DEPT_CUSTOM}.
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<Long> dataScopeDeptIds;

}
