package com.focela.platform.system.converter.user;

import com.focela.platform.framework.common.utils.collection.CollectionUtils;
import com.focela.platform.framework.common.utils.collection.MapUtils;
import com.focela.platform.framework.common.utils.object.BeanUtils;
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
        UserResponse userVO = BeanUtils.toBean(user, UserResponse.class);
        if (dept != null) {
            userVO.setDeptName(dept.getName());
        }
        return userVO;
    }

    default List<UserSimpleResponse> convertSimpleList(List<UserEntity> list, Map<Long, DepartmentEntity> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            UserSimpleResponse userVO = BeanUtils.toBean(user, UserSimpleResponse.class);
            MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> userVO.setDeptName(dept.getName()));
            return userVO;
        });
    }

    default UserProfileResponse convert(UserEntity user, List<RoleEntity> userRoles,
                                      DepartmentEntity dept, List<PostEntity> posts) {
        UserProfileResponse userVO = BeanUtils.toBean(user, UserProfileResponse.class);
        userVO.setRoles(BeanUtils.toBean(userRoles, RoleSimpleResponse.class));
        userVO.setDept(BeanUtils.toBean(dept, DepartmentSimpleResponse.class));
        userVO.setPosts(BeanUtils.toBean(posts, PostSimpleResponse.class));
        return userVO;
    }

}
