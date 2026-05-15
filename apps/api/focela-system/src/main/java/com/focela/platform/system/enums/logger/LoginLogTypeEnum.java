package com.focela.platform.system.enums.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Login log type enum
 */
@Getter
@AllArgsConstructor
public enum LoginLogTypeEnum {

    LOGIN_USERNAME(100), // login with username
    LOGIN_SOCIAL(101), // login via social
    LOGIN_MOBILE(103), // login with mobile
    LOGIN_SMS(104), // login with SMS

    LOGOUT_SELF(200),  // self-initiated logout
    LOGOUT_DELETE(202), // forced logout
    ;

    /**
     * Log type
     */
    private final Integer type;

}
