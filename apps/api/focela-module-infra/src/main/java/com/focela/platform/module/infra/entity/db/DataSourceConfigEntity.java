package com.focela.platform.module.infra.entity.db;

import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.focela.platform.framework.mybatis.core.type.EncryptTypeHandler;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 数据源配置
 */
@TableName(value = "infra_data_source_config", autoResultMap = true)
@KeySequence("infra_data_source_config_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@TenantIgnore
public class DataSourceConfigEntity extends BaseEntity {

    /**
     * 主键编号 - Master 数据源
     */
    public static final Long ID_MASTER = 0L;

    /**
     * 主键编号
     */
    private Long id;
    /**
     * 连接名
     */
    private String name;

    /**
     * 数据源连接
     */
    private String url;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String password;

}
