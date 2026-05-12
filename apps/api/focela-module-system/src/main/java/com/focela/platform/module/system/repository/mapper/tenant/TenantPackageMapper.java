package com.focela.platform.module.system.repository.mapper.tenant;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.tenant.dto.packages.TenantPackagePageRequest;
import com.focela.platform.module.system.repository.entity.tenant.TenantPackageEntity;
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
