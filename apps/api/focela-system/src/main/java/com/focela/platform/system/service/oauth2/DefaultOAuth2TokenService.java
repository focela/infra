package com.focela.platform.system.service.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.exception.ServiceException;
import com.focela.platform.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.date.DateUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.security.core.LoginUser;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import com.focela.platform.tenant.core.utils.TenantUtils;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception0;
import static com.focela.platform.common.utils.collection.CollectionUtils.convertSet;

/**
 * OAuth2.0 Token Service implementation class
 */
@Service
@RequiredArgsConstructor
public class DefaultOAuth2TokenService implements OAuth2TokenService {

        private final OAuth2AccessTokenMapper oauth2AccessTokenMapper;
        private final OAuth2RefreshTokenMapper oauth2RefreshTokenMapper;

        private final OAuth2AccessTokenRedisRepository oauth2AccessTokenRedisDAO;

        private final OAuth2ClientService oauth2ClientService;
    @Resource
    @Lazy // lazy loading to avoid circular dependency
    private UserService adminUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OAuth2AccessTokenEntity createAccessToken(Long userId, Integer userType, String clientId, List<String> scopes) {
        OAuth2ClientEntity clientDO = oauth2ClientService.validateOAuthClientFromCache(clientId);
        // Create refresh token
        OAuth2RefreshTokenEntity refreshTokenDO = createOAuth2RefreshToken(userId, userType, clientDO, scopes);
        // Create access token
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    @Override
    @Transactional(noRollbackFor = ServiceException.class)
    public OAuth2AccessTokenEntity refreshAccessToken(String refreshToken, String clientId) {
        // Query the refresh token
        OAuth2RefreshTokenEntity refreshTokenDO = oauth2RefreshTokenMapper.selectByRefreshToken(refreshToken);
        if (refreshTokenDO == null) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "Invalid refresh token");
        }

