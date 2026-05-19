package com.focela.platform.system.service.oauth2;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.exception.ErrorCode;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.date.DateUtils;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import com.focela.platform.test.core.support.BaseDbAndRedisUnitTest;
import com.focela.platform.system.controller.admin.oauth2.request.token.OAuth2AccessTokenPageRequest;
import com.focela.platform.system.domain.entity.oauth2.OAuth2AccessTokenEntity;
import com.focela.platform.system.domain.entity.oauth2.OAuth2ClientEntity;
import com.focela.platform.system.domain.entity.oauth2.OAuth2RefreshTokenEntity;
import com.focela.platform.system.domain.entity.user.UserEntity;
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
    private OAuth2AccessTokenRedisRepository oauth2AccessTokenRedisRepository;

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
        OAuth2ClientEntity clientEntity = randomPojo(OAuth2ClientEntity.class).setClientId(clientId)
                .setAccessTokenValiditySeconds(30).setRefreshTokenValiditySeconds(60);
        when(oauth2ClientService.validateOAuthClientFromCache(eq(clientId))).thenReturn(clientEntity);
        // mock data（user）
        UserEntity user = randomPojo(UserEntity.class);
        when(adminUserService.getUser(userId)).thenReturn(user);

        // invoke
        OAuth2AccessTokenEntity accessTokenEntity = oauth2TokenService.createAccessToken(userId, userType, clientId, scopes);
        // assert access token
        OAuth2AccessTokenEntity dbAccessTokenEntity = oauth2AccessTokenMapper.selectByAccessToken(accessTokenEntity.getAccessToken());
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(accessTokenEntity, dbAccessTokenEntity, "expiresTime", "createTime", "updateTime", "deleted");
        assertEquals(userId, accessTokenEntity.getUserId());
        assertEquals(userType, accessTokenEntity.getUserType());
        assertEquals(2, accessTokenEntity.getUserInfo().size());
        assertEquals(user.getNickname(), accessTokenEntity.getUserInfo().get("nickname"));
        assertEquals(user.getDeptId().toString(), accessTokenEntity.getUserInfo().get("deptId"));
        assertEquals(clientId, accessTokenEntity.getClientId());
        assertEquals(scopes, accessTokenEntity.getScopes());
        assertFalse(DateUtils.isExpired(accessTokenEntity.getExpiresTime()));
        // assert access token cache
        OAuth2AccessTokenEntity redisAccessTokenEntity = oauth2AccessTokenRedisRepository.get(accessTokenEntity.getAccessToken());
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(accessTokenEntity, redisAccessTokenEntity, "expiresTime", "createTime", "updateTime", "deleted");
        // assert refresh token
        OAuth2RefreshTokenEntity refreshTokenEntity = oauth2RefreshTokenMapper.selectList().get(0);
        assertPojoEquals(accessTokenEntity, refreshTokenEntity, "id", "expiresTime", "createTime", "updateTime", "deleted");
        assertFalse(DateUtils.isExpired(refreshTokenEntity.getExpiresTime()));
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
        OAuth2ClientEntity clientEntity = randomPojo(OAuth2ClientEntity.class).setClientId(clientId);
        when(oauth2ClientService.validateOAuthClientFromCache(eq(clientId))).thenReturn(clientEntity);
        // mock data（access token）
        OAuth2RefreshTokenEntity refreshTokenEntity = randomPojo(OAuth2RefreshTokenEntity.class)
                .setRefreshToken(refreshToken).setClientId("error");
        oauth2RefreshTokenMapper.insert(refreshTokenEntity);

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
        OAuth2ClientEntity clientEntity = randomPojo(OAuth2ClientEntity.class).setClientId(clientId);
        when(oauth2ClientService.validateOAuthClientFromCache(eq(clientId))).thenReturn(clientEntity);
        // mock data（access token）
        OAuth2RefreshTokenEntity refreshTokenEntity = randomPojo(OAuth2RefreshTokenEntity.class)
                .setRefreshToken(refreshToken).setClientId(clientId)
                .setExpiresTime(LocalDateTime.now().minusDays(1));
        oauth2RefreshTokenMapper.insert(refreshTokenEntity);

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
        OAuth2ClientEntity clientEntity = randomPojo(OAuth2ClientEntity.class).setClientId(clientId)
                .setAccessTokenValiditySeconds(30);
        when(oauth2ClientService.validateOAuthClientFromCache(eq(clientId))).thenReturn(clientEntity);
        // mock data（access token）
        OAuth2RefreshTokenEntity refreshTokenEntity = randomPojo(OAuth2RefreshTokenEntity.class, o ->
                o.setRefreshToken(refreshToken).setClientId(clientId)
                        .setExpiresTime(LocalDateTime.now().plusDays(1))
                        .setUserType(UserTypeEnum.ADMIN.getValue())
                        .setTenantId(TenantContextHolder.getTenantId()));
        oauth2RefreshTokenMapper.insert(refreshTokenEntity);
        // mock data（access token）
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class).setRefreshToken(refreshToken)
                .setUserType(refreshTokenEntity.getUserType());
        oauth2AccessTokenMapper.insert(accessTokenEntity);
        oauth2AccessTokenRedisRepository.set(accessTokenEntity);
        // mock data（user）
        UserEntity user = randomPojo(UserEntity.class);
        when(adminUserService.getUser(refreshTokenEntity.getUserId())).thenReturn(user);

        // invoke
        OAuth2AccessTokenEntity newAccessTokenEntity = oauth2TokenService.refreshAccessToken(refreshToken, clientId);
        // assert old access token is deleted
        assertNull(oauth2AccessTokenMapper.selectByAccessToken(accessTokenEntity.getAccessToken()));
        assertNull(oauth2AccessTokenRedisRepository.get(accessTokenEntity.getAccessToken()));
        // assert new access token
        OAuth2AccessTokenEntity dbAccessTokenEntity = oauth2AccessTokenMapper.selectByAccessToken(newAccessTokenEntity.getAccessToken());
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(newAccessTokenEntity, dbAccessTokenEntity, "expiresTime", "createTime", "updateTime", "deleted");
        assertPojoEquals(newAccessTokenEntity, refreshTokenEntity, "id", "expiresTime", "createTime", "updateTime", "deleted",
                "creator", "updater");
        assertFalse(DateUtils.isExpired(newAccessTokenEntity.getExpiresTime()));
        // assert new access token cache
        OAuth2AccessTokenEntity redisAccessTokenEntity = oauth2AccessTokenRedisRepository.get(newAccessTokenEntity.getAccessToken());
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(newAccessTokenEntity, redisAccessTokenEntity, "expiresTime", "createTime", "updateTime", "deleted");
    }

    @Test
    public void testGetAccessToken() {
        // mock data（access token）
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2AccessTokenMapper.insert(accessTokenEntity);
        // prepare parameters
        String accessToken = accessTokenEntity.getAccessToken();

        // invoke
        OAuth2AccessTokenEntity result = oauth2TokenService.getAccessToken(accessToken);
        // assert
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(accessTokenEntity, result, "expiresTime", "createTime", "updateTime", "deleted",
                "creator", "updater");
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(accessTokenEntity, oauth2AccessTokenRedisRepository.get(accessToken), "expiresTime", "createTime", "updateTime", "deleted",
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
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class)
                .setExpiresTime(LocalDateTime.now().minusDays(1));
        oauth2AccessTokenMapper.insert(accessTokenEntity);
        // prepare parameters
        String accessToken = accessTokenEntity.getAccessToken();

        // invoke, and assert
        assertServiceException(() -> oauth2TokenService.checkAccessToken(accessToken),
                new ErrorCode(401, "Access token has expired"));
    }

    @Test
    public void testCheckAccessToken_refreshToken() {
        // mock data（access token）
        OAuth2RefreshTokenEntity refreshTokenEntity = randomPojo(OAuth2RefreshTokenEntity.class)
                .setUserId(0L)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2RefreshTokenMapper.insert(refreshTokenEntity);
        // prepare parameters
        String accessToken = refreshTokenEntity.getRefreshToken();

        // invoke, and assert
        OAuth2AccessTokenEntity result = oauth2TokenService.getAccessToken(accessToken);
        // assert
        assertPojoEquals(refreshTokenEntity, result, "expiresTime", "createTime", "updateTime", "deleted",
                "creator", "updater");
    }

    @Test
    public void testCheckAccessToken_success() {
        // mock data（access token）
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2AccessTokenMapper.insert(accessTokenEntity);
        // prepare parameters
        String accessToken = accessTokenEntity.getAccessToken();

        // invoke, and assert
        OAuth2AccessTokenEntity result = oauth2TokenService.getAccessToken(accessToken);
        // assert
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(accessTokenEntity, result, "expiresTime", "createTime", "updateTime", "deleted",
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
        OAuth2AccessTokenEntity accessTokenEntity = randomPojo(OAuth2AccessTokenEntity.class)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2AccessTokenMapper.insert(accessTokenEntity);
        // mock data（refresh token）
        OAuth2RefreshTokenEntity refreshTokenEntity = randomPojo(OAuth2RefreshTokenEntity.class)
                .setRefreshToken(accessTokenEntity.getRefreshToken());
        oauth2RefreshTokenMapper.insert(refreshTokenEntity);
        // invoke
        OAuth2AccessTokenEntity result = oauth2TokenService.removeAccessToken(accessTokenEntity.getAccessToken());
        // TODO:  expiresTime blocked, only reproducible on win11, follow-up fix recommended.
        assertPojoEquals(accessTokenEntity, result, "expiresTime", "createTime", "updateTime", "deleted",
                "creator", "updater");
        // assert data
        assertNull(oauth2AccessTokenMapper.selectByAccessToken(accessTokenEntity.getAccessToken()));
        assertNull(oauth2RefreshTokenMapper.selectByRefreshToken(accessTokenEntity.getRefreshToken()));
        assertNull(oauth2AccessTokenRedisRepository.get(accessTokenEntity.getAccessToken()));
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
