package com.focela.platform.system.service.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.common.utils.string.StrUtils;
import com.focela.platform.system.controller.admin.oauth2.dto.client.OAuth2ClientPageRequest;
import com.focela.platform.system.controller.admin.oauth2.dto.client.OAuth2ClientSaveRequest;
import com.focela.platform.system.entity.oauth2.OAuth2ClientEntity;
import com.focela.platform.system.repository.mapper.oauth2.OAuth2ClientMapper;
import com.focela.platform.system.constants.RedisKeyConstants;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.ErrorCodeConstants.*;

/**
 * OAuth2.0 Client Service implementation class
 */
@Service
@Validated
@Slf4j
public class DefaultOAuth2ClientService implements OAuth2ClientService {

    @Resource
    private OAuth2ClientMapper oauth2ClientMapper;

    @Override
    public Long createOAuth2Client(OAuth2ClientSaveRequest createRequest) {
        validateClientIdExists(null, createRequest.getClientId());
        // Insert
        OAuth2ClientEntity client = BeanUtils.toBean(createRequest, OAuth2ClientEntity.class);
        oauth2ClientMapper.insert(client);
        return client.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.OAUTH_CLIENT,
            allEntries = true) // allEntries clears all caches because the clientId field may be modified and is hard to clean up
    public void updateOAuth2Client(OAuth2ClientSaveRequest updateRequest) {
        // Validate existence
        validateOAuth2ClientExists(updateRequest.getId());
        // Validate Client is not occupied
        validateClientIdExists(updateRequest.getId(), updateRequest.getClientId());

        // Update
        OAuth2ClientEntity updateObj = BeanUtils.toBean(updateRequest, OAuth2ClientEntity.class);
        oauth2ClientMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.OAUTH_CLIENT,
            allEntries = true) // allEntries clears all caches because id is not the direct cache key and is hard to clean up
    public void deleteOAuth2Client(Long id) {
        // Validate existence
        validateOAuth2ClientExists(id);
        // Delete
        oauth2ClientMapper.deleteById(id);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.OAUTH_CLIENT,
            allEntries = true) // allEntries clears all caches because id is not the direct cache key and is hard to clean up
    public void deleteOAuth2ClientList(List<Long> ids) {
        oauth2ClientMapper.deleteByIds(ids);
    }

    private void validateOAuth2ClientExists(Long id) {
        if (oauth2ClientMapper.selectById(id) == null) {
            throw exception(OAUTH2_CLIENT_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    void validateClientIdExists(Long id, String clientId) {
        OAuth2ClientEntity client = oauth2ClientMapper.selectByClientId(clientId);
        if (client == null) {
            return;
        }
        // If id is null, no need to compare whether it is a client with the same id
        if (id == null) {
            throw exception(OAUTH2_CLIENT_EXISTS);
        }
        if (!client.getId().equals(id)) {
            throw exception(OAUTH2_CLIENT_EXISTS);
        }
    }

    @Override
    public OAuth2ClientEntity getOAuth2Client(Long id) {
        return oauth2ClientMapper.selectById(id);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.OAUTH_CLIENT, key = "#clientId",
            unless = "#result == null")
    public OAuth2ClientEntity getOAuth2ClientFromCache(String clientId) {
        return oauth2ClientMapper.selectByClientId(clientId);
    }

    @Override
    public PageResult<OAuth2ClientEntity> getOAuth2ClientPage(OAuth2ClientPageRequest pageRequest) {
        return oauth2ClientMapper.selectPage(pageRequest);
    }

    @Override
    public OAuth2ClientEntity validOAuthClientFromCache(String clientId, String clientSecret, String authorizedGrantType,
                                                    Collection<String> scopes, String redirectUri) {
        // Validate that the client exists and is enabled
        OAuth2ClientEntity client = getSelf().getOAuth2ClientFromCache(clientId);
        if (client == null) {
            throw exception(OAUTH2_CLIENT_NOT_EXISTS);
        }
        if (CommonStatusEnum.isDisable(client.getStatus())) {
            throw exception(OAUTH2_CLIENT_DISABLE);
        }

        // Validate the client secret
        if (StrUtil.isNotEmpty(clientSecret) && ObjectUtil.notEqual(client.getSecret(), clientSecret)) {
            throw exception(OAUTH2_CLIENT_CLIENT_SECRET_ERROR);
        }
        // Validate the grant type
        if (StrUtil.isNotEmpty(authorizedGrantType) && !CollUtil.contains(client.getAuthorizedGrantTypes(), authorizedGrantType)) {
            throw exception(OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS);
        }
        // Validate the authorization scope
        if (CollUtil.isNotEmpty(scopes) && !CollUtil.containsAll(client.getScopes(), scopes)) {
            throw exception(OAUTH2_CLIENT_SCOPE_OVER);
        }
        // Validate the redirect URI
        if (StrUtil.isNotEmpty(redirectUri) && !StrUtils.startWithAny(redirectUri, client.getRedirectUris())) {
            throw exception(OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH, redirectUri);
        }
        return client;
    }

    /**
     * Get the self proxy object to ensure AOP takes effect
     *
     * @return self
     */
    private DefaultOAuth2ClientService getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
