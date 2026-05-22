package com.focela.platform.system.controller.admin.oauth2;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import com.focela.platform.common.core.KeyValue;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.exception.ErrorCode;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.collection.SetUtils;
import com.focela.platform.common.utils.object.ObjectUtils;
import com.focela.platform.test.core.support.BaseMockitoUnitTest;
import com.focela.platform.system.controller.admin.oauth2.response.open.OAuth2OpenAccessTokenResponse;
import com.focela.platform.system.controller.admin.oauth2.response.open.OAuth2OpenAuthorizeInfoResponse;
import com.focela.platform.system.controller.admin.oauth2.response.open.OAuth2OpenCheckTokenResponse;
import com.focela.platform.system.domain.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.system.domain.entity.oauth2.OAuth2ApproveEntity;
import com.focela.platform.system.domain.entity.oauth2.OAuth2ClientEntity;
import com.focela.platform.system.enums.oauth2.OAuth2GrantTypeEnum;
import com.focela.platform.system.service.oauth2.OAuth2ApproveService;
import com.focela.platform.system.service.oauth2.OAuth2ClientService;
import com.focela.platform.system.service.oauth2.OAuth2GrantService;
import com.focela.platform.system.service.oauth2.OAuth2TokenService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.focela.platform.common.utils.collection.SetUtils.asSet;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.test.core.utils.RandomUtils.randomString;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link OAuth2OpenController}  unit test
 */
public class OAuth2OpenControllerTest extends BaseMockitoUnitTest {

    @InjectMocks
    private OAuth2OpenController oauth2OpenController;

    @Mock
    private OAuth2GrantService oauth2GrantService;
    @Mock
    private OAuth2ClientService oauth2ClientService;
    @Mock
    private OAuth2ApproveService oauth2ApproveService;
    @Mock
    private OAuth2TokenService oauth2TokenService;

    @Test
    public void postAccessToken_authorizationCode() {
        // prepare parameters
        String granType = OAuth2GrantTypeEnum.AUTHORIZATION_CODE.getGrantType();
        String code = randomString();
        String redirectUri = randomString();
        String state = randomString();
        HttpServletRequest request = mockRequest("test_client_id", "test_client_secret");
        // mock the method（client）
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId("test_client_id");
        when(oauth2ClientService.validateOAuthClientFromCache(eq("test_client_id"), eq("test_client_secret"), eq(granType), eq(new ArrayList<>()), eq(redirectUri))).thenReturn(client);

        // mock the method（access token）
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class)
                .setExpiresTime(LocalDateTimeUtil.offset(LocalDateTime.now(), 30000L, ChronoUnit.MILLIS));
        when(oauth2GrantService.grantAuthorizationCodeForAccessToken(eq("test_client_id"),
                eq(code), eq(redirectUri), eq(state))).thenReturn(accessTokenEntity);

