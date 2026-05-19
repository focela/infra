package com.focela.platform.system.service.social;

import com.focela.platform.common.exception.ServiceException;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.api.social.dto.SocialUserBindRpcRequest;
import com.focela.platform.system.api.social.dto.SocialUserRpcResponse;
import com.focela.platform.system.controller.admin.social.dto.user.SocialUserPageRequest;
import com.focela.platform.system.domain.entity.social.SocialUserEntity;
import com.focela.platform.system.enums.social.SocialTypeEnum;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Social user Service interface, e.g. social platform authorization login
 */
public interface SocialUserService {

    /**
     * Get the social user list for the specified user
     *
     * @param userId   user ID
     * @param userType user type
     * @return social user list
     */
    List<SocialUserEntity> getSocialUserList(Long userId, Integer userType);

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
     * @param userId user ID
     * @param userType global user type
     * @param socialType social platform type {@link SocialTypeEnum}
     * @param openid social platform openid
     */
    void unbindSocialUser(Long userId, Integer userType, Integer socialType, String openid);

    /**
     * Get social user by userId
     *
     * @param userType user type
     * @param userId user ID
     * @param socialType social platform type
     * @return social user
     */
    SocialUserRpcResponse getSocialUserByUserId(Integer userType, Long userId, Integer socialType);

    /**
     * Get social user.
     *
     * If authentication info is incorrect, a {@link ServiceException} business exception will also be thrown.
     *
     * @param userType user type
     * @param socialType social platform type
     * @param code authorization code
     * @param state state
     * @return social user
     */
    SocialUserRpcResponse getSocialUserByCode(Integer userType, Integer socialType, String code, String state);

    // ==================== Social user CRUD ====================

    /**
     * Get social user
     *
     * @param id ID
     * @return social user
     */
    SocialUserEntity getSocialUser(Long id);

    /**
     * Get paginated social users
     *
     * @param pageRequest pagination query
     * @return paginated social users
     */
    PageResult<SocialUserEntity> getSocialUserPage(SocialUserPageRequest pageRequest);

}
