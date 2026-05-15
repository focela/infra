package com.focela.platform.system.entity.sms;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.focela.platform.system.enums.sms.SmsReceiveStatusEnum;
import com.focela.platform.system.enums.sms.SmsSendStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * SMS log DO
 *
 * @since 2021-01-25
 */
@TableName(value = "system_sms_log", autoResultMap = true)
@KeySequence("system_sms_log_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
public class SmsLogEntity extends BaseEntity {

    /**
     * Auto-increment ID
     */
    private Long id;

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

    // ========= Template-related fields =========

    /**
     * Template ID
     *
     * Associated with {@link SmsTemplateEntity#getId()}
     */
    private Long templateId;
    /**
     * Template code
     *
     * Redundant {@link SmsTemplateEntity#getCode()}
     */
    private String templateCode;
    /**
     * SMS type
     *
     * Redundant {@link SmsTemplateEntity#getType()}
     */
    private Integer templateType;
    /**
     * Content formatted based on {@link SmsTemplateEntity#getContent()}
     */
    private String templateContent;
    /**
     * Parameters provided based on {@link SmsTemplateEntity#getParams()}
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> templateParams;
    /**
     * SMS API template ID
     *
     * Redundant {@link SmsTemplateEntity#getApiTemplateId()}
     */
    private String apiTemplateId;

    // ========= Mobile-related fields =========

    /**
     * Mobile number
     */
    private String mobile;
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

    // ========= Send-related fields =========

    /**
     * Send status
     *
     * Enum {@link SmsSendStatusEnum}
     */
    private Integer sendStatus;
    /**
     * Send time
     */
    private LocalDateTime sendTime;
    /**
     * SMS API send result code
     *
     * Since third-party error codes may be strings, String type is used.
     */
    private String apiSendCode;
    /**
     * SMS API send failure message
     */
    private String apiSendMsg;
    /**
     * Unique request ID returned by the SMS API
     *
     * Used for locating and troubleshooting with the SMS API.
     */
    private String apiRequestId;
    /**
     * Serial number returned by the SMS API
     *
     * Used to associate with send records on the SMS API platform.
     */
    private String apiSerialNo;

    // ========= Receive-related fields =========

    /**
     * Receive status
     *
     * Enum {@link SmsReceiveStatusEnum}
     */
    private Integer receiveStatus;
    /**
     * Receive time
     */
    private LocalDateTime receiveTime;
    /**
     * SMS API receive result code
     */
    private String apiReceiveCode;
    /**
     * SMS API receive result message
     */
    private String apiReceiveMsg;

}
