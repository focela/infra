package com.focela.platform.module.system.repository.mapper.tenant;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.framework.mybatis.core.util.MyBatisUtils;
import com.focela.platform.module.system.controller.admin.tenant.dto.tenant.TenantPageRequest;
import com.focela.platform.module.system.repository.entity.tenant.TenantEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TenantMapper extends BaseMapperX<TenantEntity> {

    default PageResult<TenantEntity> selectPage(TenantPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<TenantEntity>()
                .likeIfPresent(TenantEntity::getName, request.getName())
                .likeIfPresent(TenantEntity::getContactName, request.getContactName())
                .likeIfPresent(TenantEntity::getContactMobile, request.getContactMobile())
                .eqIfPresent(TenantEntity::getStatus, request.getStatus())
                .betweenIfPresent(TenantEntity::getCreateTime, request.getCreateTime())
                .orderByDesc(TenantEntity::getId));
    }

    default TenantEntity selectByName(String name) {
        return selectOne(TenantEntity::getName, name);
    }

    default List<TenantEntity> selectListByWebsite(String website) {
        return selectList(new LambdaQueryWrapperX<TenantEntity>()
                .apply(MyBatisUtils.findInSet("websites", website)));
    }

    default Long selectCountByPackageId(Long packageId) {
        return selectCount(TenantEntity::getPackageId, packageId);
    }

    default List<TenantEntity> selectListByPackageId(Long packageId) {
        return selectList(TenantEntity::getPackageId, packageId);
    }

    default List<TenantEntity> selectListByStatus(Integer status) {
        return selectList(TenantEntity::getStatus, status);
    }

}
