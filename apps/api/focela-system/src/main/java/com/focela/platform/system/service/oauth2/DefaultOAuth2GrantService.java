package com.focela.platform.system.service.oauth2;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.system.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.system.entity.oauth2.OAuth2CodeEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.focela.platform.system.constants.SystemErrorCodeConstants;
import com.focela.platform.system.service.auth.AuthService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;

/**
 * OAuth2 Grant Service implementation class
 */
@Service
public class DefaultOAuth2GrantService implements OAuth2GrantService {

    @Resource
    private OAuth2TokenService oauth2TokenService;
    @Resource
    private OAuth2CodeService oauth2CodeService;
    @Resource
    private AuthService adminAuthService;

    @Override
    public OAuth2AccessTokenEntity grantImplicit(Long userId, Integer userType,
                                             String clientId, List<String> scopes) {
        return oauth2TokenService.createAccessToken(userId, userType, clientId, scopes);
    }

    @Override
    public String grantAuthorizationCodeForCode(Long userId, Integer userType,
                                                String clientId, List<String> scopes,
                                                String redirectUri, String state) {
        return oauth2CodeService.createAuthorizationCode(userId, userType, clientId, scopes,
                redirectUri, state).getCode();
    }

    @Override
    public OAuth2AccessTokenEntity grantAuthorizationCodeForAccessToken(String clientId, String code,
                                                                    String redirectUri, String state) {
        OAuth2CodeEntity codeDO = oauth2CodeService.consumeAuthorizationCode(code);
        Assert.notNull(codeDO, "Authorization code must not be blank"); // defensive programming
        // Validate that clientId matches
        if (!StrUtil.equals(clientId, codeDO.getClientId())) {
            throw exception(SystemErrorCodeConstants.OAUTH2_GRANT_CLIENT_ID_MISMATCH);
        }
        // Validate that redirectUri matches
        if (!StrUtil.equals(redirectUri, codeDO.getRedirectUri())) {
            throw exception(SystemErrorCodeConstants.OAUTH2_GRANT_REDIRECT_URI_MISMATCH);
        }
        // Validate that state matches
        state = StrUtil.nullToDefault(state, ""); // when database state is null, it will be set to empty string ""
        if (!StrUtil.equals(state, codeDO.getState())) {
            throw exception(SystemErrorCodeConstants.OAUTH2_GRANT_STATE_MISMATCH);
        }

        // Create access token
        return oauth2TokenService.createAccessToken(codeDO.getUserId(), codeDO.getUserType(),
                codeDO.getClientId(), codeDO.getScopes());
    }

    @Override
    public OAuth2AccessTokenEntity grantPassword(String username, String password, String clientId, List<String> scopes) {
        // Login using account + password
        UserEntity user = adminAuthService.authenticate(username, password);
        Assert.notNull(user, "User must not be blank!"); // defensive programming

        // Create access token
        return oauth2TokenService.createAccessToken(user.getId(), UserTypeEnum.ADMIN.getValue(), clientId, scopes);
    }

    @Override
    public OAuth2AccessTokenEntity grantRefreshToken(String refreshToken, String clientId) {
        return oauth2TokenService.refreshAccessToken(refreshToken, clientId);
    }

    @Override
    public OAuth2AccessTokenEntity grantClientCredentials(String clientId, List<String> scopes) {
        // Special: https://yuanbao.tencent.com/bot/app/share/chat/wFj642xSZHHx
        return oauth2TokenService.createAccessToken(0L, UserTypeEnum.ADMIN.getValue(), clientId, scopes);
    }

    @Override
    public boolean revokeToken(String clientId, String accessToken) {
        // Query first to ensure clientId matches
        OAuth2AccessTokenEntity accessTokenDO = oauth2TokenService.getAccessToken(accessToken);
        if (accessTokenDO == null || ObjectUtil.notEqual(clientId, accessTokenDO.getClientId())) {
            return false;
        }
        // Then delete
        return oauth2TokenService.removeAccessToken(accessToken) != null;
    }

}
