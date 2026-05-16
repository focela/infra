package com.focela.platform.system.service.oauth2;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.exception.ErrorCode;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.date.DateUtils;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import com.focela.platform.test.core.support.BaseDbAndRedisUnitTest;
import com.focela.platform.system.controller.admin.oauth2.dto.token.OAuth2AccessTokenPageRequest;
import com.focela.platform.system.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.system.entity.oauth2.OAuth2ClientEntity;
import com.focela.platform.system.entity.oauth2.OAuth2RefreshTokenEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.focela.platform.system.repository.mapper.oauth2.OAuth2AccessTokenMapper;
import com.focela.platform.system.repository.mapper.oauth2.OAuth2RefreshTokenMapper;
import com.focela.platform.system.repository.redis.oauth2.OAuth2AccessTokenRedisRepository;
import com.focela.platform.system.service.user.UserService;
import jakarta.annotation.Resource;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * {@link DefaultOAuth2TokenService}  unit test class
 */
@Import({DefaultOAuth2TokenService.class, OAuth2AccessTokenRedisRepository.class})
public class DefaultOAuth2TokenServiceTest extends BaseDbAndRedisUnitTest {

    @Resource
    private DefaultOAuth2TokenService oauth2TokenService;

    @Resource
    private OAuth2AccessTokenMapper oauth2AccessTokenMapper;
    @Resource
    private OAuth2RefreshTokenMapper oauth2RefreshTokenMapper;

    @Resource
    private OAuth2AccessTokenRedisRepository oauth2AccessTokenRedisDAO;

    @MockitoBean
    private OAuth2ClientService oauth2ClientService;
    @MockitoBean
    private UserService adminUserService;

