package com.focela.platform.system.api.social;

import com.focela.platform.common.exception.ServiceException;
import com.focela.platform.system.api.social.dto.SocialUserBindRpcRequest;
import com.focela.platform.system.api.social.dto.SocialUserRpcResponse;
import com.focela.platform.system.api.social.dto.SocialUserUnbindRpcRequest;
import jakarta.validation.Valid;

/**
 * Social user API interface
 */
public interface SocialUserApi {

    /**
     * Bind a social user
     *
     * @param request bind info
     * @return social user openid
     */
    String bindSocialUser(@Valid SocialUserBindRpcRequest request);

    /**
     * Unbind a social user
     *
     * @param request unbind request
     */
    void unbindSocialUser(@Valid SocialUserUnbindRpcRequest request);

    /**
     * Get a social user by userId
     *
     * @param userType   user type
     * @param userId     user ID
     * @param socialType social platform type
     * @return social user
     */
    SocialUserRpcResponse getSocialUserByUserId(Integer userType, Long userId, Integer socialType);

    /**
     * Get a social user
     *
     * When the authentication info is incorrect, a {@link ServiceException} business exception will be thrown
     *
     * @param userType   user type
     * @param socialType social platform type
     * @param code       authorization code
     * @param state      state
     * @return social user
     */
    SocialUserRpcResponse getSocialUserByCode(Integer userType, Integer socialType, String code, String state);

}
