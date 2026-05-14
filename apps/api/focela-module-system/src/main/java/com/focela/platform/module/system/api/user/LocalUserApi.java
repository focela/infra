package com.focela.platform.module.system.api.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.datapermission.core.annotation.DataPermission;
import com.focela.platform.framework.datapermission.core.utils.DataPermissionUtils;
import com.focela.platform.module.system.api.user.dto.UserRpcResponse;
import com.focela.platform.module.system.entity.department.DepartmentEntity;
import com.focela.platform.module.system.entity.user.UserEntity;
import com.focela.platform.module.system.service.department.DepartmentService;
import com.focela.platform.module.system.service.user.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.focela.platform.framework.common.utils.collection.CollectionUtils.convertSet;

/**
 * Admin user API implementation class
 */
@Service
public class LocalUserApi implements UserApi {

    @Resource
    private UserService userService;
    @Resource
    private DepartmentService deptService;

    @Override
    @DataPermission(enable = false) // Ignore data permission to avoid filtering preventing the user from being found
    public UserRpcResponse getUser(Long id) {
        UserEntity user = userService.getUser(id);
        return BeanUtils.toBean(user, UserRpcResponse.class);
    }

    @Override
    public List<UserRpcResponse> getUserListBySubordinate(Long id) {
        // 1.1 Get the departments the user is responsible for
        List<DepartmentEntity> depts = deptService.getDeptListByLeaderUserId(id);
        if (CollUtil.isEmpty(depts)) {
            return Collections.emptyList();
        }
        // 1.2 Get all child departments
        Set<Long> deptIds = convertSet(depts, DepartmentEntity::getId);
        List<DepartmentEntity> childDeptList = deptService.getChildDeptList(deptIds);
        if (CollUtil.isNotEmpty(childDeptList)) {
            deptIds.addAll(convertSet(childDeptList, DepartmentEntity::getId));
        }

        // 2. Get users belonging to the departments
        List<UserEntity> users = userService.getUserListByDeptIds(deptIds);
        users.removeIf(item -> ObjUtil.equal(item.getId(), id)); // exclude self
        return BeanUtils.toBean(users, UserRpcResponse.class);
    }

    @Override
    public List<UserRpcResponse> getUserList(Collection<Long> ids) {
        return DataPermissionUtils.executeIgnore(() -> { // Disable data permission. Reason: ID-based API queries are mostly for data joining
            List<UserEntity> users = userService.getUserList(ids);
            return BeanUtils.toBean(users, UserRpcResponse.class);
        });
    }

    @Override
    public List<UserRpcResponse> getUserListByDeptIds(Collection<Long> deptIds) {
        List<UserEntity> users = userService.getUserListByDeptIds(deptIds);
        return BeanUtils.toBean(users, UserRpcResponse.class);
    }

    @Override
    public List<UserRpcResponse> getUserListByPostIds(Collection<Long> postIds) {
        List<UserEntity> users = userService.getUserListByPostIds(postIds);
        return BeanUtils.toBean(users, UserRpcResponse.class);
    }

    @Override
    public void validateUserList(Collection<Long> ids) {
        userService.validateUserList(ids);
    }

}
