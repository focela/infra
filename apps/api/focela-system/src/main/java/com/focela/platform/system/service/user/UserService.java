package com.focela.platform.system.service.user;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.system.controller.admin.auth.dto.AuthRegisterRequest;
import com.focela.platform.system.controller.admin.user.dto.profile.UserProfileUpdatePasswordRequest;
import com.focela.platform.system.controller.admin.user.dto.profile.UserProfileUpdateRequest;
import com.focela.platform.system.controller.admin.user.dto.UserImportExcelRow;
import com.focela.platform.system.controller.admin.user.dto.UserImportResponse;
import com.focela.platform.system.controller.admin.user.dto.UserPageRequest;
import com.focela.platform.system.controller.admin.user.dto.UserSaveRequest;
import com.focela.platform.system.domain.entity.user.UserEntity;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin User Service interface
 */
public interface UserService {

    /**
     * Create a user
     *
     * @param createRequest user information
     * @return user ID
     */
    Long createUser(@Valid UserSaveRequest createRequest);

    /**
     * Register a user
     *
     * @param registerRequest user information
     * @return user ID
     */
    Long registerUser(@Valid AuthRegisterRequest registerRequest);

    /**
     * Update a user
     *
     * @param updateRequest user information
     */
    void updateUser(@Valid UserSaveRequest updateRequest);

    /**
     * Update the last login info of a user
     *
     * @param id user ID
     * @param loginIp login IP
     */
    void updateUserLogin(Long id, String loginIp);

    /**
     * Update a user's personal profile
     *
     * @param id user ID
     * @param request user profile information
     */
    void updateUserProfile(Long id, @Valid UserProfileUpdateRequest request);

    /**
     * Update a user's personal password
     *
     * @param id user ID
     * @param request update user personal password
     */
    void updateUserPassword(Long id, @Valid UserProfileUpdatePasswordRequest request);

    /**
     * Update password
     *
     * @param id       user ID
     * @param password password
     */
    void updateUserPassword(Long id, String password);

    /**
     * Update status
     *
     * @param id     user ID
     * @param status status
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * Delete a user
     *
     * @param id user ID
     */
    void deleteUser(Long id);

    /**
     * Batch delete users
     *
     * @param ids user ID array
     */
    void deleteUserList(List<Long> ids);

    /**
     * Get a user by username
     *
     * @param username username
     * @return user object information
     */
    UserEntity getUserByUsername(String username);

    /**
     * Get a user by mobile number
     *
     * @param mobile mobile number
     * @return user object information
     */
    UserEntity getUserByMobile(String mobile);

    /**
     * Get the paginated user list
     *
     * @param request page query parameters
     * @return paginated list
     */
    PageResult<UserEntity> getUserPage(UserPageRequest request);

    /**
     * Get a user by user ID
     *
     * @param id user ID
     * @return user object information
     */
    UserEntity getUser(Long id);

    /**
     * Get the user list for specified departments
     *
     * @param deptIds department ID array
     * @return user array
     */
    List<UserEntity> getUserListByDeptIds(Collection<Long> deptIds);

    /**
     * Get the user list for specified posts
     *
     * @param postIds post ID array
     * @return user array
     */
    List<UserEntity> getUserListByPostIds(Collection<Long> postIds);

    /**
     * Get the user list
     *
     * @param ids user ID array
     * @return user list
     */
    List<UserEntity> getUserList(Collection<Long> ids);

    /**
     * Validate whether the users are valid. The following are considered invalid:
     * 1. User ID does not exist
     * 2. User is disabled
     *
     * @param ids user ID array
     */
    void validateUserList(Collection<Long> ids);

    /**
     * Get a user Map
     *
     * @param ids user ID array
     * @return user Map
     */
    default Map<Long, UserEntity> getUserMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        return CollectionUtils.convertMap(getUserList(ids), UserEntity::getId);
    }

    /**
     * Get the user list by fuzzy matching nickname
     *
     * @param nickname nickname
     * @return user list
     */
    List<UserEntity> getUserListByNickname(String nickname);

    /**
     * Batch import users
     *
     * @param importUsers     user list to import
     * @param isUpdateSupport whether update is supported
     * @return import result
     */
    UserImportResponse importUserList(List<UserImportExcelRow> importUsers, boolean isUpdateSupport);

    /**
     * Get users by status
     *
     * @param status status
     * @return users
     */
    List<UserEntity> getUserListByStatus(Integer status);

    /**
     * Determine whether the password matches
     *
     * @param rawPassword the raw password
     * @param encodedPassword the encoded password
     * @return whether it matches
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);

}
