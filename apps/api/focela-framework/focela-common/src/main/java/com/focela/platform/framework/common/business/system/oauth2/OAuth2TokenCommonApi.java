package com.focela.platform.framework.common.business.system.oauth2;

import com.focela.platform.framework.common.business.system.oauth2.dto.OAuth2AccessTokenCheckRpcResponse;
import com.focela.platform.framework.common.business.system.oauth2.dto.OAuth2AccessTokenCreateRpcRequest;
import com.focela.platform.framework.common.business.system.oauth2.dto.OAuth2AccessTokenRpcResponse;

import jakarta.validation.Valid;

/**
 * OAuth2.0 Token API 接口
 */
public interface OAuth2TokenCommonApi {

    /**
     * 创建访问令牌
     *
     * @param reqDTO 访问令牌的创建信息
     * @return 访问令牌的信息
     */
    OAuth2AccessTokenRpcResponse createAccessToken(@Valid OAuth2AccessTokenCreateRpcRequest reqDTO);

    /**
     * 校验访问令牌
     *
     * @param accessToken 访问令牌
     * @return 访问令牌的信息
     */
    OAuth2AccessTokenCheckRpcResponse checkAccessToken(String accessToken);

    /**
     * 移除访问令牌
     *
     * @param accessToken 访问令牌
     * @return 访问令牌的信息
     */
    OAuth2AccessTokenRpcResponse removeAccessToken(String accessToken);

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @param clientId 客户端编号
     * @return 访问令牌的信息
     */
    OAuth2AccessTokenRpcResponse refreshAccessToken(String refreshToken, String clientId);

}
