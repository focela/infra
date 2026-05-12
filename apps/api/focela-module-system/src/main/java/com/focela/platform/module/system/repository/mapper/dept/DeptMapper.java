package com.focela.platform.module.system.repository.mapper.dept;

import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.dept.dto.dept.DeptListRequest;
import com.focela.platform.module.system.repository.entity.dept.DeptEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface DeptMapper extends BaseMapperX<DeptEntity> {

    default List<DeptEntity> selectList(DeptListRequest reqVO) {
        return selectList(new LambdaQueryWrapperX<DeptEntity>()
                .likeIfPresent(DeptEntity::getName, reqVO.getName())
                .eqIfPresent(DeptEntity::getStatus, reqVO.getStatus()));
    }

    default DeptEntity selectByParentIdAndName(Long parentId, String name) {
        return selectOne(DeptEntity::getParentId, parentId, DeptEntity::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(DeptEntity::getParentId, parentId);
    }

    default List<DeptEntity> selectListByParentId(Collection<Long> parentIds) {
        return selectList(DeptEntity::getParentId, parentIds);
    }

    default List<DeptEntity> selectListByLeaderUserId(Long id) {
        return selectList(DeptEntity::getLeaderUserId, id);
    }

}
