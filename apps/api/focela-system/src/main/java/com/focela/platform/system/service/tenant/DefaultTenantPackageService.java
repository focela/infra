package com.focela.platform.system.service.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.tenant.request.plan.TenantPackagePageRequest;
import com.focela.platform.system.controller.admin.tenant.request.plan.TenantPackageSaveRequest;
import com.focela.platform.system.domain.entity.tenant.TenantEntity;
import com.focela.platform.system.domain.entity.tenant.TenantPackageEntity;
import com.focela.platform.system.repository.mapper.tenant.TenantPackageMapper;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;

/**
 * Tenant package Service implementation class
 */
@Service
@Validated
@RequiredArgsConstructor
public class DefaultTenantPackageService implements TenantPackageService {

    private final TenantPackageMapper tenantPackageMapper;

    @Resource
    @Lazy // avoid circular dependency error
    private TenantService tenantService;

    @Override
    public Long createTenantPackage(TenantPackageSaveRequest createRequest) {
        // validate package name is not duplicated
        validateTenantPackageNameUnique(null, createRequest.getName());
        // insert
        TenantPackageEntity tenantPackage = BeanUtils.toBean(createRequest, TenantPackageEntity.class);
        tenantPackageMapper.insert(tenantPackage);
        // return
        return tenantPackage.getId();
    }

    @Override
    @DSTransactional // multi datasource: use @DSTransactional to guarantee local transactions and datasource switching
    public void updateTenantPackage(TenantPackageSaveRequest updateRequest) {
        // validate existence
        TenantPackageEntity tenantPackage = validateTenantPackageExists(updateRequest.getId());
        // validate package name is not duplicated
        validateTenantPackageNameUnique(updateRequest.getId(), updateRequest.getName());
        // update
        TenantPackageEntity updateObj = BeanUtils.toBean(updateRequest, TenantPackageEntity.class);
        tenantPackageMapper.updateById(updateObj);
        // if the menus changed, update each tenant's menus
        if (!CollUtil.isEqualList(tenantPackage.getMenuIds(), updateRequest.getMenuIds())) {
            List<TenantEntity> tenants = tenantService.getTenantListByPackageId(tenantPackage.getId());
            tenants.forEach(tenant -> tenantService.updateTenantRoleMenu(tenant.getId(), updateRequest.getMenuIds()));
        }
    }

    @Override
    public void deleteTenantPackage(Long id) {
        // validate existence
        validateTenantPackageExists(id);
        // validate in use
        validateTenantUsed(id);
        // delete
        tenantPackageMapper.deleteById(id);
    }

    @Override
    public void deleteTenantPackageList(List<Long> ids) {
        // 1. validate whether any tenant is using this package
        for (Long id : ids) {
            if (tenantService.getTenantCountByPackageId(id) > 0) {
                throw exception(TENANT_PACKAGE_USED);
            }
        }

        // 2. batch delete
        tenantPackageMapper.deleteByIds(ids);
    }

    private TenantPackageEntity validateTenantPackageExists(Long id) {
        TenantPackageEntity tenantPackage = tenantPackageMapper.selectById(id);
        if (tenantPackage == null) {
            throw exception(TENANT_PACKAGE_NOT_FOUND);
        }
        return tenantPackage;
    }

    private void validateTenantUsed(Long id) {
        if (tenantService.getTenantCountByPackageId(id) > 0) {
            throw exception(TENANT_PACKAGE_USED);
        }
    }

    @Override
    public TenantPackageEntity getTenantPackage(Long id) {
        return tenantPackageMapper.selectById(id);
    }

    @Override
    public PageResult<TenantPackageEntity> getTenantPackagePage(TenantPackagePageRequest pageRequest) {
        return tenantPackageMapper.selectPage(pageRequest);
    }

    @Override
    public TenantPackageEntity validateTenantPackage(Long id) {
        TenantPackageEntity tenantPackage = tenantPackageMapper.selectById(id);
        if (tenantPackage == null) {
            throw exception(TENANT_PACKAGE_NOT_FOUND);
        }
        if (tenantPackage.getStatus().equals(CommonStatusEnum.DISABLE.getStatus())) {
            throw exception(TENANT_PACKAGE_DISABLED, tenantPackage.getName());
        }
        return tenantPackage;
    }

    @Override
    public List<TenantPackageEntity> getTenantPackageListByStatus(Integer status) {
        return tenantPackageMapper.selectListByStatus(status);
    }


    @VisibleForTesting
    void validateTenantPackageNameUnique(Long id, String name) {
        if (StrUtil.isBlank(name)) {
            return;
        }
        TenantPackageEntity tenantPackage = tenantPackageMapper.selectByName(name);
        if (tenantPackage == null) {
            return;
        }
        // if id is null, no need to compare against a user with the same id
        if (id == null) {
            throw exception(TENANT_PACKAGE_NAME_DUPLICATE);
        }
        if (!tenantPackage.getId().equals(id)) {
            throw exception(TENANT_PACKAGE_NAME_DUPLICATE);
        }
    }

}
