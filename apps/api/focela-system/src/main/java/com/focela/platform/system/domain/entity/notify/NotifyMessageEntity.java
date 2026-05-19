package com.focela.platform.system.domain.entity.notify;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.system.domain.entity.mail.MailTemplateEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * In-site notification message Entity
 */
@TableName(value = "system_notify_message", autoResultMap = true)
@KeySequence("system_notify_message_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyMessageEntity extends BaseEntity {

    /**
     * Notification message ID, auto-increment
     */
    @TableId
    private Long id;
    /**
     * User ID
     *
     * Associated with member user ID or {@link com.focela.platform.system.domain.entity.user.UserEntity#getId()}.
     */
    private Long userId;
    /**
     * User type
     *
     * Enum {@link UserTypeEnum}
     */
    private Integer userType;

    // ========= Template-related fields =========

    /**
     * Template ID
     *
     * Associated with {@link NotifyTemplateEntity#getId()}
     */
    private Long templateId;
    /**
     * Template code
     *
     * Associated with {@link NotifyTemplateEntity#getCode()}
     */
    private String templateCode;
    /**
     * Template type
     *
     * Redundant {@link NotifyTemplateEntity#getType()}
     */
    private Integer templateType;
    /**
     * Template sender nickname
     *
     * Redundant {@link NotifyTemplateEntity#getNickname()}
     */
    private String templateNickname;
    /**
     * Template content
     *
     * Formatted content based on {@link NotifyTemplateEntity#getContent()}
     */
    private String templateContent;
    /**
     * Template parameters
     *
     * Parameters provided based on {@link NotifyTemplateEntity#getParams()}
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> templateParams;

    // ========= Read-related fields =========

    /**
     * Whether read
     */
    private Boolean readStatus;
    /**
     * Read time
     */
    private LocalDateTime readTime;

}
