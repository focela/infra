package com.focela.platform.system.service.oauth2;

import cn.hutool.core.util.IdUtil;
import com.focela.platform.common.utils.date.DateUtils;
import com.focela.platform.system.entity.oauth2.OAuth2CodeEntity;
import com.focela.platform.system.repository.mapper.oauth2.OAuth2CodeMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.OAUTH2_CODE_EXPIRE;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.OAUTH2_CODE_NOT_EXISTS;

/**
 * OAuth2.0 Authorization Code Service implementation class
 */
@Service
@Validated
@RequiredArgsConstructor
public class DefaultOAuth2CodeService implements OAuth2CodeService {

    /**
     * Expiration time of the authorization code, default 5 minutes
     */
    private static final Integer TIMEOUT = 5 * 60;

        private final OAuth2CodeMapper oauth2CodeMapper;

    @Override
    public OAuth2CodeEntity createAuthorizationCode(Long userId, Integer userType, String clientId,
                                                List<String> scopes, String redirectUri, String state) {
        OAuth2CodeEntity codeDO = new OAuth2CodeEntity().setCode(generateCode())
                .setUserId(userId).setUserType(userType)
                .setClientId(clientId).setScopes(scopes)
                .setExpiresTime(LocalDateTime.now().plusSeconds(TIMEOUT))
                .setRedirectUri(redirectUri).setState(state);
        oauth2CodeMapper.insert(codeDO);
        return codeDO;
    }

    @Override
    public OAuth2CodeEntity consumeAuthorizationCode(String code) {
        OAuth2CodeEntity codeDO = oauth2CodeMapper.selectByCode(code);
        if (codeDO == null) {
            throw exception(OAUTH2_CODE_NOT_EXISTS);
        }
        if (DateUtils.isExpired(codeDO.getExpiresTime())) {
            throw exception(OAUTH2_CODE_EXPIRE);
        }
        oauth2CodeMapper.deleteById(codeDO.getId());
        return codeDO;
    }

    private static String generateCode() {
        return IdUtil.fastSimpleUUID();
    }

}
