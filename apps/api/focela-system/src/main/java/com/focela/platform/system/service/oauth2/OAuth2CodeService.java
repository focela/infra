package com.focela.platform.system.service.oauth2;

import com.focela.platform.system.entity.oauth2.OAuth2CodeEntity;

import java.util.List;

/**
 * OAuth2.0 Authorization Code Service interface
 *
 * Functionally similar to Spring Security OAuth's JdbcAuthorizationCodeServices, provides authorization code operations
 */
public interface OAuth2CodeService {

    /**
     * Create an authorization code
     *
     * Refer to the createAuthorizationCode method in JdbcAuthorizationCodeServices
     *
     * @param userId user ID
     * @param userType user type
     * @param clientId client ID
     * @param scopes authorization scope
     * @param redirectUri redirect URI
     * @param state state
     * @return authorization code information
     */
    OAuth2CodeEntity createAuthorizationCode(Long userId, Integer userType, String clientId,
                                         List<String> scopes, String redirectUri, String state);

    /**
     * Consume an authorization code
     *
     * @param code authorization code
     */
    OAuth2CodeEntity consumeAuthorizationCode(String code);

}
