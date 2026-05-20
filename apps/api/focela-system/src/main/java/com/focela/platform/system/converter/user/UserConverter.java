package com.focela.platform.system.converter.user;

import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.common.utils.collection.MapUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.department.response.department.DepartmentSimpleResponse;
import com.focela.platform.system.controller.admin.department.response.post.PostSimpleResponse;
import com.focela.platform.system.controller.admin.permission.response.role.RoleSimpleResponse;
import com.focela.platform.system.controller.admin.user.response.profile.UserProfileResponse;
import com.focela.platform.system.controller.admin.user.response.UserResponse;
import com.focela.platform.system.controller.admin.user.response.UserSimpleResponse;
import com.focela.platform.system.domain.entity.department.DepartmentEntity;
import com.focela.platform.system.domain.entity.department.PostEntity;
import com.focela.platform.system.domain.entity.permission.RoleEntity;
import com.focela.platform.system.domain.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    default List<UserResponse> convertList(List<UserEntity> list, Map<Long, DepartmentEntity> departmentMap) {
        return CollectionUtils.convertList(list, user -> convert(user, departmentMap.get(user.getDeptId())));
    }

    default UserResponse convert(UserEntity user, DepartmentEntity department) {
        UserResponse userResponse = BeanUtils.toBean(user, UserResponse.class);
        if (department != null) {
            userResponse.setDeptName(department.getName());
        }
        return userResponse;
    }

    default List<UserSimpleResponse> convertSimpleList(List<UserEntity> list, Map<Long, DepartmentEntity> departmentMap) {
        return CollectionUtils.convertList(list, user -> {
            UserSimpleResponse userResponse = BeanUtils.toBean(user, UserSimpleResponse.class);
            MapUtils.findAndThen(departmentMap, user.getDeptId(), department -> userResponse.setDeptName(department.getName()));
            return userResponse;
        });
    }

    default UserProfileResponse convert(UserEntity user, List<RoleEntity> userRoles,
                                      DepartmentEntity department, List<PostEntity> posts) {
        UserProfileResponse userProfileResponse = BeanUtils.toBean(user, UserProfileResponse.class);
        userProfileResponse.setRoles(BeanUtils.toBean(userRoles, RoleSimpleResponse.class));
        userProfileResponse.setDept(BeanUtils.toBean(department, DepartmentSimpleResponse.class));
        userProfileResponse.setPosts(BeanUtils.toBean(posts, PostSimpleResponse.class));
        return userProfileResponse;
    }

}
