package com.focela.platform.system.entity.department;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.tenant.core.db.TenantBaseEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Department table
 */
@TableName("system_dept")
@KeySequence("system_dept_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
public class DepartmentEntity extends TenantBaseEntity {

    public static final Long PARENT_ID_ROOT = 0L;

    /**
     * Department ID
     */
    @TableId
    private Long id;
    /**
     * Department name
     */
    private String name;
    /**
     * Parent department ID
     *
     * Associated with {@link #id}
     */
    private Long parentId;
    /**
     * Display order
     */
    private Integer sort;
    /**
     * Leader
     *
     * Associated with {@link UserEntity#getId()}
     */
    private Long leaderUserId;
    /**
     * Contact phone
     */
    private String phone;
    /**
     * Email
     */
    private String email;
    /**
     * Department status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;

}
