package com.focela.platform.module.system.entity.sms;

import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.focela.platform.module.system.enums.sms.SmsTemplateTypeEnum;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * SMS template DO
 *
 * @since 2021-01-25
 */
@TableName(value = "system_sms_template", autoResultMap = true)
@KeySequence("system_sms_template_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TenantIgnore
public class SmsTemplateEntity extends BaseEntity {

    /**
     * Auto-increment ID
     */
    private Long id;

    // ========= Template-related fields =========

    /**
     * SMS type
     *
     * Enum {@link SmsTemplateTypeEnum}
     */
    private Integer type;
    /**
     * Enabled status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * Template code; must be unique
     */
    private String code;
    /**
     * Template name
     */
    private String name;
    /**
     * Template content
     *
     * Parameters in the content are wrapped with {}, e.g. {name}.
     */
    private String content;
    /**
     * Parameter array (auto-generated from content)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> params;
    /**
     * Remarks
     */
    private String remark;
    /**
     * SMS API template ID
     */
    private String apiTemplateId;

    // ========= Channel-related fields =========

    /**
     * SMS channel ID
     *
     * Associated with {@link SmsChannelEntity#getId()}
     */
    private Long channelId;
    /**
     * SMS channel code
     *
     * Redundant {@link SmsChannelEntity#getCode()}
     */
    private String channelCode;

}
