package com.focela.platform.system.service.tenant;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.tenant.dto.packages.TenantPackagePageRequest;
import com.focela.platform.system.controller.admin.tenant.dto.packages.TenantPackageSaveRequest;
import com.focela.platform.system.entity.tenant.TenantPackageEntity;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 租户套餐 Service 接口
 */
public interface TenantPackageService {

    /**
     * 创建租户套餐
     *
     * @param createRequest 创建信息
     * @return 编号
     */
    Long createTenantPackage(@Valid TenantPackageSaveRequest createRequest);

    /**
     * 更新租户套餐
     *
     * @param updateRequest 更新信息
     */
    void updateTenantPackage(@Valid TenantPackageSaveRequest updateRequest);

    /**
     * 删除租户套餐
     *
     * @param id 编号
     */
    void deleteTenantPackage(Long id);

    /**
     * 批量删除租户套餐
     *
     * @param ids 编号数组
     */
    void deleteTenantPackageList(List<Long> ids);

    /**
     * 获得租户套餐
     *
     * @param id 编号
     * @return 租户套餐
     */
    TenantPackageEntity getTenantPackage(Long id);

    /**
     * 获得租户套餐分页
     *
     * @param pageRequest 分页查询
     * @return 租户套餐分页
     */
    PageResult<TenantPackageEntity> getTenantPackagePage(TenantPackagePageRequest pageRequest);

    /**
     * 校验租户套餐
     *
     * @param id 编号
     * @return 租户套餐
     */
    TenantPackageEntity validTenantPackage(Long id);

    /**
     * 获得指定状态的租户套餐列表
     *
     * @param status 状态
     * @return 租户套餐
     */
    List<TenantPackageEntity> getTenantPackageListByStatus(Integer status);

}
