package com.focela.platform.system.config.sms.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SMS template audit status enum
 */
@AllArgsConstructor
@Getter
public enum SmsTemplateAuditStatusEnum {

    CHECKING(1),
    SUCCESS(2),
    FAIL(3);

    private final Integer status;

}
