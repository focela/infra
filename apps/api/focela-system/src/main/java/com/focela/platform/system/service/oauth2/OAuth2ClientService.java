package com.focela.platform.system.service.oauth2;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.oauth2.dto.client.OAuth2ClientPageRequest;
import com.focela.platform.system.controller.admin.oauth2.dto.client.OAuth2ClientSaveRequest;
import com.focela.platform.system.entity.oauth2.OAuth2ClientEntity;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

/**
 * OAuth2.0 Client Service interface
 *
 * Functionally similar to JdbcClientDetailsService, provides client operations
 */
public interface OAuth2ClientService {

    /**
     * Create an OAuth2 client
     *
     * @param createRequest create information
     * @return ID
     */
    Long createOAuth2Client(@Valid OAuth2ClientSaveRequest createRequest);

    /**
     * Update an OAuth2 client
     *
     * @param updateRequest update information
     */
    void updateOAuth2Client(@Valid OAuth2ClientSaveRequest updateRequest);

    /**
     * Delete an OAuth2 client
     *
     * @param id ID
     */
    void deleteOAuth2Client(Long id);

    /**
     * Batch delete OAuth2 clients
     *
     * @param ids ID array
     */
    void deleteOAuth2ClientList(List<Long> ids);

    /**
     * Get an OAuth2 client
     *
     * @param id ID
     * @return OAuth2 client
     */
    OAuth2ClientEntity getOAuth2Client(Long id);

    /**
     * Get an OAuth2 client from cache
     *
     * @param clientId client ID
     * @return OAuth2 client
     */
    OAuth2ClientEntity getOAuth2ClientFromCache(String clientId);

    /**
     * Get the paginated OAuth2 clients
     *
     * @param pageRequest page query
     * @return paginated OAuth2 clients
     */
    PageResult<OAuth2ClientEntity> getOAuth2ClientPage(OAuth2ClientPageRequest pageRequest);

    /**
     * Validate whether a client is legitimate, from cache
     *
     * @return client
     */
    default OAuth2ClientEntity validOAuthClientFromCache(String clientId) {
        return validOAuthClientFromCache(clientId, null, null, null, null);
    }

    /**
     * Validate whether a client is legitimate, from cache
     *
     * Non-null fields will be validated
     *
     * @param clientId client ID
     * @param clientSecret client secret
     * @param authorizedGrantType grant type
     * @param scopes authorization scope
     * @param redirectUri redirect URI
     * @return client
     */
    OAuth2ClientEntity validOAuthClientFromCache(String clientId, String clientSecret, String authorizedGrantType,
                                             Collection<String> scopes, String redirectUri);

}
