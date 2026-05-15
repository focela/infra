package com.focela.platform.framework.common.api.system.oauth2;

import com.focela.platform.framework.common.api.system.oauth2.dto.OAuth2AccessTokenCheckRpcResponse;
import com.focela.platform.framework.common.api.system.oauth2.dto.OAuth2AccessTokenCreateRpcRequest;
import com.focela.platform.framework.common.api.system.oauth2.dto.OAuth2AccessTokenRpcResponse;

import jakarta.validation.Valid;

/**
 * OAuth2.0 Token API interface.
 */
public interface OAuth2TokenContractApi {

    /**
     * Create an access token.
     *
     * @param reqDTO access token creation info
     * @return access token info
     */
    OAuth2AccessTokenRpcResponse createAccessToken(@Valid OAuth2AccessTokenCreateRpcRequest reqDTO);

    /**
     * Validate an access token.
     *
     * @param accessToken access token
     * @return access token info
     */
    OAuth2AccessTokenCheckRpcResponse checkAccessToken(String accessToken);

    /**
     * Remove an access token.
     *
     * @param accessToken access token
     * @return access token info
     */
    OAuth2AccessTokenRpcResponse removeAccessToken(String accessToken);

    /**
     * Refresh an access token.
     *
     * @param refreshToken refresh token
     * @param clientId client ID
     * @return access token info
     */
    OAuth2AccessTokenRpcResponse refreshAccessToken(String refreshToken, String clientId);

}
