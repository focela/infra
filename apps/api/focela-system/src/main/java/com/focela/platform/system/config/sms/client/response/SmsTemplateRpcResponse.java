package com.focela.platform.system.config.sms.client.response;

import com.focela.platform.system.config.sms.enums.SmsTemplateAuditStatusEnum;
import lombok.Data;

/**
 * SMS template response
 */
@Data
public class SmsTemplateRpcResponse {

    /**
     * Template ID
     */
    private String id;
    /**
     * SMS content
     */
    private String content;
    /**
     * Audit status
     *
     * Enum {@link SmsTemplateAuditStatusEnum}
     */
    private Integer auditStatus;
    /**
     * Reason for audit rejection
     */
    private String auditReason;

}
