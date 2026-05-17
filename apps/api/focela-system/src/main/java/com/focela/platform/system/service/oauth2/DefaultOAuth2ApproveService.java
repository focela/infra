package com.focela.platform.system.service.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.focela.platform.common.utils.date.DateUtils;
import com.focela.platform.system.entity.oauth2.OAuth2ApproveEntity;
import com.focela.platform.system.entity.oauth2.OAuth2ClientEntity;
import com.focela.platform.system.repository.mapper.oauth2.OAuth2ApproveMapper;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

import static com.focela.platform.common.utils.collection.CollectionUtils.convertSet;

/**
 * OAuth2 Approve Service implementation class
 */
@Service
@Validated
@RequiredArgsConstructor
public class DefaultOAuth2ApproveService implements OAuth2ApproveService {

    /**
     * Expiration time of an approval, default 30 days
     */
    private static final Integer TIMEOUT = 30 * 24 * 60 * 60; // unit: seconds

        private final OAuth2ClientService oauth2ClientService;

        private final OAuth2ApproveMapper oauth2ApproveMapper;

    @Override
    @Transactional
    public boolean checkForPreApproval(Long userId, Integer userType, String clientId, Collection<String> requestedScopes) {
        // Step 1: based on the client's auto-approve calculation; if all scopes are in auto-approve, return true (approved)
        OAuth2ClientEntity clientEntity = oauth2ClientService.validateOAuthClientFromCache(clientId);
        Assert.notNull(clientEntity, "Client must not be blank"); // defensive programming
        if (CollUtil.containsAll(clientEntity.getAutoApproveScopes(), requestedScopes)) {
            // gh-877 - if all scopes are auto approved, approvals still need to be added to the approval store.
            LocalDateTime expireTime = LocalDateTime.now().plusSeconds(TIMEOUT);
            for (String scope : requestedScopes) {
                saveApprove(userId, userType, clientId, scope, true, expireTime);
            }
            return true;
        }

        // Step 2: include approvals already granted by the user. If all scopes are contained, return true
        List<OAuth2ApproveEntity> approvals = getApproveList(userId, userType, clientId);
        Set<String> scopes = convertSet(approvals, OAuth2ApproveEntity::getScope,
                OAuth2ApproveEntity::getApproved); // keep only non-expired + approved
        return CollUtil.containsAll(scopes, requestedScopes);
    }

    @Override
    @Transactional
    public boolean updateAfterApproval(Long userId, Integer userType, String clientId, Map<String, Boolean> requestedScopes) {
        // If requestedScopes is empty, there is no request, return true (approved)
        if (CollUtil.isEmpty(requestedScopes)) {
            return true;
        }

        // Update approval information
        boolean success = false; // need at least one approval
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(TIMEOUT);
        for (Map.Entry<String, Boolean> entry : requestedScopes.entrySet()) {
            if (entry.getValue()) {
                success = true;
            }
            saveApprove(userId, userType, clientId, entry.getKey(), entry.getValue(), expireTime);
        }
        return success;
    }

    @Override
    public List<OAuth2ApproveEntity> getApproveList(Long userId, Integer userType, String clientId) {
        List<OAuth2ApproveEntity> approvals = oauth2ApproveMapper.selectListByUserIdAndUserTypeAndClientId(
                userId, userType, clientId);
        approvals.removeIf(o -> DateUtils.isExpired(o.getExpiresTime()));
        return approvals;
    }

    @VisibleForTesting
    void saveApprove(Long userId, Integer userType, String clientId,
                     String scope, Boolean approved, LocalDateTime expireTime) {
        // Try update first
        OAuth2ApproveEntity approval = new OAuth2ApproveEntity().setUserId(userId).setUserType(userType)
                .setClientId(clientId).setScope(scope).setApproved(approved).setExpiresTime(expireTime);
        if (oauth2ApproveMapper.update(approval) == 1) {
            return;
        }
        // On failure it means it does not exist, so insert it
        oauth2ApproveMapper.insert(approval);
    }

}
