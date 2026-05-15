package com.focela.platform.module.system.enums.oauth2;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OAuth2 grant type (mode) enum
 */
@AllArgsConstructor
@Getter
public enum OAuth2GrantTypeEnum {

    PASSWORD("password"), // password mode
    AUTHORIZATION_CODE("authorization_code"), // authorization code mode
    IMPLICIT("implicit"), // implicit mode
    CLIENT_CREDENTIALS("client_credentials"), // client credentials mode
    REFRESH_TOKEN("refresh_token"), // refresh mode
    ;

    private final String grantType;

    public static OAuth2GrantTypeEnum getByGrantType(String grantType) {
        return ArrayUtil.firstMatch(o -> o.getGrantType().equals(grantType), values());
    }

}
