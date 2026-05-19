package com.focela.platform.system.repository.mapper.user;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.system.controller.admin.user.dto.UserPageRequest;
import com.focela.platform.system.domain.entity.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapperX<UserEntity> {

    default UserEntity selectByUsername(String username) {
        return selectOne(UserEntity::getUsername, username);
    }

    default UserEntity selectByEmail(String email) {
        return selectOne(UserEntity::getEmail, email);
    }

    default UserEntity selectByMobile(String mobile) {
        return selectOne(UserEntity::getMobile, mobile);
    }

    default PageResult<UserEntity> selectPage(UserPageRequest request, Collection<Long> deptIds, Collection<Long> userIds) {
        return selectPage(request, new LambdaQueryWrapperX<UserEntity>()
                .likeIfPresent(UserEntity::getUsername, request.getUsername())
                .likeIfPresent(UserEntity::getMobile, request.getMobile())
                .eqIfPresent(UserEntity::getStatus, request.getStatus())
                .betweenIfPresent(UserEntity::getCreateTime, request.getCreateTime())
                .inIfPresent(UserEntity::getDeptId, deptIds)
                .inIfPresent(UserEntity::getId, userIds)
                .orderByDesc(UserEntity::getId));
    }

    default List<UserEntity> selectListByNickname(String nickname) {
        return selectList(new LambdaQueryWrapperX<UserEntity>().like(UserEntity::getNickname, nickname));
    }

    default List<UserEntity> selectListByStatus(Integer status) {
        return selectList(UserEntity::getStatus, status);
    }

    default List<UserEntity> selectListByDeptIds(Collection<Long> deptIds) {
        return selectList(UserEntity::getDeptId, deptIds);
    }

}