        // invoke
        CommonResult<OAuth2OpenAccessTokenResponse> result = oauth2OpenController.postAccessToken(request, granType,
                code, redirectUri, state, null, null, null, null);
        // assert
        assertEquals(0, result.getCode());
        assertPojoEquals(accessTokenEntity, result.getData());
        assertTrue(ObjectUtils.equalsAny(result.getData().getExpiresIn(), 29L, 30L));  // execution takes a few milliseconds
    }

    @Test
    public void postAccessToken_password() {
        // prepare parameters
        String granType = OAuth2GrantTypeEnum.PASSWORD.getGrantType();
        String username = randomString();
        String password = randomString();
        String scope = "write read";
        HttpServletRequest request = mockRequest("test_client_id", "test_client_secret");
        // mock the method（client）
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId("test_client_id");
        when(oauth2ClientService.validateOAuthClientFromCache(eq("test_client_id"), eq("test_client_secret"),
                eq(granType), eq(Lists.newArrayList("write", "read")), isNull())).thenReturn(client);

        // mock the method（access token）
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class)
                .setExpiresTime(LocalDateTimeUtil.offset(LocalDateTime.now(), 30000L, ChronoUnit.MILLIS));
        when(oauth2GrantService.grantPassword(eq(username), eq(password), eq("test_client_id"),
                eq(Lists.newArrayList("write", "read")))).thenReturn(accessTokenEntity);

        // invoke
        CommonResult<OAuth2OpenAccessTokenResponse> result = oauth2OpenController.postAccessToken(request, granType,
                null, null, null, username, password, scope, null);
        // assert
        assertEquals(0, result.getCode());
        assertPojoEquals(accessTokenEntity, result.getData());
        assertTrue(ObjectUtils.equalsAny(result.getData().getExpiresIn(), 29L, 30L));  // execution takes a few milliseconds
    }

    @Test
    public void postAccessToken_refreshToken() {
        // prepare parameters
        String granType = OAuth2GrantTypeEnum.REFRESH_TOKEN.getGrantType();
        String refreshToken = randomString();
        String password = randomString();
        HttpServletRequest request = mockRequest("test_client_id", "test_client_secret");
        // mock the method（client）
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId("test_client_id");
        when(oauth2ClientService.validateOAuthClientFromCache(eq("test_client_id"), eq("test_client_secret"),
                eq(granType), eq(Lists.newArrayList()), isNull())).thenReturn(client);

        // mock the method（access token）
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class)
                .setExpiresTime(LocalDateTimeUtil.offset(LocalDateTime.now(), 30000L, ChronoUnit.MILLIS));
        when(oauth2GrantService.grantRefreshToken(eq(refreshToken), eq("test_client_id"))).thenReturn(accessTokenEntity);

        // invoke
        CommonResult<OAuth2OpenAccessTokenResponse> result = oauth2OpenController.postAccessToken(request, granType,
                null, null, null, null, password, null, refreshToken);
        // assert
        assertEquals(0, result.getCode());
        assertPojoEquals(accessTokenEntity, result.getData());
        assertTrue(ObjectUtils.equalsAny(result.getData().getExpiresIn(), 29L, 30L));  // execution takes a few milliseconds
    }

    @Test
    public void postAccessToken_implicit() {
        // invoke, and assert
        assertServiceException(() -> oauth2OpenController.postAccessToken(null,
                        OAuth2GrantTypeEnum.IMPLICIT.getGrantType(), null, null, null,
                        null, null, null, null),
                new ErrorCode(400, "Token endpoint does not support implicit grant mode"));
    }

    @Test
    public void revokeToken() {
        // prepare parameters
        HttpServletRequest request = mockRequest("demo_client_id", "demo_client_secret");
        String token = randomString();
        // mock the method（client）
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId("demo_client_id");
        when(oauth2ClientService.validateOAuthClientFromCache(eq("demo_client_id"),
                eq("demo_client_secret"), isNull(), isNull(), isNull())).thenReturn(client);
        // mock the method（remove）
        when(oauth2GrantService.revokeToken(eq("demo_client_id"), eq(token))).thenReturn(true);

        // invoke
        CommonResult<Boolean> result = oauth2OpenController.revokeToken(request, token);
        // assert
        assertEquals(0, result.getCode());
        assertTrue(result.getData());
    }

    @Test
    public void checkToken() {
        // prepare parameters
        HttpServletRequest request = mockRequest("demo_client_id", "demo_client_secret");
        String token = randomString();
        // mock the method
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class).setUserType(UserTypeEnum.ADMIN.getValue()).setExpiresTime(LocalDateTimeUtil.of(1653485731195L));
        when(oauth2TokenService.checkAccessToken(eq(token))).thenReturn(accessTokenEntity);

        // invoke
        CommonResult<OAuth2OpenCheckTokenResponse> result = oauth2OpenController.checkToken(request, token);
        // assert
        assertEquals(0, result.getCode());
        assertPojoEquals(accessTokenEntity, result.getData());
        assertEquals(1653485731L, result.getData().getExp()); // execution takes a few milliseconds
    }

    @Test
    public void authorize() {
        // prepare parameters
        String clientId = randomString();
        // mock the method（client）
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId("demo_client_id").setScopes(ListUtil.toList("read", "write", "all"));
        when(oauth2ClientService.validateOAuthClientFromCache(eq(clientId))).thenReturn(client);
        // mock the method（approve）
        List<OAuth2ApproveEntity> approves = asList(
                randomPojo(OAuth2ApproveEntity.class).setScope("read").setApproved(true),
                randomPojo(OAuth2ApproveEntity.class).setScope("write").setApproved(false));
        when(oauth2ApproveService.getApproveList(isNull(), eq(UserTypeEnum.ADMIN.getValue()), eq(clientId))).thenReturn(approves);

        // invoke
        CommonResult<OAuth2OpenAuthorizeInfoResponse> result = oauth2OpenController.authorize(clientId);
        // assert
        assertEquals(0, result.getCode());
        assertPojoEquals(client, result.getData().getClient());
        assertEquals(new KeyValue<>("read", true), result.getData().getScopes().get(0));
        assertEquals(new KeyValue<>("write", false), result.getData().getScopes().get(1));
        assertEquals(new KeyValue<>("all", false), result.getData().getScopes().get(2));
    }

    @Test
    public void approveOrDeny_grantTypeError() {
        // invoke, and assert
        assertServiceException(() -> oauth2OpenController.approveOrDeny(randomString(), null,
                        null, null, null, null),
                new ErrorCode(400, "response_type parameter value only allows code and token"));
    }

    @Test // autoApprove = true, but does not pass
    public void approveOrDeny_autoApproveNo() {
        // prepare parameters
        String responseType = "code";
        String clientId = randomString();
        String scope = "{\"read\": true, \"write\": false}";
        String redirectUri = randomString();
        String state = randomString();
        // mock the method
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class);
        when(oauth2ClientService.validateOAuthClientFromCache(eq(clientId), isNull(), eq("authorization_code"),
                eq(asSet("read", "write")), eq(redirectUri))).thenReturn(client);

        // invoke
        CommonResult<String> result = oauth2OpenController.approveOrDeny(responseType, clientId,
                scope, redirectUri, true, state);
        // assert
        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test // autoApprove = false, but does not pass
    public void approveOrDeny_ApproveNo() {
        // prepare parameters
        String responseType = "token";
        String clientId = randomString();
        String scope = "{\"read\": true, \"write\": false}";
        String redirectUri = "https://www.example.com";
        String state = "test";
        // mock the method
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class);
        when(oauth2ClientService.validateOAuthClientFromCache(eq(clientId), isNull(), eq("implicit"),
                eq(asSet("read", "write")), eq(redirectUri))).thenReturn(client);

        // invoke
        CommonResult<String> result = oauth2OpenController.approveOrDeny(responseType, clientId,
                scope, redirectUri, false, state);
        // assert
        assertEquals(0, result.getCode());
        assertEquals("https://www.example.com#error=access_denied&error_description=User%20denied%20access&state=test", result.getData());
    }

    @Test // autoApprove = true, pass + token
    public void approveOrDeny_autoApproveWithToken() {
        // prepare parameters
        String responseType = "token";
        String clientId = randomString();
        String scope = "{\"read\": true, \"write\": false}";
        String redirectUri = "https://www.example.com";
        String state = "test";
        // mock the method（client)
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId(clientId).setAdditionalInformation(null);
        when(oauth2ClientService.validateOAuthClientFromCache(eq(clientId), isNull(), eq("implicit"),
                eq(asSet("read", "write")), eq(redirectUri))).thenReturn(client);
        // mock the method（scenario 1）
        when(oauth2ApproveService.checkForPreApproval(isNull(), eq(UserTypeEnum.ADMIN.getValue()),
                eq(clientId), eq(SetUtils.asSet("read", "write")))).thenReturn(true);
        // mock the method（access token）
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class)
                .setAccessToken("test_access_token").setExpiresTime(LocalDateTimeUtil.offset(LocalDateTime.now(), 30010L, ChronoUnit.MILLIS));
        when(oauth2GrantService.grantImplicit(isNull(), eq(UserTypeEnum.ADMIN.getValue()),
                eq(clientId), eq(ListUtil.toList("read")))).thenReturn(accessTokenEntity);

        // invoke
        CommonResult<String> result = oauth2OpenController.approveOrDeny(responseType, clientId,
                scope, redirectUri, true, state);
        // assert
        assertEquals(0, result.getCode());
        assertThat(result.getData(), anyOf( // 29 30 also has some probability, mainly due to time calculation
                is("https://www.example.com#access_token=test_access_token&token_type=bearer&state=test&expires_in=29&scope=read"),
                is("https://www.example.com#access_token=test_access_token&token_type=bearer&state=test&expires_in=30&scope=read")
        ));
    }

    @Test // autoApprove = false, pass + code
    public void approveOrDeny_approveWithCode() {
        // prepare parameters
        String responseType = "code";
        String clientId = randomString();
        String scope = "{\"read\": true, \"write\": false}";
        String redirectUri = "https://www.example.com";
        String state = "test";
        // mock the method（client)
        OAuth2ClientEntity client = randomPojo(OAuth2ClientEntity.class).setClientId(clientId).setAdditionalInformation(null);
        when(oauth2ClientService.validateOAuthClientFromCache(eq(clientId), isNull(), eq("authorization_code"),
                eq(asSet("read", "write")), eq(redirectUri))).thenReturn(client);
        // mock the method（scenario 2）
        when(oauth2ApproveService.updateAfterApproval(isNull(), eq(UserTypeEnum.ADMIN.getValue()), eq(clientId),
                eq(MapUtil.builder(new LinkedHashMap<String, Boolean>()).put("read", true).put("write", false).build())))
                .thenReturn(true);
        // mock the method（access token）
        String authorizationCode = "test_code";
        when(oauth2GrantService.grantAuthorizationCodeForCode(isNull(), eq(UserTypeEnum.ADMIN.getValue()),
                eq(clientId), eq(ListUtil.toList("read")), eq(redirectUri), eq(state))).thenReturn(authorizationCode);

        // invoke
        CommonResult<String> result = oauth2OpenController.approveOrDeny(responseType, clientId,
                scope, redirectUri, false, state);
        // assert
        assertEquals(0, result.getCode());
        assertEquals("https://www.example.com?code=test_code&state=test", result.getData());
    }

    private HttpServletRequest mockRequest(String clientId, String secret) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter(eq("client_id"))).thenReturn(clientId);
        when(request.getParameter(eq("client_secret"))).thenReturn(secret);
        return request;
    }

}
