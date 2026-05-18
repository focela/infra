package com.focela.platform.system.entity.mail;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Mail template Entity
 *
 * @since 2022-03-21
 */
@TableName(value = "system_mail_template", autoResultMap = true)
@KeySequence("system_mail_template_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@TenantIgnore
public class MailTemplateEntity extends BaseEntity {

    /**
     * Primary key
     */
    private Long id;
    /**
     * Template name
     */
    private String name;
    /**
     * Template code
     */
    private String code;
    /**
     * Sender mail account ID
     *
     * Associated with {@link MailAccountEntity#getId()}
     */
    private Long accountId;

    /**
     * Sender nickname
     */
    private String nickname;
    /**
     * Title
     */
    private String title;
    /**
     * Content
     */
    private String content;
    /**
     * Parameter array (auto-generated from content)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> params;
    /**
     * Status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * Remarks
     */
    private String remark;

}
