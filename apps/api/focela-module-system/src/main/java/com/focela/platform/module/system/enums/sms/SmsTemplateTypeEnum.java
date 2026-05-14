package com.focela.platform.module.system.enums.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SMS template type enum
 */
@Getter
@AllArgsConstructor
public enum SmsTemplateTypeEnum {

    VERIFICATION_CODE(1), // verification code
    NOTICE(2), // notice
    PROMOTION(3), // promotion
    ;

    /**
     * Type
     */
    private final int type;

}
