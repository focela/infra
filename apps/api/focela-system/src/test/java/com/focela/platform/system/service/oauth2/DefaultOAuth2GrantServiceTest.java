package com.focela.platform.system.service.oauth2;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.test.core.support.BaseMockitoUnitTest;
import com.focela.platform.system.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.system.entity.oauth2.OAuth2CodeEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.focela.platform.system.service.auth.AuthService;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * {@link DefaultOAuth2GrantService} 的单元测试
 */
public class DefaultOAuth2GrantServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DefaultOAuth2GrantService oauth2GrantService;

    @Mock
    private OAuth2TokenService oauth2TokenService;
    @Mock
    private OAuth2CodeService oauth2CodeService;
    @Mock
    private AuthService adminAuthService;

    @Test
    public void testGrantImplicit() {
        // 准备参数
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();
        List<String> scopes = Lists.newArrayList("read", "write");
        // mock 方法
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class);
        when(oauth2TokenService.createAccessToken(eq(userId), eq(userType),
                eq(clientId), eq(scopes))).thenReturn(accessTokenDO);

        // 调用，并断言
        assertPojoEquals(accessTokenDO, oauth2GrantService.grantImplicit(
                userId, userType, clientId, scopes));
    }

    @Test
    public void testGrantAuthorizationCodeForCode() {
        // 准备参数
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();
        List<String> scopes = Lists.newArrayList("read", "write");
        String redirectUri = randomString();
        String state = randomString();
        // mock 方法
        OAuth2CodeEntity codeDO = randomPojo(OAuth2CodeEntity.class);
        when(oauth2CodeService.createAuthorizationCode(eq(userId), eq(userType),
                eq(clientId), eq(scopes), eq(redirectUri), eq(state))).thenReturn(codeDO);

        // 调用，并断言
        assertEquals(codeDO.getCode(), oauth2GrantService.grantAuthorizationCodeForCode(userId, userType,
                clientId, scopes, redirectUri, state));
    }

    @Test
    public void testGrantAuthorizationCodeForAccessToken() {
        // 准备参数
        String clientId = randomString();
        String code = randomString();
        List<String> scopes = Lists.newArrayList("read", "write");
        String redirectUri = randomString();
        String state = randomString();
        // mock 方法（code）
        OAuth2CodeEntity codeDO = randomPojo(OAuth2CodeEntity.class, o -> {
            o.setClientId(clientId);
            o.setRedirectUri(redirectUri);
            o.setState(state);
            o.setScopes(scopes);
        });
        when(oauth2CodeService.consumeAuthorizationCode(eq(code))).thenReturn(codeDO);
        // mock 方法（创建令牌）
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class);
        when(oauth2TokenService.createAccessToken(eq(codeDO.getUserId()), eq(codeDO.getUserType()),
                eq(codeDO.getClientId()), eq(codeDO.getScopes()))).thenReturn(accessTokenDO);

        // 调用，并断言
        assertPojoEquals(accessTokenDO, oauth2GrantService.grantAuthorizationCodeForAccessToken(
                clientId, code, redirectUri, state));
    }

    @Test
    public void testGrantPassword() {
        // 准备参数
        String username = randomString();
        String password = randomString();
        String clientId = randomString();
        List<String> scopes = Lists.newArrayList("read", "write");
        // mock 方法(认证)
        UserEntity user = randomPojo(UserEntity.class);
        when(adminAuthService.authenticate(eq(username), eq(password))).thenReturn(user);
        // mock 方法（访问令牌）
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class);
        when(oauth2TokenService.createAccessToken(eq(user.getId()), eq(UserTypeEnum.ADMIN.getValue()),
                eq(clientId), eq(scopes))).thenReturn(accessTokenDO);

        // 调用，并断言
        assertPojoEquals(accessTokenDO, oauth2GrantService.grantPassword(
                username, password, clientId, scopes));
    }

    @Test
    public void testGrantRefreshToken() {
        // 准备参数
        String refreshToken = randomString();
        String clientId = randomString();
        // mock 方法
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class);
        when(oauth2TokenService.refreshAccessToken(eq(refreshToken), eq(clientId)))
                .thenReturn(accessTokenDO);

        // 调用，并断言
        assertPojoEquals(accessTokenDO, oauth2GrantService.grantRefreshToken(
                refreshToken, clientId));
    }

    @Test
    public void testRevokeToken_clientIdError() {
        // 准备参数
        String clientId = randomString();
        String accessToken = randomString();
        // mock 方法
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class);
        when(oauth2TokenService.getAccessToken(eq(accessToken))).thenReturn(accessTokenDO);

        // 调用，并断言
        assertFalse(oauth2GrantService.revokeToken(clientId, accessToken));
    }

    @Test
    public void testRevokeToken_success() {
        // 准备参数
        String clientId = randomString();
        String accessToken = randomString();
        // mock 方法（访问令牌）
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class).setClientId(clientId);
        when(oauth2TokenService.getAccessToken(eq(accessToken))).thenReturn(accessTokenDO);
        // mock 方法（移除）
        when(oauth2TokenService.removeAccessToken(eq(accessToken))).thenReturn(accessTokenDO);

        // 调用，并断言
        assertTrue(oauth2GrantService.revokeToken(clientId, accessToken));
    }

}
