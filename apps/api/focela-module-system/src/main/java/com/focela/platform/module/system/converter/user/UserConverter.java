package com.focela.platform.module.system.converter.user;

import com.focela.platform.framework.common.utils.collection.CollectionUtils;
import com.focela.platform.framework.common.utils.collection.MapUtils;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.department.dto.dept.DepartmentSimpleResponse;
import com.focela.platform.module.system.controller.admin.department.dto.post.PostSimpleResponse;
import com.focela.platform.module.system.controller.admin.permission.dto.role.RoleSimpleResponse;
import com.focela.platform.module.system.controller.admin.user.dto.profile.UserProfileResponse;
import com.focela.platform.module.system.controller.admin.user.dto.UserResponse;
import com.focela.platform.module.system.controller.admin.user.dto.UserSimpleResponse;
import com.focela.platform.module.system.entity.department.DepartmentEntity;
import com.focela.platform.module.system.entity.department.PostEntity;
import com.focela.platform.module.system.entity.permission.RoleEntity;
import com.focela.platform.module.system.entity.user.AdminUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    default List<UserResponse> convertList(List<AdminUserEntity> list, Map<Long, DepartmentEntity> deptMap) {
        return CollectionUtils.convertList(list, user -> convert(user, deptMap.get(user.getDeptId())));
    }

    default UserResponse convert(AdminUserEntity user, DepartmentEntity dept) {
        UserResponse userVO = BeanUtils.toBean(user, UserResponse.class);
        if (dept != null) {
            userVO.setDeptName(dept.getName());
        }
        return userVO;
    }

    default List<UserSimpleResponse> convertSimpleList(List<AdminUserEntity> list, Map<Long, DepartmentEntity> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            UserSimpleResponse userVO = BeanUtils.toBean(user, UserSimpleResponse.class);
            MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> userVO.setDeptName(dept.getName()));
            return userVO;
        });
    }

    default UserProfileResponse convert(AdminUserEntity user, List<RoleEntity> userRoles,
                                      DepartmentEntity dept, List<PostEntity> posts) {
        UserProfileResponse userVO = BeanUtils.toBean(user, UserProfileResponse.class);
        userVO.setRoles(BeanUtils.toBean(userRoles, RoleSimpleResponse.class));
        userVO.setDept(BeanUtils.toBean(dept, DepartmentSimpleResponse.class));
        userVO.setPosts(BeanUtils.toBean(posts, PostSimpleResponse.class));
        return userVO;
    }

}
