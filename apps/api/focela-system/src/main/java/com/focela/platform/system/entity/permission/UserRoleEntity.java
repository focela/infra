package com.focela.platform.system.entity.permission;

import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User-role association
 */
@TableName("system_user_role")
@KeySequence("system_user_role_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
public class UserRoleEntity extends BaseEntity {

    /**
     * Auto-increment primary key
     */
    @TableId
    private Long id;
    /**
     * User ID
     */
    private Long userId;
    /**
     * Role ID
     */
    private Long roleId;

}
