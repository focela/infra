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
 * {@link DefaultOAuth2GrantService}  unit test
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
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();
        List<String> scopes = Lists.newArrayList("read", "write");
        // mock the method
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class);
        when(oauth2TokenService.createAccessToken(eq(userId), eq(userType),
                eq(clientId), eq(scopes))).thenReturn(accessTokenEntity);

        // invoke, and assert
        assertPojoEquals(accessTokenEntity, oauth2GrantService.grantImplicit(
                userId, userType, clientId, scopes));
    }

    @Test
    public void testGrantAuthorizationCodeForCode() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String clientId = randomString();
        List<String> scopes = Lists.newArrayList("read", "write");
        String redirectUri = randomString();
        String state = randomString();
        // mock the method
        OAuth2CodeEntity authorizationCode = randomPojo(OAuth2CodeEntity.class);
        when(oauth2CodeService.createAuthorizationCode(eq(userId), eq(userType),
                eq(clientId), eq(scopes), eq(redirectUri), eq(state))).thenReturn(authorizationCode);

        // invoke, and assert
        assertEquals(authorizationCode.getCode(), oauth2GrantService.grantAuthorizationCodeForCode(userId, userType,
                clientId, scopes, redirectUri, state));
    }

    @Test
    public void testGrantAuthorizationCodeForAccessToken() {
        // prepare parameters
        String clientId = randomString();
        String code = randomString();
        List<String> scopes = Lists.newArrayList("read", "write");
        String redirectUri = randomString();
        String state = randomString();
        // mock the method（code）
        OAuth2CodeEntity authorizationCode = randomPojo(OAuth2CodeEntity.class, o -> {
            o.setClientId(clientId);
            o.setRedirectUri(redirectUri);
            o.setState(state);
            o.setScopes(scopes);
        });
        when(oauth2CodeService.consumeAuthorizationCode(eq(code))).thenReturn(authorizationCode);
        // mock the method（create token）
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class);
        when(oauth2TokenService.createAccessToken(eq(authorizationCode.getUserId()), eq(authorizationCode.getUserType()),
                eq(authorizationCode.getClientId()), eq(authorizationCode.getScopes()))).thenReturn(accessTokenEntity);

        // invoke, and assert
        assertPojoEquals(accessTokenEntity, oauth2GrantService.grantAuthorizationCodeForAccessToken(
                clientId, code, redirectUri, state));
    }

    @Test
    public void testGrantPassword() {
        // prepare parameters
        String username = randomString();
        String password = randomString();
        String clientId = randomString();
        List<String> scopes = Lists.newArrayList("read", "write");
        // mock the method(authentication)
        UserEntity user = randomPojo(UserEntity.class);
        when(adminAuthService.authenticate(eq(username), eq(password))).thenReturn(user);
        // mock the method（access token）
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class);
        when(oauth2TokenService.createAccessToken(eq(user.getId()), eq(UserTypeEnum.ADMIN.getValue()),
                eq(clientId), eq(scopes))).thenReturn(accessTokenEntity);

        // invoke, and assert
        assertPojoEquals(accessTokenEntity, oauth2GrantService.grantPassword(
                username, password, clientId, scopes));
    }

    @Test
    public void testGrantRefreshToken() {
        // prepare parameters
        String refreshToken = randomString();
        String clientId = randomString();
        // mock the method
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class);
        when(oauth2TokenService.refreshAccessToken(eq(refreshToken), eq(clientId)))
                .thenReturn(accessTokenEntity);

        // invoke, and assert
        assertPojoEquals(accessTokenEntity, oauth2GrantService.grantRefreshToken(
                refreshToken, clientId));
    }

    @Test
    public void testRevokeToken_clientIdError() {
        // prepare parameters
        String clientId = randomString();
        String accessToken = randomString();
        // mock the method
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class);
        when(oauth2TokenService.getAccessToken(eq(accessToken))).thenReturn(accessTokenEntity);

        // invoke, and assert
        assertFalse(oauth2GrantService.revokeToken(clientId, accessToken));
    }

    @Test
    public void testRevokeToken_success() {
        // prepare parameters
        String clientId = randomString();
        String accessToken = randomString();
        // mock the method（access token）
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class).setClientId(clientId);
        when(oauth2TokenService.getAccessToken(eq(accessToken))).thenReturn(accessTokenEntity);
        // mock the method（remove）
        when(oauth2TokenService.removeAccessToken(eq(accessToken))).thenReturn(accessTokenEntity);

        // invoke, and assert
        assertTrue(oauth2GrantService.revokeToken(clientId, accessToken));
    }

}
