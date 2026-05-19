package com.focela.platform.system.service.oauth2;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.oauth2.dto.token.OAuth2AccessTokenPageRequest;
import com.focela.platform.system.domain.entity.oauth2.OAuth2AccessTokenEntity;

import java.util.List;

/**
 * OAuth2.0 Token Service interface
 *
 * Functionally similar to Spring Security OAuth's DefaultTokenServices + JdbcTokenStore, provides access token and refresh token operations
 */
public interface OAuth2TokenService {

    /**
     * Create an access token
     * Note: this flow also includes creating a refresh token
     *
     * Refer to the createAccessToken method in DefaultTokenServices
     *
     * @param userId user ID
     * @param userType user type
     * @param clientId client ID
     * @param scopes authorization scope
     * @return access token information
     */
    OAuth2AccessTokenEntity createAccessToken(Long userId, Integer userType, String clientId, List<String> scopes);

    /**
     * Refresh the access token
     *
     * Refer to the refreshAccessToken method in DefaultTokenServices
     *
     * @param refreshToken refresh token
     * @param clientId client ID
     * @return access token information
     */
    OAuth2AccessTokenEntity refreshAccessToken(String refreshToken, String clientId);

    /**
     * Get the access token
     *
     * Refer to the getAccessToken method in DefaultTokenServices
     *
     * @param accessToken access token
     * @return access token information
     */
    OAuth2AccessTokenEntity getAccessToken(String accessToken);

    /**
     * Validate the access token
     *
     * @param accessToken access token
     * @return access token information
     */
    OAuth2AccessTokenEntity checkAccessToken(String accessToken);

    /**
     * Remove the access token
     * Note: this flow also removes the related refresh token
     *
     * Refer to the revokeToken method in DefaultTokenServices
     *
     * @param accessToken refresh token
     * @return access token information
     */
    OAuth2AccessTokenEntity removeAccessToken(String accessToken);

    /**
     * Remove the access token
     * Note: this flow also removes the related refresh token
     *
     * Refer to the revokeToken method in DefaultTokenServices
     *
     * @param userId user ID
     * @param userType user type
     */
    void removeAccessToken(Long userId, Integer userType);

    /**
     * Get the paginated access tokens
     *
     * @param request request
     * @return paginated access tokens
     */
    PageResult<OAuth2AccessTokenEntity> getAccessTokenPage(OAuth2AccessTokenPageRequest request);

}