    @Test
    public void testCreateAccessToken() {
        TenantContextHolder.setTenantId(0L);
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = UserTypeEnum.ADMIN.getValue();
        String clientId = randomString();
        List<String> scopes = Lists.newArrayList("read", "write");
        // mock the method
        OAuth2ClientEntity clientDO = randomPojo(OAuth2ClientEntity.class).setClientId(clientId)
                .setAccessTokenValiditySeconds(30).setRefreshTokenValiditySeconds(60);
        when(oauth2ClientService.validOAuthClientFromCache(eq(clientId))).thenReturn(clientDO);
        // mock data（user）
        UserEntity user = randomPojo(UserEntity.class);
        when(adminUserService.getUser(userId)).thenReturn(user);

        // invoke
        OAuth2AccessTokenEntity accessTokenDO = oauth2TokenService.createAccessToken(userId, userType, clientId, scopes);
        // assert access token
        OAuth2AccessTokenEntity dbAccessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessTokenDO.getAccessToken());
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(accessTokenDO, dbAccessTokenDO, "expiresTime", "createTime", "updateTime", "deleted");
        assertEquals(userId, accessTokenDO.getUserId());
        assertEquals(userType, accessTokenDO.getUserType());
        assertEquals(2, accessTokenDO.getUserInfo().size());
        assertEquals(user.getNickname(), accessTokenDO.getUserInfo().get("nickname"));
        assertEquals(user.getDeptId().toString(), accessTokenDO.getUserInfo().get("deptId"));
        assertEquals(clientId, accessTokenDO.getClientId());
        assertEquals(scopes, accessTokenDO.getScopes());
        assertFalse(DateUtils.isExpired(accessTokenDO.getExpiresTime()));
        // assert access token cache
        OAuth2AccessTokenEntity redisAccessTokenDO = oauth2AccessTokenRedisDAO.get(accessTokenDO.getAccessToken());
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(accessTokenDO, redisAccessTokenDO, "expiresTime", "createTime", "updateTime", "deleted");
        // assert refresh token
        OAuth2RefreshTokenEntity refreshTokenDO = oauth2RefreshTokenMapper.selectList().get(0);
        assertPojoEquals(accessTokenDO, refreshTokenDO, "id", "expiresTime", "createTime", "updateTime", "deleted");
        assertFalse(DateUtils.isExpired(refreshTokenDO.getExpiresTime()));
    }

    @Test
    public void testRefreshAccessToken_null() {
        // prepare parameters
        String refreshToken = randomString();
        String clientId = randomString();
        // mock the method

        // invoke, and assert
        assertServiceException(() -> oauth2TokenService.refreshAccessToken(refreshToken, clientId),
                new ErrorCode(400, "Invalid refresh token"));
    }

    @Test
    public void testRefreshAccessToken_clientIdError() {
        // prepare parameters
        String refreshToken = randomString();
        String clientId = randomString();
        // mock the method
        OAuth2ClientEntity clientDO = randomPojo(OAuth2ClientEntity.class).setClientId(clientId);
        when(oauth2ClientService.validOAuthClientFromCache(eq(clientId))).thenReturn(clientDO);
        // mock data（access token）
        OAuth2RefreshTokenEntity refreshTokenDO = randomPojo(OAuth2RefreshTokenEntity.class)
                .setRefreshToken(refreshToken).setClientId("error");
        oauth2RefreshTokenMapper.insert(refreshTokenDO);

        // invoke, and assert
        assertServiceException(() -> oauth2TokenService.refreshAccessToken(refreshToken, clientId),
                new ErrorCode(400, "Refresh token client ID is incorrect"));
    }

    @Test
    public void testRefreshAccessToken_expired() {
        // prepare parameters
        String refreshToken = randomString();
        String clientId = randomString();
        // mock the method
        OAuth2ClientEntity clientDO = randomPojo(OAuth2ClientEntity.class).setClientId(clientId);
        when(oauth2ClientService.validOAuthClientFromCache(eq(clientId))).thenReturn(clientDO);
        // mock data（access token）
        OAuth2RefreshTokenEntity refreshTokenDO = randomPojo(OAuth2RefreshTokenEntity.class)
                .setRefreshToken(refreshToken).setClientId(clientId)
                .setExpiresTime(LocalDateTime.now().minusDays(1));
        oauth2RefreshTokenMapper.insert(refreshTokenDO);

        // invoke, and assert
        assertServiceException(() -> oauth2TokenService.refreshAccessToken(refreshToken, clientId),
                new ErrorCode(401, "Refresh token has expired"));
        assertEquals(0, oauth2AccessTokenMapper.selectCount());
    }

    @Test
    public void testRefreshAccessToken_success() {
        TenantContextHolder.setTenantId(0L);
        // prepare parameters
        String refreshToken = randomString();
        String clientId = randomString();
        // mock the method
        OAuth2ClientEntity clientDO = randomPojo(OAuth2ClientEntity.class).setClientId(clientId)
                .setAccessTokenValiditySeconds(30);
        when(oauth2ClientService.validOAuthClientFromCache(eq(clientId))).thenReturn(clientDO);
        // mock data（access token）
        OAuth2RefreshTokenEntity refreshTokenDO = randomPojo(OAuth2RefreshTokenEntity.class, o ->
                o.setRefreshToken(refreshToken).setClientId(clientId)
                        .setExpiresTime(LocalDateTime.now().plusDays(1))
                        .setUserType(UserTypeEnum.ADMIN.getValue())
                        .setTenantId(TenantContextHolder.getTenantId()));
        oauth2RefreshTokenMapper.insert(refreshTokenDO);
        // mock data（access token）
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class).setRefreshToken(refreshToken)
                .setUserType(refreshTokenDO.getUserType());
        oauth2AccessTokenMapper.insert(accessTokenDO);
        oauth2AccessTokenRedisDAO.set(accessTokenDO);
        // mock data（user）
        UserEntity user = randomPojo(UserEntity.class);
        when(adminUserService.getUser(refreshTokenDO.getUserId())).thenReturn(user);

        // invoke
        OAuth2AccessTokenEntity newAccessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, clientId);
        // assert old access token is deleted
        assertNull(oauth2AccessTokenMapper.selectByAccessToken(accessTokenDO.getAccessToken()));
        assertNull(oauth2AccessTokenRedisDAO.get(accessTokenDO.getAccessToken()));
        // assert new access token
        OAuth2AccessTokenEntity dbAccessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(newAccessTokenDO.getAccessToken());
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(newAccessTokenDO, dbAccessTokenDO, "expiresTime", "createTime", "updateTime", "deleted");
        assertPojoEquals(newAccessTokenDO, refreshTokenDO, "id", "expiresTime", "createTime", "updateTime", "deleted",
                "creator", "updater");
        assertFalse(DateUtils.isExpired(newAccessTokenDO.getExpiresTime()));
        // assert new access token cache
        OAuth2AccessTokenEntity redisAccessTokenDO = oauth2AccessTokenRedisDAO.get(newAccessTokenDO.getAccessToken());
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(newAccessTokenDO, redisAccessTokenDO, "expiresTime", "createTime", "updateTime", "deleted");
    }

    @Test
    public void testGetAccessToken() {
        // mock data（access token）
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2AccessTokenMapper.insert(accessTokenDO);
        // prepare parameters
        String accessToken = accessTokenDO.getAccessToken();

        // invoke
        OAuth2AccessTokenEntity result = oauth2TokenService.getAccessToken(accessToken);
        // assert
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(accessTokenDO, result, "expiresTime", "createTime", "updateTime", "deleted",
                "creator", "updater");
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(accessTokenDO, oauth2AccessTokenRedisDAO.get(accessToken), "expiresTime", "createTime", "updateTime", "deleted",
                "creator", "updater");
    }

    @Test
    public void testCheckAccessToken_null() {
        // invoke, and assert
        assertServiceException(() -> oauth2TokenService.checkAccessToken(randomString()),
                new ErrorCode(401, "Access token does not exist"));
    }

    @Test
    public void testCheckAccessToken_expired() {
        // mock data（access token）
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class)
                .setExpiresTime(LocalDateTime.now().minusDays(1));
        oauth2AccessTokenMapper.insert(accessTokenDO);
        // prepare parameters
        String accessToken = accessTokenDO.getAccessToken();

        // invoke, and assert
        assertServiceException(() -> oauth2TokenService.checkAccessToken(accessToken),
                new ErrorCode(401, "Access token has expired"));
    }

    @Test
    public void testCheckAccessToken_refreshToken() {
        // mock data（access token）
        OAuth2RefreshTokenEntity refreshTokenDO = randomPojo(OAuth2RefreshTokenEntity.class)
                .setUserId(0L)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2RefreshTokenMapper.insert(refreshTokenDO);
        // prepare parameters
        String accessToken = refreshTokenDO.getRefreshToken();

        // invoke, and assert
        OAuth2AccessTokenEntity result = oauth2TokenService.getAccessToken(accessToken);
        // assert
        assertPojoEquals(refreshTokenDO, result, "expiresTime", "createTime", "updateTime", "deleted",
                "creator", "updater");
    }

    @Test
    public void testCheckAccessToken_success() {
        // mock data（access token）
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2AccessTokenMapper.insert(accessTokenDO);
        // prepare parameters
        String accessToken = accessTokenDO.getAccessToken();

        // invoke, and assert
        OAuth2AccessTokenEntity result = oauth2TokenService.getAccessToken(accessToken);
        // assert
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(accessTokenDO, result, "expiresTime", "createTime", "updateTime", "deleted",
                "creator", "updater");
    }

    @Test
    public void testRemoveAccessToken_null() {
        // invoke, and assert
        assertNull(oauth2TokenService.removeAccessToken(randomString()));
    }

    @Test
    public void testRemoveAccessToken_success() {
        // mock data（access token）
        OAuth2AccessTokenEntity accessTokenDO = randomPojo(OAuth2AccessTokenEntity.class)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2AccessTokenMapper.insert(accessTokenDO);
        // mock data（refresh token）
        OAuth2RefreshTokenEntity refreshTokenDO = randomPojo(OAuth2RefreshTokenEntity.class)
                .setRefreshToken(accessTokenDO.getRefreshToken());
        oauth2RefreshTokenMapper.insert(refreshTokenDO);
        // invoke
        OAuth2AccessTokenEntity result = oauth2TokenService.removeAccessToken(accessTokenDO.getAccessToken());
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(accessTokenDO, result, "expiresTime", "createTime", "updateTime", "deleted",
                "creator", "updater");
        // assert data
        assertNull(oauth2AccessTokenMapper.selectByAccessToken(accessTokenDO.getAccessToken()));
        assertNull(oauth2RefreshTokenMapper.selectByRefreshToken(accessTokenDO.getRefreshToken()));
        assertNull(oauth2AccessTokenRedisDAO.get(accessTokenDO.getAccessToken()));
    }


    @Test
    public void testGetAccessTokenPage() {
        // mock data
        OAuth2AccessTokenEntity dbAccessToken = randomPojo(OAuth2AccessTokenEntity.class, o -> { // will be queried later
            o.setUserId(10L);
            o.setUserType(1);
            o.setClientId("test_client");
            o.setExpiresTime(LocalDateTime.now().plusDays(1));
        });
        oauth2AccessTokenMapper.insert(dbAccessToken);
        // test userId mismatch
        oauth2AccessTokenMapper.insert(cloneIgnoreId(dbAccessToken, o -> o.setUserId(20L)));
        // test userType mismatch
        oauth2AccessTokenMapper.insert(cloneIgnoreId(dbAccessToken, o -> o.setUserType(2)));
        // test userType mismatch
        oauth2AccessTokenMapper.insert(cloneIgnoreId(dbAccessToken, o -> o.setClientId("it_client")));
        // test expireTime mismatch
        oauth2AccessTokenMapper.insert(cloneIgnoreId(dbAccessToken, o -> o.setExpiresTime(LocalDateTimeUtil.now())));
        // prepare parameters
        OAuth2AccessTokenPageRequest request = new OAuth2AccessTokenPageRequest();
        request.setUserId(10L);
        request.setUserType(1);
        request.setClientId("test");

        // invoke
        PageResult<OAuth2AccessTokenEntity> pageResult = oauth2TokenService.getAccessTokenPage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(dbAccessToken, pageResult.getList().get(0), "expiresTime");
    }

}
