package com.focela.platform.module.system.service.oauth2;

import cn.hutool.core.util.IdUtil;
import com.focela.platform.framework.common.util.date.DateUtils;
import com.focela.platform.module.system.repository.entity.oauth2.OAuth2CodeEntity;
import com.focela.platform.module.system.repository.mapper.oauth2.OAuth2CodeMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static com.focela.platform.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.OAUTH2_CODE_EXPIRE;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.OAUTH2_CODE_NOT_EXISTS;

/**
 * OAuth2.0 授权码 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class OAuth2CodeServiceImpl implements OAuth2CodeService {

    /**
     * 授权码的过期时间，默认 5 分钟
     */
    private static final Integer TIMEOUT = 5 * 60;

    @Resource
    private OAuth2CodeMapper oauth2CodeMapper;

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
