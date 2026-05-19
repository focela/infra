package com.focela.platform.system.service.social;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.http.HttpUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.social.request.client.SocialClientPageRequest;
import com.focela.platform.system.controller.admin.social.request.client.SocialClientSaveRequest;
import com.focela.platform.system.enums.social.SocialTypeEnum;
import com.focela.platform.system.config.justauth.AuthRequestFactory;
import com.focela.platform.system.domain.entity.social.SocialClientEntity;
import com.focela.platform.system.repository.mapper.social.SocialClientMapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.common.utils.json.JsonUtils.toJsonString;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;

/**
 * Social client service implementation
 */
@Service
@Slf4j
public class DefaultSocialClientService implements SocialClientService {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private AuthRequestFactory authRequestFactory;

    @Resource
    private SocialClientMapper socialClientMapper;

    @Override
    public String getAuthorizeUrl(Integer socialType, Integer userType, String redirectUri) {
        AuthRequest authRequest = buildAuthRequest(socialType, userType);
        String authorizeUri = authRequest.authorize(AuthStateUtils.createState());
        return HttpUtils.replaceUrlQuery(authorizeUri, "redirect_uri", redirectUri);
    }

    @Override
    public AuthUser getAuthUser(Integer socialType, Integer userType, String code, String state) {
        AuthRequest authRequest = buildAuthRequest(socialType, userType);
        AuthCallback authCallback = AuthCallback.builder().code(code).auth_code(code).state(state).build();
        AuthResponse<?> authResponse = authRequest.login(authCallback);
        log.info("[getAuthUser][social type({}) request({}) response({})]", socialType,
                toJsonString(authCallback), toJsonString(authResponse));
        if (!authResponse.ok()) {
            throw exception(SOCIAL_USER_AUTH_FAILURE, authResponse.getMsg());
        }
        return (AuthUser) authResponse.getData();
    }

    @VisibleForTesting
    AuthRequest buildAuthRequest(Integer socialType, Integer userType) {
        AuthRequest request = authRequestFactory.get(SocialTypeEnum.valueOfType(socialType).getSource());
        Assert.notNull(request, String.format("Social platform (%d) not found", socialType));
        SocialClientEntity client = socialClientMapper.selectBySocialTypeAndUserType(socialType, userType);
        if (client != null && Objects.equals(client.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
            AuthConfig authConfig = (AuthConfig) ReflectUtil.getFieldValue(request, "config");
            AuthConfig newAuthConfig = ReflectUtil.newInstance(authConfig.getClass());
            BeanUtil.copyProperties(authConfig, newAuthConfig);
            newAuthConfig.setClientId(client.getClientId());
            newAuthConfig.setClientSecret(client.getClientSecret());
            if (client.getAgentId() != null) {
                newAuthConfig.setAgentId(client.getAgentId());
            }
            ReflectUtil.setFieldValue(request, "config", newAuthConfig);
        }
        return request;
    }

    // =================== Client management ===================

    @Override
    public Long createSocialClient(SocialClientSaveRequest createRequest) {
        validateSocialClientUnique(null, createRequest.getUserType(), createRequest.getSocialType());
        SocialClientEntity client = BeanUtils.toBean(createRequest, SocialClientEntity.class);
        socialClientMapper.insert(client);
        return client.getId();
    }

    @Override
    public void updateSocialClient(SocialClientSaveRequest updateRequest) {
        validateSocialClientExists(updateRequest.getId());
        validateSocialClientUnique(updateRequest.getId(), updateRequest.getUserType(), updateRequest.getSocialType());
        SocialClientEntity updateObj = BeanUtils.toBean(updateRequest, SocialClientEntity.class);
        socialClientMapper.updateById(updateObj);
    }

    @Override
    public void deleteSocialClient(Long id) {
        validateSocialClientExists(id);
        socialClientMapper.deleteById(id);
    }

    @Override
    public void deleteSocialClientList(List<Long> ids) {
        socialClientMapper.deleteByIds(ids);
    }

    private void validateSocialClientExists(Long id) {
        if (socialClientMapper.selectById(id) == null) {
            throw exception(SOCIAL_CLIENT_NOT_EXISTS);
        }
    }

    private void validateSocialClientUnique(Long id, Integer userType, Integer socialType) {
        SocialClientEntity client = socialClientMapper.selectBySocialTypeAndUserType(socialType, userType);
        if (client == null) {
            return;
        }
        if (id == null || ObjUtil.notEqual(id, client.getId())) {
            throw exception(SOCIAL_CLIENT_UNIQUE);
        }
    }

    @Override
    public SocialClientEntity getSocialClient(Long id) {
        return socialClientMapper.selectById(id);
    }

    @Override
    public PageResult<SocialClientEntity> getSocialClientPage(SocialClientPageRequest pageRequest) {
        return socialClientMapper.selectPage(pageRequest);
    }

}
