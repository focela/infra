package com.focela.platform.infra.entity.config;

import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.infra.enums.config.ConfigTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Config table
 */
@TableName("infra_config")
@KeySequence("infra_config_seq") // Used for database primary key auto-increment in Oracle, PostgreSQL, Kingbase, DB2, H2. For databases like MySQL it can be omitted.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TenantIgnore
public class ConfigEntity extends BaseEntity {

    /**
     * Config primary key
     */
    @TableId
    private Long id;
    /**
     * Config category
     */
    private String category;
    /**
     * Config name
     */
    private String name;
    /**
     * Config key
     *
     * When supporting multiple DB types, cannot directly use key + @TableField("config_key") for conversion, because "config_key" AS key causes errors
     */
    private String configKey;
    /**
     * Config value
     */
    private String value;
    /**
     * Config type
     *
     * Enum {@link ConfigTypeEnum}
     */
    private Integer type;
    /**
     * Whether visible
     *
     * Invisible configs are generally sensitive parameters that the front-end cannot retrieve
     */
    private Boolean visible;
    /**
     * Remarks
     */
    private String remark;

}