        // Validate that the Client matches
        OAuth2ClientEntity clientDO = oauth2ClientService.validateOAuthClientFromCache(clientId);
        if (ObjectUtil.notEqual(clientId, refreshTokenDO.getClientId())) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "Refresh token client ID is incorrect");
        }

        // Remove the related access tokens
        List<OAuth2AccessTokenEntity> accessTokenDOs = oauth2AccessTokenMapper.selectListByRefreshToken(refreshToken);
        if (CollUtil.isNotEmpty(accessTokenDOs)) {
            oauth2AccessTokenMapper.deleteByIds(convertSet(accessTokenDOs, OAuth2AccessTokenEntity::getId));
            oauth2AccessTokenRedisDAO.deleteList(convertSet(accessTokenDOs, OAuth2AccessTokenEntity::getAccessToken));
        }

        // When expired, delete the refresh token
        if (DateUtils.isExpired(refreshTokenDO.getExpiresTime())) {
            oauth2RefreshTokenMapper.deleteById(refreshTokenDO.getId());
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "Refresh token has expired");
        }

        // Create access token
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    @Override
    public OAuth2AccessTokenEntity getAccessToken(String accessToken) {
        // Prefer fetching from Redis
        OAuth2AccessTokenEntity accessTokenDO = oauth2AccessTokenRedisDAO.get(accessToken);
        if (accessTokenDO != null) {
            return accessTokenDO;
        }

        // If not found, fetch the access token from MySQL
        accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        if (accessTokenDO == null) {
            // Special: fetch the refresh token from MySQL. Reason: handle scenarios where refreshing the access token is inconvenient.
            // For example, JimuReport only allows passing token and not refresh_token, so the access token cannot be refreshed.
            // Another example: the frontend WebSocket token is passed directly in the URL and cannot pass refresh_token.
            OAuth2RefreshTokenEntity refreshTokenDO = oauth2RefreshTokenMapper.selectByRefreshToken(accessToken);
            if (refreshTokenDO != null && !DateUtils.isExpired(refreshTokenDO.getExpiresTime())) {
                accessTokenDO = convertToAccessToken(refreshTokenDO);
            }
        }

        // If it exists in MySQL, write it back to Redis
        if (accessTokenDO != null && !DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            oauth2AccessTokenRedisDAO.set(accessTokenDO);
        }
        return accessTokenDO;
    }

    @Override
    public OAuth2AccessTokenEntity checkAccessToken(String accessToken) {
        OAuth2AccessTokenEntity accessTokenDO = getAccessToken(accessToken);
        if (accessTokenDO == null) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "Access token does not exist");
        }
        if (DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "Access token has expired");
        }
        return accessTokenDO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OAuth2AccessTokenEntity removeAccessToken(String accessToken) {
        // Delete the access token
        OAuth2AccessTokenEntity accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        if (accessTokenDO == null) {
            return null;
        }
        oauth2AccessTokenMapper.deleteById(accessTokenDO.getId());
        oauth2AccessTokenRedisDAO.delete(accessToken);
        // Delete the refresh token
        oauth2RefreshTokenMapper.deleteByRefreshToken(accessTokenDO.getRefreshToken());
        return accessTokenDO;
    }

    @Override
    public void removeAccessToken(Long userId, Integer userType) {
        List<OAuth2AccessTokenEntity> accessTokens = oauth2AccessTokenMapper.selectListByUserIdAndUserType(userId, userType);
        if (CollUtil.isEmpty(accessTokens)) {
            return;
        }
        accessTokens.forEach(accessToken -> {
            // Delete the access token
            oauth2AccessTokenMapper.deleteById(accessToken.getId());
            oauth2AccessTokenRedisDAO.delete(accessToken.getAccessToken());
            // Delete the refresh token
            oauth2RefreshTokenMapper.deleteByRefreshToken(accessToken.getRefreshToken());
        });
    }

    @Override
    public PageResult<OAuth2AccessTokenEntity> getAccessTokenPage(OAuth2AccessTokenPageRequest request) {
        return oauth2AccessTokenMapper.selectPage(request);
    }

    private OAuth2AccessTokenEntity createOAuth2AccessToken(OAuth2RefreshTokenEntity refreshTokenDO, OAuth2ClientEntity clientDO) {
        OAuth2AccessTokenEntity accessTokenDO = new OAuth2AccessTokenEntity().setAccessToken(generateAccessToken())
                .setUserId(refreshTokenDO.getUserId()).setUserType(refreshTokenDO.getUserType())
                .setUserInfo(buildUserInfo(refreshTokenDO.getUserId(), refreshTokenDO.getUserType()))
                .setClientId(clientDO.getClientId()).setScopes(refreshTokenDO.getScopes())
                .setRefreshToken(refreshTokenDO.getRefreshToken())
                .setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getAccessTokenValiditySeconds()));
        // Prefer obtaining the tenant ID from refreshToken to avoid tenantId being null when ThreadLocal is polluted
        // Possible related issue: https://t.zsxq.com/JIi5G
        Long tenantId = refreshTokenDO.getTenantId();
        if (tenantId == null) {
            tenantId = TenantContextHolder.getTenantId();
        }
        accessTokenDO.setTenantId(tenantId);
        oauth2AccessTokenMapper.insert(accessTokenDO);
        // Record into Redis
        oauth2AccessTokenRedisDAO.set(accessTokenDO);
        return accessTokenDO;
    }

    private OAuth2RefreshTokenEntity createOAuth2RefreshToken(Long userId, Integer userType, OAuth2ClientEntity clientDO, List<String> scopes) {
        OAuth2RefreshTokenEntity refreshToken = new OAuth2RefreshTokenEntity().setRefreshToken(generateRefreshToken())
                .setUserId(userId).setUserType(userType)
                .setClientId(clientDO.getClientId()).setScopes(scopes)
                .setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getRefreshTokenValiditySeconds()));
        oauth2RefreshTokenMapper.insert(refreshToken);
        return refreshToken;
    }

    private OAuth2AccessTokenEntity convertToAccessToken(OAuth2RefreshTokenEntity refreshTokenDO) {
        OAuth2AccessTokenEntity accessTokenDO = BeanUtils.toBean(refreshTokenDO, OAuth2AccessTokenEntity.class)
                .setAccessToken(refreshTokenDO.getRefreshToken());
        TenantUtils.execute(refreshTokenDO.getTenantId(),
                        () -> accessTokenDO.setUserInfo(buildUserInfo(refreshTokenDO.getUserId(), refreshTokenDO.getUserType())));
        return accessTokenDO;
    }

    /**
     * Load user information so {@link com.focela.platform.security.core.LoginUser} can access nickname, department, etc.
     *
     * @param userId user ID
     * @param userType user type
     * @return user information
     */
    private Map<String, String> buildUserInfo(Long userId, Integer userType) {
        if (userId == null || userId <= 0) {
            return Collections.emptyMap();
        }
        if (userType.equals(UserTypeEnum.ADMIN.getValue())) {
            UserEntity user = adminUserService.getUser(userId);
            return MapUtil.builder(LoginUser.INFO_KEY_NICKNAME, user.getNickname())
                    .put(LoginUser.INFO_KEY_DEPT_ID, StrUtil.toStringOrNull(user.getDeptId())).build();
        } else if (userType.equals(UserTypeEnum.MEMBER.getValue())) {
            // Note: currently Member is not read, can be implemented on demand
            return Collections.emptyMap();
        }
        throw new IllegalArgumentException("unknown user type:" + userType);
    }

    private static String generateAccessToken() {
        return IdUtil.fastSimpleUUID();
    }

    private static String generateRefreshToken() {
        return IdUtil.fastSimpleUUID();
    }

}
