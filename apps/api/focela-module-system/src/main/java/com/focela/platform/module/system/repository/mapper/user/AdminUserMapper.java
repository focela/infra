package com.focela.platform.module.system.repository.mapper.user;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.user.dto.UserPageRequest;
import com.focela.platform.module.system.entity.user.AdminUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface AdminUserMapper extends BaseMapperX<AdminUserEntity> {

    default AdminUserEntity selectByUsername(String username) {
        return selectOne(AdminUserEntity::getUsername, username);
    }

    default AdminUserEntity selectByEmail(String email) {
        return selectOne(AdminUserEntity::getEmail, email);
    }

    default AdminUserEntity selectByMobile(String mobile) {
        return selectOne(AdminUserEntity::getMobile, mobile);
    }

    default PageResult<AdminUserEntity> selectPage(UserPageRequest request, Collection<Long> deptIds, Collection<Long> userIds) {
        return selectPage(request, new LambdaQueryWrapperX<AdminUserEntity>()
                .likeIfPresent(AdminUserEntity::getUsername, request.getUsername())
                .likeIfPresent(AdminUserEntity::getMobile, request.getMobile())
                .eqIfPresent(AdminUserEntity::getStatus, request.getStatus())
                .betweenIfPresent(AdminUserEntity::getCreateTime, request.getCreateTime())
                .inIfPresent(AdminUserEntity::getDeptId, deptIds)
                .inIfPresent(AdminUserEntity::getId, userIds)
                .orderByDesc(AdminUserEntity::getId));
    }

    default List<AdminUserEntity> selectListByNickname(String nickname) {
        return selectList(new LambdaQueryWrapperX<AdminUserEntity>().like(AdminUserEntity::getNickname, nickname));
    }

    default List<AdminUserEntity> selectListByStatus(Integer status) {
        return selectList(AdminUserEntity::getStatus, status);
    }

    default List<AdminUserEntity> selectListByDeptIds(Collection<Long> deptIds) {
        return selectList(AdminUserEntity::getDeptId, deptIds);
    }

}
