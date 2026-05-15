package com.focela.platform.system.api.user;

import com.focela.platform.framework.common.utils.collection.CollectionUtils;
import com.focela.platform.system.api.user.dto.UserRpcResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Admin user API interface
 */
public interface UserApi {

    /**
     * Get a user by user ID
     *
     * @param id user ID
     * @return user information
     */
    UserRpcResponse getUser(Long id);

    /**
     * Get the subordinates of a user by user ID
     *
     * @param id user ID
     * @return list of subordinate users
     */
    List<UserRpcResponse> getUserListBySubordinate(Long id);

    /**
     * Get users by user IDs
     *
     * @param ids user IDs
     * @return user information list
     */
    List<UserRpcResponse> getUserList(Collection<Long> ids);

    /**
     * Get users for the specified departments
     *
     * @param deptIds department IDs
     * @return user list
     */
    List<UserRpcResponse> getUserListByDeptIds(Collection<Long> deptIds);

    /**
     * Get users for the specified posts
     *
     * @param postIds post IDs
     * @return user list
     */
    List<UserRpcResponse> getUserListByPostIds(Collection<Long> postIds);

    /**
     * Get the user Map
     *
     * @param ids user IDs
     * @return user Map
     */
    default Map<Long, UserRpcResponse> getUserMap(Collection<Long> ids) {
        List<UserRpcResponse> users = getUserList(ids);
        return CollectionUtils.convertMap(users, UserRpcResponse::getId);
    }

    /**
     * Validate whether a user is valid. The following cases are considered invalid:
     * 1. user ID does not exist
     * 2. user is disabled
     *
     * @param id user ID
     */
    default void validateUser(Long id) {
        validateUserList(Collections.singleton(id));
    }

    /**
     * Validate whether the users are valid. The following cases are considered invalid:
     * 1. user ID does not exist
     * 2. user is disabled
     *
     * @param ids user IDs
     */
    void validateUserList(Collection<Long> ids);

}
