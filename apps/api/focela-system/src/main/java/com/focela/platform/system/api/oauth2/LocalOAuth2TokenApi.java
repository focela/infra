package com.focela.platform.system.api.oauth2;

import com.focela.platform.common.api.system.oauth2.OAuth2TokenContractApi;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.common.api.system.oauth2.dto.OAuth2AccessTokenCheckRpcResponse;
import com.focela.platform.common.api.system.oauth2.dto.OAuth2AccessTokenCreateRpcRequest;
import com.focela.platform.common.api.system.oauth2.dto.OAuth2AccessTokenRpcResponse;
import com.focela.platform.system.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.system.service.oauth2.OAuth2TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * OAuth2.0 Token API implementation class
 */
@Service
@RequiredArgsConstructor
public class LocalOAuth2TokenApi implements OAuth2TokenContractApi {

    private final OAuth2TokenService oauth2TokenService;

    @Override
    public OAuth2AccessTokenRpcResponse createAccessToken(OAuth2AccessTokenCreateRpcRequest request) {
        OAuth2AccessTokenEntity accessTokenDO = oauth2TokenService.createAccessToken(
                request.getUserId(), request.getUserType(), request.getClientId(), request.getScopes());
        return BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenRpcResponse.class);
    }

    @Override
    public OAuth2AccessTokenCheckRpcResponse checkAccessToken(String accessToken) {
        OAuth2AccessTokenEntity accessTokenDO = oauth2TokenService.checkAccessToken(accessToken);
        return BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenCheckRpcResponse.class);
    }

    @Override
    public OAuth2AccessTokenRpcResponse removeAccessToken(String accessToken) {
        OAuth2AccessTokenEntity accessTokenDO = oauth2TokenService.removeAccessToken(accessToken);
        return BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenRpcResponse.class);
    }

    @Override
    public OAuth2AccessTokenRpcResponse refreshAccessToken(String refreshToken, String clientId) {
        OAuth2AccessTokenEntity accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, clientId);
        return BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenRpcResponse.class);
    }

}
