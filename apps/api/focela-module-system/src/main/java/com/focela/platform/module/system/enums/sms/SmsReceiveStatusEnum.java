package com.focela.platform.module.system.enums.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SMS receive status enum
 *
 * @date 2021/2/1 13:39
 */
@Getter
@AllArgsConstructor
public enum SmsReceiveStatusEnum {

    INIT(0), // initialized
    SUCCESS(10), // receive succeeded
    FAILURE(20), // receive failed
    ;

    private final int status;

}
