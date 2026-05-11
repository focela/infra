package com.focela.platform.module.system.repository.mapper.tenant;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.tenant.vo.packages.TenantPackagePageReqVO;
import com.focela.platform.module.system.repository.entity.tenant.TenantPackageEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TenantPackageMapper extends BaseMapperX<TenantPackageEntity> {

    default PageResult<TenantPackageEntity> selectPage(TenantPackagePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TenantPackageEntity>()
                .likeIfPresent(TenantPackageEntity::getName, reqVO.getName())
                .eqIfPresent(TenantPackageEntity::getStatus, reqVO.getStatus())
                .likeIfPresent(TenantPackageEntity::getRemark, reqVO.getRemark())
                .betweenIfPresent(TenantPackageEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TenantPackageEntity::getId));
    }

    default List<TenantPackageEntity> selectListByStatus(Integer status) {
        return selectList(TenantPackageEntity::getStatus, status);
    }

    default TenantPackageEntity selectByName(String name) {
        return selectOne(TenantPackageEntity::getName, name);
    }
}
