package com.focela.platform.infra.entity.database;

import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.focela.platform.framework.mybatis.core.type.EncryptTypeHandler;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Data source config
 */
@TableName(value = "infra_data_source_config", autoResultMap = true)
@KeySequence("infra_data_source_config_seq") // Used for database primary key auto-increment in Oracle, PostgreSQL, Kingbase, DB2, H2. For databases like MySQL it can be omitted.
@Data
@TenantIgnore
public class DataSourceConfigEntity extends BaseEntity {

    /**
     * Primary key ID - Master data source
     */
    public static final Long ID_MASTER = 0L;

    /**
     * Primary key ID
     */
    private Long id;
    /**
     * Connection name
     */
    private String name;

    /**
     * Data source URL
     */
    private String url;
    /**
     * Username
     */
    private String username;
    /**
     * Password
     */
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String password;

}
