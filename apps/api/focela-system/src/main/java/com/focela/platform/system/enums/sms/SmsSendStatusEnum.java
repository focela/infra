package com.focela.platform.system.enums.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SMS send status enum
 *
 * @date 2021/2/1 13:39
 */
@Getter
@AllArgsConstructor
public enum SmsSendStatusEnum {

    INIT(0), // initialized
    SUCCESS(10), // send succeeded
    FAILURE(20), // send failed
    IGNORE(30), // ignored, i.e. not sent
    ;

    private final int status;

}
