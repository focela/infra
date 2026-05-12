package com.focela.platform.module.system.repository.mapper.permission;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.permission.dto.role.RolePageRequest;
import com.focela.platform.module.system.repository.entity.permission.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapperX<RoleEntity> {

    default PageResult<RoleEntity> selectPage(RolePageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<RoleEntity>()
                .likeIfPresent(RoleEntity::getName, request.getName())
                .likeIfPresent(RoleEntity::getCode, request.getCode())
                .eqIfPresent(RoleEntity::getStatus, request.getStatus())
                .betweenIfPresent(BaseEntity::getCreateTime, request.getCreateTime())
                .orderByAsc(RoleEntity::getSort));
    }

    default RoleEntity selectByName(String name) {
        return selectOne(RoleEntity::getName, name);
    }

    default RoleEntity selectByCode(String code) {
        return selectOne(RoleEntity::getCode, code);
    }

    default List<RoleEntity> selectListByStatus(@Nullable Collection<Integer> statuses) {
        return selectList(RoleEntity::getStatus, statuses);
    }

}
