package com.focela.platform.module.system.convert.user;

import com.focela.platform.framework.common.util.collection.CollectionUtils;
import com.focela.platform.framework.common.util.collection.MapUtils;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.dept.dto.dept.DeptSimpleResponse;
import com.focela.platform.module.system.controller.admin.dept.dto.post.PostSimpleResponse;
import com.focela.platform.module.system.controller.admin.permission.dto.role.RoleSimpleResponse;
import com.focela.platform.module.system.controller.admin.user.dto.profile.UserProfileResponse;
import com.focela.platform.module.system.controller.admin.user.dto.user.UserResponse;
import com.focela.platform.module.system.controller.admin.user.dto.user.UserSimpleResponse;
import com.focela.platform.module.system.repository.entity.dept.DeptEntity;
import com.focela.platform.module.system.repository.entity.dept.PostEntity;
import com.focela.platform.module.system.repository.entity.permission.RoleEntity;
import com.focela.platform.module.system.repository.entity.user.AdminUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    default List<UserResponse> convertList(List<AdminUserEntity> list, Map<Long, DeptEntity> deptMap) {
        return CollectionUtils.convertList(list, user -> convert(user, deptMap.get(user.getDeptId())));
    }

    default UserResponse convert(AdminUserEntity user, DeptEntity dept) {
        UserResponse userVO = BeanUtils.toBean(user, UserResponse.class);
        if (dept != null) {
            userVO.setDeptName(dept.getName());
        }
        return userVO;
    }

    default List<UserSimpleResponse> convertSimpleList(List<AdminUserEntity> list, Map<Long, DeptEntity> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            UserSimpleResponse userVO = BeanUtils.toBean(user, UserSimpleResponse.class);
            MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> userVO.setDeptName(dept.getName()));
            return userVO;
        });
    }

    default UserProfileResponse convert(AdminUserEntity user, List<RoleEntity> userRoles,
                                      DeptEntity dept, List<PostEntity> posts) {
        UserProfileResponse userVO = BeanUtils.toBean(user, UserProfileResponse.class);
        userVO.setRoles(BeanUtils.toBean(userRoles, RoleSimpleResponse.class));
        userVO.setDept(BeanUtils.toBean(dept, DeptSimpleResponse.class));
        userVO.setPosts(BeanUtils.toBean(posts, PostSimpleResponse.class));
        return userVO;
    }

}
