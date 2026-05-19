package com.focela.platform.system.repository.mapper.tenant;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.mybatis.core.utils.MyBatisUtils;
import com.focela.platform.system.controller.admin.tenant.dto.TenantPageRequest;
import com.focela.platform.system.domain.entity.tenant.TenantEntity;
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
