package com.focela.platform.system.service.oauth2;

import com.focela.platform.system.domain.entity.oauth2.OAuth2ApproveEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 Approve Service interface
 *
 * Functionally similar to Spring Security OAuth's ApprovalStoreUserApprovalHandler,
 * records user approvals for specific clients to reduce manual confirmation.
 */
public interface OAuth2ApproveService {

    /**
     * Check whether the specified user's approval for the specified client and scopes has been granted
     *
     * Refer to the checkForPreApproval method in ApprovalStoreUserApprovalHandler
     *
     * @param userId user ID
     * @param userType user type
     * @param clientId client ID
     * @param requestedScopes authorization scope
     * @return whether authorization is granted
     */
    boolean checkForPreApproval(Long userId, Integer userType, String clientId, Collection<String> requestedScopes);

    /**
     * When a user initiates approval, compute the final result based on the scopes options
     *
     * @param userId user ID
     * @param userType user type
     * @param clientId client ID
     * @param requestedScopes authorization scope
     * @return whether authorization is granted
     */
    boolean updateAfterApproval(Long userId, Integer userType, String clientId, Map<String, Boolean> requestedScopes);

    /**
     * Get the user's approval list, excluding expired ones
     *
     * @param userId user ID
     * @param userType user type
     * @param clientId client ID
     * @return whether authorization is granted
     */
    List<OAuth2ApproveEntity> getApproveList(Long userId, Integer userType, String clientId);

}
