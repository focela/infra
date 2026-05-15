package com.focela.platform.system.entity.mail;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.mybatis.core.type.StringListTypeHandler;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.system.enums.mail.MailSendStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Mail log DO
 * Records each mail send
 *
 * @since 2022-03-21
 */
@TableName(value = "system_mail_log", autoResultMap = true)
@KeySequence("system_mail_log_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
public class MailLogEntity extends BaseEntity implements Serializable {

    /**
     * Log ID, auto-increment
     */
    private Long id;

    /**
     * User ID
     */
    private Long userId;
    /**
     * User type
     *
     * Enum {@link UserTypeEnum}
     */
    private Integer userType;

    /**
     * Recipient email addresses
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> toMails;
    /**
     * CC email addresses
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> ccMails;
    /**
     * BCC email addresses
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> bccMails;

    /**
     * Mail account ID
     *
     * Associated with {@link MailAccountEntity#getId()}
     */
    private Long accountId;
    /**
     * Sender email address
     *
     * Redundant {@link MailAccountEntity#getMail()}
     */
    private String fromMail;

    // ========= Template-related fields =========
    /**
     * Template ID
     *
     * Associated with {@link MailTemplateEntity#getId()}
     */
    private Long templateId;
    /**
     * Template code
     *
     * Redundant {@link MailTemplateEntity#getCode()}
     */
    private String templateCode;
    /**
     * Template sender nickname
     *
     * Redundant {@link MailTemplateEntity#getNickname()}
     */
    private String templateNickname;
    /**
     * Template title
     */
    private String templateTitle;
    /**
     * Template content
     *
     * Formatted content based on {@link MailTemplateEntity#getContent()}
     */
    private String templateContent;
    /**
     * Template parameters
     *
     * Parameters provided based on {@link MailTemplateEntity#getParams()}
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> templateParams;

    // ========= Sending-related fields =========
    /**
     * Send status
     *
     * Enum {@link MailSendStatusEnum}
     */
    private Integer sendStatus;
    /**
     * Send time
     */
    private LocalDateTime sendTime;
    /**
     * Message ID returned after sending
     */
    private String sendMessageId;
    /**
     * Send exception
     */
    private String sendException;

}
