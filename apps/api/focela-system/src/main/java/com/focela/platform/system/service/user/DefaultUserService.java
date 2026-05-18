package com.focela.platform.system.service.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.exception.ServiceException;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.common.utils.validation.ValidationUtils;
import com.focela.platform.datapermission.core.utils.DataPermissionUtils;
import com.focela.platform.infra.api.config.ConfigApi;
import com.focela.platform.system.controller.admin.auth.dto.AuthRegisterRequest;
import com.focela.platform.system.controller.admin.user.dto.profile.UserProfileUpdatePasswordRequest;
import com.focela.platform.system.controller.admin.user.dto.profile.UserProfileUpdateRequest;
import com.focela.platform.system.controller.admin.user.dto.UserImportExcelDto;
import com.focela.platform.system.controller.admin.user.dto.UserImportResponse;
import com.focela.platform.system.controller.admin.user.dto.UserPageRequest;
import com.focela.platform.system.controller.admin.user.dto.UserSaveRequest;
import com.focela.platform.system.entity.department.DepartmentEntity;
import com.focela.platform.system.entity.department.UserPostEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.focela.platform.system.repository.mapper.department.UserPostMapper;
import com.focela.platform.system.repository.mapper.user.UserMapper;
import com.focela.platform.system.service.department.DepartmentService;
import com.focela.platform.system.service.department.PostService;
import com.focela.platform.system.service.oauth2.OAuth2TokenService;
import com.focela.platform.system.service.permission.PermissionService;
import com.focela.platform.system.service.tenant.TenantService;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.common.utils.collection.CollectionUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static com.focela.platform.system.constants.LogRecordConstants.*;

/**
 * Admin User Service implementation class
 */
