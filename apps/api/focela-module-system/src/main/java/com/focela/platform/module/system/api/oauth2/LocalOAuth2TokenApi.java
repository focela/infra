package com.focela.platform.module.system.api.oauth2;

import com.focela.platform.framework.common.contract.system.oauth2.OAuth2TokenCommonApi;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.common.contract.system.oauth2.dto.OAuth2AccessTokenCheckRpcResponse;
import com.focela.platform.framework.common.contract.system.oauth2.dto.OAuth2AccessTokenCreateRpcRequest;
import com.focela.platform.framework.common.contract.system.oauth2.dto.OAuth2AccessTokenRpcResponse;
import com.focela.platform.module.system.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.module.system.service.oauth2.OAuth2TokenService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * OAuth2.0 Token API 实现类
 */
@Service
public class LocalOAuth2TokenApi implements OAuth2TokenCommonApi {

    @Resource
    private OAuth2TokenService oauth2TokenService;

    @Override
    public OAuth2AccessTokenRpcResponse createAccessToken(OAuth2AccessTokenCreateRpcRequest reqDTO) {
        OAuth2AccessTokenEntity accessTokenDO = oauth2TokenService.createAccessToken(
                reqDTO.getUserId(), reqDTO.getUserType(), reqDTO.getClientId(), reqDTO.getScopes());
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
