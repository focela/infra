package com.focela.platform.system.api.social;

import com.focela.platform.system.api.social.dto.SocialUserBindRpcRequest;
import com.focela.platform.system.api.social.dto.SocialUserRpcResponse;
import com.focela.platform.system.api.social.dto.SocialUserUnbindRpcRequest;
import com.focela.platform.system.service.social.SocialUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Social user API implementation class
 */
@Service
@Validated
@RequiredArgsConstructor
public class LocalSocialUserApi implements SocialUserApi {

    private final SocialUserService socialUserService;

    @Override
    public String bindSocialUser(SocialUserBindRpcRequest request) {
        return socialUserService.bindSocialUser(request);
    }

    @Override
    public void unbindSocialUser(SocialUserUnbindRpcRequest request) {
        socialUserService.unbindSocialUser(request.getUserId(), request.getUserType(),
                request.getSocialType(), request.getOpenid());
    }

    @Override
    public SocialUserRpcResponse getSocialUserByUserId(Integer userType, Long userId, Integer socialType) {
        return socialUserService.getSocialUserByUserId(userType, userId, socialType);
    }

    @Override
    public SocialUserRpcResponse getSocialUserByCode(Integer userType, Integer socialType, String code, String state) {
       return socialUserService.getSocialUserByCode(userType, socialType, code, state);
    }

}
