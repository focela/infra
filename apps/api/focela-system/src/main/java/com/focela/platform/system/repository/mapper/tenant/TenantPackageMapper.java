package com.focela.platform.system.repository.mapper.tenant;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.system.controller.admin.tenant.request.plan.TenantPackagePageRequest;
import com.focela.platform.system.domain.entity.tenant.TenantPackageEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TenantPackageMapper extends BaseMapperX<TenantPackageEntity> {

    default PageResult<TenantPackageEntity> selectPage(TenantPackagePageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<TenantPackageEntity>()
                .likeIfPresent(TenantPackageEntity::getName, request.getName())
                .eqIfPresent(TenantPackageEntity::getStatus, request.getStatus())
                .likeIfPresent(TenantPackageEntity::getRemark, request.getRemark())
                .betweenIfPresent(TenantPackageEntity::getCreateTime, request.getCreateTime())
                .orderByDesc(TenantPackageEntity::getId));
    }

    default List<TenantPackageEntity> selectListByStatus(Integer status) {
        return selectList(TenantPackageEntity::getStatus, status);
    }

    default TenantPackageEntity selectByName(String name) {
        return selectOne(TenantPackageEntity::getName, name);
    }
}
