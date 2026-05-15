package com.focela.platform.module.system.entity.permission;

import com.focela.platform.framework.tenant.core.db.TenantBaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Role-menu association
 */
@TableName("system_role_menu")
@KeySequence("system_role_menu_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleMenuEntity extends TenantBaseEntity {

    /**
     * Auto-increment primary key
     */
    @TableId
    private Long id;
    /**
     * Role ID
     */
    private Long roleId;
    /**
     * Menu ID
     */
    private Long menuId;

}
