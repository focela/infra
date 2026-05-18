package com.focela.platform.system.converter.user;

import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.common.utils.collection.MapUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.department.dto.dept.DepartmentSimpleResponse;
import com.focela.platform.system.controller.admin.department.dto.post.PostSimpleResponse;
import com.focela.platform.system.controller.admin.permission.dto.role.RoleSimpleResponse;
import com.focela.platform.system.controller.admin.user.dto.profile.UserProfileResponse;
import com.focela.platform.system.controller.admin.user.dto.UserResponse;
import com.focela.platform.system.controller.admin.user.dto.UserSimpleResponse;
import com.focela.platform.system.entity.department.DepartmentEntity;
import com.focela.platform.system.entity.department.PostEntity;
import com.focela.platform.system.entity.permission.RoleEntity;
import com.focela.platform.system.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    default List<UserResponse> convertList(List<UserEntity> list, Map<Long, DepartmentEntity> deptMap) {
        return CollectionUtils.convertList(list, user -> convert(user, deptMap.get(user.getDeptId())));
    }

    default UserResponse convert(UserEntity user, DepartmentEntity dept) {
        UserResponse userResponse = BeanUtils.toBean(user, UserResponse.class);
        if (dept != null) {
            userResponse.setDeptName(dept.getName());
        }
        return userResponse;
    }

    default List<UserSimpleResponse> convertSimpleList(List<UserEntity> list, Map<Long, DepartmentEntity> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            UserSimpleResponse userResponse = BeanUtils.toBean(user, UserSimpleResponse.class);
            MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> userResponse.setDeptName(dept.getName()));
            return userResponse;
        });
    }

    default UserProfileResponse convert(UserEntity user, List<RoleEntity> userRoles,
                                      DepartmentEntity dept, List<PostEntity> posts) {
        UserProfileResponse userProfileResponse = BeanUtils.toBean(user, UserProfileResponse.class);
        userProfileResponse.setRoles(BeanUtils.toBean(userRoles, RoleSimpleResponse.class));
        userProfileResponse.setDept(BeanUtils.toBean(dept, DepartmentSimpleResponse.class));
        userProfileResponse.setPosts(BeanUtils.toBean(posts, PostSimpleResponse.class));
        return userProfileResponse;
    }

}
