package com.focela.platform.system.repository.mapper.department;

import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.system.controller.admin.department.request.dept.DepartmentListRequest;
import com.focela.platform.system.domain.entity.department.DepartmentEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface DepartmentMapper extends BaseMapperX<DepartmentEntity> {

    default List<DepartmentEntity> selectList(DepartmentListRequest request) {
        return selectList(new LambdaQueryWrapperX<DepartmentEntity>()
                .likeIfPresent(DepartmentEntity::getName, request.getName())
                .eqIfPresent(DepartmentEntity::getStatus, request.getStatus()));
    }

    default DepartmentEntity selectByParentIdAndName(Long parentId, String name) {
        return selectOne(DepartmentEntity::getParentId, parentId, DepartmentEntity::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(DepartmentEntity::getParentId, parentId);
    }

    default List<DepartmentEntity> selectListByParentId(Collection<Long> parentIds) {
        return selectList(DepartmentEntity::getParentId, parentIds);
    }

    default List<DepartmentEntity> selectListByLeaderUserId(Long id) {
        return selectList(DepartmentEntity::getLeaderUserId, id);
    }

}
