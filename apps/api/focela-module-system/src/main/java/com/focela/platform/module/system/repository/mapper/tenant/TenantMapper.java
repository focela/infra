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

    default PageResult<TenantEntity> selectPage(TenantPageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TenantEntity>()
                .likeIfPresent(TenantEntity::getName, reqVO.getName())
                .likeIfPresent(TenantEntity::getContactName, reqVO.getContactName())
                .likeIfPresent(TenantEntity::getContactMobile, reqVO.getContactMobile())
                .eqIfPresent(TenantEntity::getStatus, reqVO.getStatus())
                .betweenIfPresent(TenantEntity::getCreateTime, reqVO.getCreateTime())
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