@Service("adminUserService")
@Slf4j
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    static final String USER_INIT_PASSWORD_KEY = "system.user.init-password";

    static final String USER_REGISTER_ENABLED_KEY = "system.user.register-enabled";

        private final UserMapper userMapper;

        private final DepartmentService deptService;
        private final PostService postService;
        private final PermissionService permissionService;
        private final PasswordEncoder passwordEncoder;
    @Resource
    @Lazy // lazy loading to avoid circular dependency errors
    private TenantService tenantService;
    @Resource
    @Lazy // lazy loading to avoid circular dependency
    private OAuth2TokenService oauth2TokenService;

        private final UserPostMapper userPostMapper;

        private final ConfigApi configApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_CREATE_SUB_TYPE, bizNo = "{{#user.id}}",
            success = SYSTEM_USER_CREATE_SUCCESS)
    public Long createUser(UserSaveRequest createRequest) {
        // 1.1 Validate the account count
        tenantService.handleTenantInfo(tenant -> {
            long count = userMapper.selectCount();
            if (count >= tenant.getAccountCount()) {
                throw exception(USER_COUNT_MAX, tenant.getAccountCount());
            }
        });
        // 1.2 Validate
        validateUserForCreateOrUpdate(null, createRequest.getUsername(),
                createRequest.getMobile(), createRequest.getEmail(), createRequest.getDeptId(), createRequest.getPostIds());
        // 2.1 Insert user
        UserEntity user = BeanUtils.toBean(createRequest, UserEntity.class);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // enabled by default
        user.setPassword(encodePassword(createRequest.getPassword())); // encode password
        userMapper.insert(user);
        // 2.2 Insert associated posts
        if (CollectionUtil.isNotEmpty(user.getPostIds())) {
            userPostMapper.insertBatch(convertList(user.getPostIds(),
                    postId -> new UserPostEntity().setUserId(user.getId()).setPostId(postId)));
        }

        // 3. Record operation log context
        LogRecordContext.putVariable("user", user);
        return user.getId();
    }

    @Override
    public Long registerUser(AuthRegisterRequest registerRequest) {
        // 1.1 Validate whether registration is enabled
        if (ObjUtil.notEqual(configApi.getConfigValueByKey(USER_REGISTER_ENABLED_KEY), "true")) {
            throw exception(USER_REGISTER_DISABLED);
        }
        // 1.2 Validate the account count
        tenantService.handleTenantInfo(tenant -> {
            long count = userMapper.selectCount();
            if (count >= tenant.getAccountCount()) {
                throw exception(USER_COUNT_MAX, tenant.getAccountCount());
            }
        });
        // 1.3 Validate
        validateUserForCreateOrUpdate(null, registerRequest.getUsername(), null, null, null, null);

        // 2. Insert user
        UserEntity user = BeanUtils.toBean(registerRequest, UserEntity.class);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // enabled by default
        user.setPassword(encodePassword(registerRequest.getPassword())); // encode password
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_UPDATE_SUB_TYPE, bizNo = "{{#updateRequest.id}}",
            success = SYSTEM_USER_UPDATE_SUCCESS)
    public void updateUser(UserSaveRequest updateRequest) {
        updateRequest.setPassword(null); // Special: password is not updated here
        // 1. Validate
        UserEntity oldUser = validateUserForCreateOrUpdate(updateRequest.getId(), updateRequest.getUsername(),
                updateRequest.getMobile(), updateRequest.getEmail(), updateRequest.getDeptId(), updateRequest.getPostIds());

        // 2.1 Update user
        UserEntity updateObj = BeanUtils.toBean(updateRequest, UserEntity.class);
        userMapper.updateById(updateObj);
        // 2.2 Update posts
        updateUserPost(updateRequest, updateObj);

        // 3. Record operation log context
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldUser, UserSaveRequest.class));
        LogRecordContext.putVariable("user", oldUser);
    }

    private void updateUserPost(UserSaveRequest request, UserEntity updateObj) {
        Long userId = request.getId();
        Set<Long> dbPostIds = convertSet(userPostMapper.selectListByUserId(userId), UserPostEntity::getPostId);
        // Compute the post IDs to add and delete
        Set<Long> postIds = CollUtil.emptyIfNull(updateObj.getPostIds());
        Collection<Long> createPostIds = CollUtil.subtract(postIds, dbPostIds);
        Collection<Long> deletePostIds = CollUtil.subtract(dbPostIds, postIds);
        // Execute insertion and deletion. For already authorized posts, no action needed
        if (!CollectionUtil.isEmpty(createPostIds)) {
            userPostMapper.insertBatch(convertList(createPostIds,
                    postId -> new UserPostEntity().setUserId(userId).setPostId(postId)));
        }
        if (!CollectionUtil.isEmpty(deletePostIds)) {
            userPostMapper.deleteByUserIdAndPostId(userId, deletePostIds);
        }
    }

    @Override
    public void updateUserLogin(Long id, String loginIp) {
        userMapper.updateById(new UserEntity().setId(id).setLoginIp(loginIp).setLoginDate(LocalDateTime.now()));
    }

    @Override
    public void updateUserProfile(Long id, UserProfileUpdateRequest request) {
        // Validate
        validateUserExists(id);
        validateEmailUnique(id, request.getEmail());
        validateMobileUnique(id, request.getMobile());
        // Execute update
        userMapper.updateById(BeanUtils.toBean(request, UserEntity.class).setId(id));
    }

    @Override
    public void updateUserPassword(Long id, UserProfileUpdatePasswordRequest request) {
        // Validate the old password
        validateOldPassword(id, request.getOldPassword());
        // Execute update
        UserEntity updateObj = new UserEntity().setId(id);
        updateObj.setPassword(encodePassword(request.getNewPassword())); // encode password
        userMapper.updateById(updateObj);
    }

    @Override
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_UPDATE_PASSWORD_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_USER_UPDATE_PASSWORD_SUCCESS)
    public void updateUserPassword(Long id, String password) {
        // 1. Validate that the user exists
        UserEntity user = validateUserExists(id);

        // 2. Update password
        UserEntity updateObj = new UserEntity();
        updateObj.setId(id);
        updateObj.setPassword(encodePassword(password)); // encode password
        userMapper.updateById(updateObj);

        // 3. Record operate log context
        LogRecordContext.putVariable("user", user);
        LogRecordContext.putVariable("newPassword", updateObj.getPassword());
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        // Validate that the user exists
        validateUserExists(id);
        // Update status
        UserEntity updateObj = new UserEntity();
        updateObj.setId(id);
        updateObj.setStatus(status);
        userMapper.updateById(updateObj);

        // If disabling the user, also remove their Token
        if (CommonStatusEnum.isDisable(status)) {
            oauth2TokenService.removeAccessToken(id, UserTypeEnum.ADMIN.getValue());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_USER_DELETE_SUCCESS)
    public void deleteUser(Long id) {
        // 1. Validate that the user exists
        UserEntity user = validateUserExists(id);

        // 2.1 Delete the user
        userMapper.deleteById(id);
        // 2.2 Delete the user's related data
        permissionService.processUserDeleted(id);
        // 2.3 Delete the user's posts
        userPostMapper.deleteByUserId(id);

        // 3. Record operate log context
        LogRecordContext.putVariable("user", user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserList(List<Long> ids) {
        // 1. Batch delete users
        userMapper.deleteByIds(ids);

        // 2. Batch delete users' related data
        ids.forEach(id -> {
            permissionService.processUserDeleted(id);
            userPostMapper.deleteByUserId(id);
        });
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public UserEntity getUserByMobile(String mobile) {
        return userMapper.selectByMobile(mobile);
    }

    @Override
    public PageResult<UserEntity> getUserPage(UserPageRequest request) {
        // If a role ID is provided, look up the user IDs for that role
        Set<Long> userIds = null;
        if (request.getRoleId() != null) {
            userIds = permissionService.getUserRoleIdListByRoleId(singleton(request.getRoleId()));
            if (CollUtil.isEmpty(userIds)) {
                return PageResult.empty();
            }
        }

        // Paginated query
        return userMapper.selectPage(request, getDeptCondition(request.getDeptId()), userIds);
    }

    @Override
    public UserEntity getUser(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<UserEntity> getUserListByDeptIds(Collection<Long> deptIds) {
        if (CollUtil.isEmpty(deptIds)) {
            return Collections.emptyList();
        }
        return userMapper.selectListByDeptIds(deptIds);
    }

    @Override
    public List<UserEntity> getUserListByPostIds(Collection<Long> postIds) {
        if (CollUtil.isEmpty(postIds)) {
            return Collections.emptyList();
        }
        Set<Long> userIds = convertSet(userPostMapper.selectListByPostIds(postIds), UserPostEntity::getUserId);
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return userMapper.selectByIds(userIds);
    }

    @Override
    public List<UserEntity> getUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return userMapper.selectByIds(ids);
    }

    @Override
    public void validateUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // Get user info
        List<UserEntity> users = userMapper.selectByIds(ids);
        Map<Long, UserEntity> userMap = CollectionUtils.convertMap(users, UserEntity::getId);
        // Validate
        ids.forEach(id -> {
            UserEntity user = userMap.get(id);
            if (user == null) {
                throw exception(USER_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(user.getStatus())) {
                throw exception(USER_IS_DISABLE, user.getNickname());
            }
        });
    }

    @Override
    public List<UserEntity> getUserListByNickname(String nickname) {
        return userMapper.selectListByNickname(nickname);
    }

    /**
     * Get the department condition: query the child department IDs of the specified department, including itself
     *
     * @param deptId department ID
     * @return department ID set
     */
    private Set<Long> getDeptCondition(Long deptId) {
        if (deptId == null) {
            return Collections.emptySet();
        }
        Set<Long> deptIds = convertSet(deptService.getChildDeptList(deptId), DepartmentEntity::getId);
        deptIds.add(deptId); // include the department itself
        return deptIds;
    }

    private UserEntity validateUserForCreateOrUpdate(Long id, String username, String mobile, String email,
                                               Long deptId, Set<Long> postIds) {
        // Disable data permission, otherwise data may not be found and uniqueness validation could be wrong
        return DataPermissionUtils.executeIgnore(() -> {
            // Validate that the user exists
            UserEntity user = validateUserExists(id);
            // Validate uniqueness of username
            validateUsernameUnique(id, username);
            // Validate uniqueness of mobile
            validateMobileUnique(id, mobile);
            // Validate uniqueness of email
            validateEmailUnique(id, email);
            // Validate that the department is enabled
            deptService.validateDeptList(CollectionUtils.singleton(deptId));
            // Validate that the posts are enabled
            postService.validatePostList(postIds);
            return user;
        });
    }

    @VisibleForTesting
    UserEntity validateUserExists(Long id) {
        if (id == null) {
            return null;
        }
        UserEntity user = userMapper.selectById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        return user;
    }

    @VisibleForTesting
    void validateUsernameUnique(Long id, String username) {
        if (StrUtil.isBlank(username)) {
            return;
        }
        UserEntity user = userMapper.selectByUsername(username);
        if (user == null) {
            return;
        }
        // If id is null, no need to compare whether it is the same user id
        if (id == null) {
            throw exception(USER_USERNAME_EXISTS);
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_USERNAME_EXISTS);
        }
    }

    @VisibleForTesting
    void validateEmailUnique(Long id, String email) {
        if (StrUtil.isBlank(email)) {
            return;
        }
        UserEntity user = userMapper.selectByEmail(email);
        if (user == null) {
            return;
        }
        // If id is null, no need to compare whether it is the same user id
        if (id == null) {
            throw exception(USER_EMAIL_EXISTS);
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_EMAIL_EXISTS);
        }
    }

    @VisibleForTesting
    void validateMobileUnique(Long id, String mobile) {
        if (StrUtil.isBlank(mobile)) {
            return;
        }
        UserEntity user = userMapper.selectByMobile(mobile);
        if (user == null) {
            return;
        }
        // If id is null, no need to compare whether it is the same user id
        if (id == null) {
            throw exception(USER_MOBILE_EXISTS);
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_MOBILE_EXISTS);
        }
    }

    /**
     * Validate the old password
     * @param id          user id
     * @param oldPassword old password
     */
    @VisibleForTesting
    void validateOldPassword(Long id, String oldPassword) {
        UserEntity user = userMapper.selectById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        if (!isPasswordMatch(oldPassword, user.getPassword())) {
            throw exception(USER_PASSWORD_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // wrap in a transaction so any exception rolls back the entire import
    public UserImportResponse importUserList(List<UserImportExcelDto> importUsers, boolean isUpdateSupport) {
        // 1.1 Validate parameters
        if (CollUtil.isEmpty(importUsers)) {
            throw exception(USER_IMPORT_LIST_IS_EMPTY);
        }
        // 1.2 Initial password must not be blank
        String initPassword = configApi.getConfigValueByKey(USER_INIT_PASSWORD_KEY);
        if (StrUtil.isEmpty(initPassword)) {
            throw exception(USER_IMPORT_INIT_PASSWORD);
        }

        // 2. Iterate and create or update one by one
        UserImportResponse response = UserImportResponse.builder().createUsernames(new ArrayList<>())
                .updateUsernames(new ArrayList<>()).failureUsernames(new LinkedHashMap<>()).build();
        AtomicInteger index = new AtomicInteger(1);
        importUsers.forEach(importUser -> {
            int currentIndex = index.getAndIncrement();
            // 2.1.1 Validate fields
            try {
                ValidationUtils.validate(BeanUtils.toBean(importUser, UserSaveRequest.class).setPassword(initPassword));
            } catch (ConstraintViolationException ex) {
                String key = StrUtil.blankToDefault(importUser.getUsername(), "Row " + currentIndex);
                response.getFailureUsernames().put(key, ex.getMessage());
                return;
            }
            // 2.1.2 Validate, check whether there is a non-conformance reason
            try {
                validateUserForCreateOrUpdate(null, null, importUser.getMobile(), importUser.getEmail(),
                        importUser.getDeptId(), null);
            } catch (ServiceException ex) {
                response.getFailureUsernames().put(importUser.getUsername(), ex.getMessage());
                return;
            }

            // 2.2.1 If the user does not exist, insert
            UserEntity existUser = userMapper.selectByUsername(importUser.getUsername());
            if (existUser == null) {
                userMapper.insert(BeanUtils.toBean(importUser, UserEntity.class)
                        .setPassword(encodePassword(initPassword)).setPostIds(new HashSet<>())); // set default password and empty post ID array
                response.getCreateUsernames().add(importUser.getUsername());
                return;
            }
            // 2.2.2 If the user exists, check whether updates are allowed
            if (!isUpdateSupport) {
                response.getFailureUsernames().put(importUser.getUsername(), USER_USERNAME_EXISTS.getMsg());
                return;
            }
            UserEntity updateUser = BeanUtils.toBean(importUser, UserEntity.class);
            updateUser.setId(existUser.getId());
            userMapper.updateById(updateUser);
            response.getUpdateUsernames().add(importUser.getUsername());
        });
        return response;
    }

    @Override
    public List<UserEntity> getUserListByStatus(Integer status) {
        return userMapper.selectListByStatus(status);
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Encode the password
     *
     * @param password password
     * @return encoded password
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

}
