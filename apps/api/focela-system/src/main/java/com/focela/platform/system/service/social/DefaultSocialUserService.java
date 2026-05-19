package com.focela.platform.system.service.social;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.focela.platform.common.exception.ServiceException;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.api.social.dto.SocialUserBindRpcRequest;
import com.focela.platform.system.api.social.dto.SocialUserRpcResponse;
import com.focela.platform.system.controller.admin.social.request.user.SocialUserPageRequest;
import com.focela.platform.system.domain.entity.social.SocialUserBindEntity;
import com.focela.platform.system.domain.entity.social.SocialUserEntity;
import com.focela.platform.system.repository.mapper.social.SocialUserBindMapper;
import com.focela.platform.system.repository.mapper.social.SocialUserMapper;
import com.focela.platform.system.enums.social.SocialTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.common.utils.collection.CollectionUtils.convertSet;
import static com.focela.platform.common.utils.json.JsonUtils.toJsonString;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.SOCIAL_USER_NOT_FOUND;

/**
 * Social user Service implementation class
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class DefaultSocialUserService implements SocialUserService {

        private final SocialUserBindMapper socialUserBindMapper;
        private final SocialUserMapper socialUserMapper;

        private final SocialClientService socialClientService;

    @Override
    public List<SocialUserEntity> getSocialUserList(Long userId, Integer userType) {
        // Get bindings
        List<SocialUserBindEntity> socialUserBinds = socialUserBindMapper.selectListByUserIdAndUserType(userId, userType);
        if (CollUtil.isEmpty(socialUserBinds)) {
            return Collections.emptyList();
        }
        // Get social users
        return socialUserMapper.selectByIds(convertSet(socialUserBinds, SocialUserBindEntity::getSocialUserId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String bindSocialUser(SocialUserBindRpcRequest request) {
        // Get the social user
        SocialUserEntity socialUser = authSocialUser(request.getSocialType(), request.getUserType(),
                request.getCode(), request.getState());
        Assert.notNull(socialUser, "Social user must not be null");

        // The social user may have previously been bound to another user; unbind it
        socialUserBindMapper.deleteByUserTypeAndSocialUserId(request.getUserType(), socialUser.getId());

        // The user may have already been bound to this social type; unbind it
        socialUserBindMapper.deleteByUserTypeAndUserIdAndSocialType(request.getUserType(), request.getUserId(),
                socialUser.getType());

        // Bind the currently logged-in social user
        SocialUserBindEntity socialUserBind = SocialUserBindEntity.builder()
                .userId(request.getUserId()).userType(request.getUserType())
                .socialUserId(socialUser.getId()).socialType(socialUser.getType()).build();
        socialUserBindMapper.insert(socialUserBind);
        return socialUser.getOpenid();
    }

    @Override
    public void unbindSocialUser(Long userId, Integer userType, Integer socialType, String openid) {
        // Get the SocialUserEntity for the given openid
        SocialUserEntity socialUser = socialUserMapper.selectByTypeAndOpenid(socialType, openid);
        if (socialUser == null) {
            throw exception(SOCIAL_USER_NOT_FOUND);
        }

        // Delete the corresponding social binding relationship
        socialUserBindMapper.deleteByUserTypeAndUserIdAndSocialType(userType, userId, socialUser.getType());
    }

    @Override
    public SocialUserRpcResponse getSocialUserByUserId(Integer userType, Long userId, Integer socialType) {
        // Get the bound user
        SocialUserBindEntity socialUserBind = socialUserBindMapper.selectByUserIdAndUserTypeAndSocialType(userId, userType, socialType);
        if (socialUserBind == null) {
            return null;
        }
        // Get the social user
        SocialUserEntity socialUser = socialUserMapper.selectById(socialUserBind.getSocialUserId());
        Assert.notNull(socialUser, "Social user must not be null");
        return new SocialUserRpcResponse(socialUser.getOpenid(), socialUser.getNickname(), socialUser.getAvatar(),
                socialUserBind.getUserId());
    }

    @Override
    public SocialUserRpcResponse getSocialUserByCode(Integer userType, Integer socialType, String code, String state) {
        // Get the social user
        SocialUserEntity socialUser = authSocialUser(socialType, userType, code, state);
        Assert.notNull(socialUser, "Social user must not be null");

        // Get the bound user
        SocialUserBindEntity socialUserBind = socialUserBindMapper.selectByUserTypeAndSocialUserId(userType,
                socialUser.getId());
        return new SocialUserRpcResponse(socialUser.getOpenid(), socialUser.getNickname(), socialUser.getAvatar(),
                socialUserBind != null ? socialUserBind.getUserId() : null);
    }

    /**
     * Authorize and obtain the corresponding social user.
     * If authorization fails, a {@link ServiceException} is thrown.
     *
     * @param socialType social platform type {@link SocialTypeEnum}
     * @param userType user type
     * @param code     authorization code
     * @param state    state
     * @return authorized user
     */
    @NotNull
    public SocialUserEntity authSocialUser(Integer socialType, Integer userType, String code, String state) {
        // Prefer fetching from DB because code is single-use.
        // During social login, when the user is not yet bound, a binding login is required, in which case code is used twice.
        SocialUserEntity socialUser = socialUserMapper.selectByTypeAndCodeAnState(socialType, code, state);
        if (socialUser != null) {
            return socialUser;
        }

        // Request to fetch
        AuthUser authUser = socialClientService.getAuthUser(socialType, userType, code, state);
        Assert.notNull(authUser, "Third-party user must not be null");

        // Save to DB
        socialUser = socialUserMapper.selectByTypeAndOpenid(socialType, authUser.getUuid());
        if (socialUser == null) {
            socialUser = new SocialUserEntity();
        }
        socialUser.setType(socialType).setCode(code).setState(state) // must save code + state for later querying
                .setOpenid(authUser.getUuid()).setToken(authUser.getToken().getAccessToken()).setRawTokenInfo((toJsonString(authUser.getToken())))
                .setNickname(authUser.getNickname()).setAvatar(authUser.getAvatar()).setRawUserInfo(toJsonString(authUser.getRawUserInfo()));
        if (socialUser.getId() == null) {
            socialUserMapper.insert(socialUser);
        } else {
            socialUser.clean(); // avoid updateTime not being refreshed
            socialUserMapper.updateById(socialUser);
        }
        return socialUser;
    }

    // ==================== Social user CRUD ====================

    @Override
    public SocialUserEntity getSocialUser(Long id) {
        return socialUserMapper.selectById(id);
    }

    @Override
    public PageResult<SocialUserEntity> getSocialUserPage(SocialUserPageRequest pageRequest) {
        return socialUserMapper.selectPage(pageRequest);
    }

}
