package com.focela.platform.system.service.oauth2;

import com.focela.platform.system.entity.oauth2.OAuth2AccessTokenEntity;

import java.util.List;

/**
 * OAuth2 Grant Service interface
 *
 * Functionally similar to Spring Security OAuth's TokenGranter, provides access token and refresh token operations.
 *
 * Grants the local AdminUser to third-party applications using the OAuth2.0 protocol.
 *
 * Question: Why does the local app also go through this flow as a third-party app?
 * Answer: It is certainly possible — using the password mode. Given that most developers do not use this feature,
 * and OAuth2.0 has a learning curve, this approach has not been adopted for now.
 */
public interface OAuth2GrantService {

    /**
     * Implicit mode
     *
     * Corresponds to Spring Security OAuth2's ImplicitTokenGranter
     *
     * @param userId user ID
     * @param userType user type
     * @param clientId client ID
     * @param scopes authorization scope
     * @return access token
     */
    OAuth2AccessTokenEntity grantImplicit(Long userId, Integer userType,
                                      String clientId, List<String> scopes);

    /**
     * Authorization code mode, stage 1: obtain authorization code
     *
     * Corresponds to Spring Security OAuth2's AuthorizationEndpoint generateCode method
     *
     * @param userId user ID
     * @param userType user type
     * @param clientId client ID
     * @param scopes authorization scope
     * @param redirectUri redirect URI
     * @param state state
     * @return authorization code
     */
    String grantAuthorizationCodeForCode(Long userId, Integer userType,
                                         String clientId, List<String> scopes,
                                         String redirectUri, String state);

    /**
     * Authorization code mode, stage 2: obtain accessToken
     *
     * Corresponds to Spring Security OAuth2's AuthorizationCodeTokenGranter
     *
     * @param clientId client ID
     * @param code authorization code
     * @param redirectUri redirect URI
     * @param state state
     * @return access token
     */
    OAuth2AccessTokenEntity grantAuthorizationCodeForAccessToken(String clientId, String code,
                                                             String redirectUri, String state);

    /**
     * Password mode
     *
     * Corresponds to Spring Security OAuth2's ResourceOwnerPasswordTokenGranter
     *
     * @param username account
     * @param password password
     * @param clientId client ID
     * @param scopes authorization scope
     * @return access token
     */
    OAuth2AccessTokenEntity grantPassword(String username, String password,
                                      String clientId, List<String> scopes);

    /**
     * Refresh mode
     *
     * Corresponds to Spring Security OAuth2's ResourceOwnerPasswordTokenGranter
     *
     * @param refreshToken refresh token
     * @param clientId client ID
     * @return access token
     */
    OAuth2AccessTokenEntity grantRefreshToken(String refreshToken, String clientId);

    /**
     * Client credentials mode
     *
     * Corresponds to Spring Security OAuth2's ClientCredentialsTokenGranter
     *
     * @param clientId client ID
     * @param scopes authorization scope
     * @return access token
     */
    OAuth2AccessTokenEntity grantClientCredentials(String clientId, List<String> scopes);

    /**
     * Revoke an access token
     *
     * Corresponds to Spring Security OAuth2's ConsumerTokenServices revokeToken method
     *
     * @param accessToken access token
     * @param clientId client ID
     * @return whether removed
     */
    boolean revokeToken(String clientId, String accessToken);

}
