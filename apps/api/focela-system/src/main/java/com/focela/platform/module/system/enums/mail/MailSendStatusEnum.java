package com.focela.platform.module.system.enums.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Mail send status enum
 *
 * @since 2022/4/10 13:39
 */
@Getter
@AllArgsConstructor
public enum MailSendStatusEnum {

    INIT(0), // initialized
    SUCCESS(10), // send succeeded
    FAILURE(20), // send failed
    IGNORE(30), // ignored, i.e. not sent
    ;

    private final int status;

}
