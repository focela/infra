package com.focela.platform.module.system.enums.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Login result enum
 */
@Getter
@AllArgsConstructor
public enum LoginResultEnum {

    SUCCESS(0), // succeeded
    BAD_CREDENTIALS(10), // incorrect username or password
    USER_DISABLED(20), // user is disabled
    CAPTCHA_NOT_FOUND(30), // captcha does not exist
    CAPTCHA_CODE_ERROR(31), // captcha is incorrect

    ;

    /**
     * Result
     */
    private final Integer result;

}
