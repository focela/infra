package com.focela.platform.system.service.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.common.utils.date.DateUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.datapermission.core.annotation.DataPermission;
import com.focela.platform.tenant.config.TenantProperties;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import com.focela.platform.tenant.core.utils.TenantUtils;
import com.focela.platform.system.controller.admin.permission.dto.role.RoleSaveRequest;
import com.focela.platform.system.controller.admin.tenant.dto.TenantPageRequest;
import com.focela.platform.system.controller.admin.tenant.dto.TenantSaveRequest;
import com.focela.platform.system.converter.tenant.TenantConverter;
import com.focela.platform.system.entity.permission.MenuEntity;
import com.focela.platform.system.entity.permission.RoleEntity;
import com.focela.platform.system.entity.tenant.TenantEntity;
import com.focela.platform.system.entity.tenant.TenantPackageEntity;
import com.focela.platform.system.repository.mapper.tenant.TenantMapper;
import com.focela.platform.system.enums.permission.RoleCodeEnum;
import com.focela.platform.system.enums.permission.RoleTypeEnum;
import com.focela.platform.system.service.permission.MenuService;
import com.focela.platform.system.service.permission.PermissionService;
import com.focela.platform.system.service.permission.RoleService;
import com.focela.platform.system.service.tenant.handler.TenantInfoHandler;
import com.focela.platform.system.service.tenant.handler.TenantMenuHandler;
import com.focela.platform.system.service.user.UserService;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static java.util.Collections.singleton;

/**
 * Tenant Service implementation class
 */
@Service
@Validated
@Slf4j
public class DefaultTenantService implements TenantService {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false) // focela.tenant.enable may disable multi-tenancy
    private TenantProperties tenantProperties;

    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    @Lazy // lazy to avoid circular dependency error
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;
    @Resource
    private PermissionService permissionService;

    @Override
    public List<Long> getTenantIdList() {
        List<TenantEntity> tenants = tenantMapper.selectList();
        return CollectionUtils.convertList(tenants, TenantEntity::getId);
    }

    @Override
    public void validTenant(Long id) {
        TenantEntity tenant = getTenant(id);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        if (tenant.getStatus().equals(CommonStatusEnum.DISABLE.getStatus())) {
            throw exception(TENANT_DISABLE, tenant.getName());
        }
        if (DateUtils.isExpired(tenant.getExpireTime())) {
            throw exception(TENANT_EXPIRE, tenant.getName());
        }
    }

    @Override
    @DSTransactional // multi datasource: use @DSTransactional to guarantee local transactions and datasource switching
    @DataPermission(enable = false) // see https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/1154 for details
    public Long createTenant(TenantSaveRequest createRequest) {
        // validate that the tenant name is not duplicated
        validTenantNameDuplicate(createRequest.getName(), null);
        // validate that the tenant website is not duplicated
        validTenantWebsiteDuplicate(createRequest.getWebsites(), null);
        // validate that the package is not disabled
        TenantPackageEntity tenantPackage = tenantPackageService.validTenantPackage(createRequest.getPackageId());

        // create tenant
        TenantEntity tenant = BeanUtils.toBean(createRequest, TenantEntity.class);
        tenantMapper.insert(tenant);
        // create tenant admin
        TenantUtils.execute(tenant.getId(), () -> {
            // create role
            Long roleId = createRole(tenantPackage);
            // create user and assign role
            Long userId = createUser(roleId, createRequest);
            // update tenant's admin
            tenantMapper.updateById(new TenantEntity().setId(tenant.getId()).setContactUserId(userId));
        });
        return tenant.getId();
    }

    private Long createUser(Long roleId, TenantSaveRequest createRequest) {
        // create user
        Long userId = userService.createUser(TenantConverter.INSTANCE.convert02(createRequest));
        // assign role
        permissionService.assignUserRole(userId, singleton(roleId));
        return userId;
    }

    private Long createRole(TenantPackageEntity tenantPackage) {
        // create role
        RoleSaveRequest request = new RoleSaveRequest();
        request.setName(RoleCodeEnum.TENANT_ADMIN.getName()).setCode(RoleCodeEnum.TENANT_ADMIN.getCode())
                .setSort(0).setRemark("system auto-generated");
        Long roleId = roleService.createRole(request, RoleTypeEnum.SYSTEM.getType());
        // assign permissions
        permissionService.assignRoleMenu(roleId, tenantPackage.getMenuIds());
        return roleId;
    }

    @Override
    @DSTransactional // multi datasource: use @DSTransactional to guarantee local transactions and datasource switching
    public void updateTenant(TenantSaveRequest updateRequest) {
        // validate existence
        TenantEntity tenant = validateUpdateTenant(updateRequest.getId());
        // validate that the tenant name is not duplicated
        validTenantNameDuplicate(updateRequest.getName(), updateRequest.getId());
        // validate that the tenant website is not duplicated
        validTenantWebsiteDuplicate(updateRequest.getWebsites(), updateRequest.getId());
        // validate that the package is not disabled
        TenantPackageEntity tenantPackage = tenantPackageService.validTenantPackage(updateRequest.getPackageId());

        // update tenant
        TenantEntity updateObj = BeanUtils.toBean(updateRequest, TenantEntity.class);
        tenantMapper.updateById(updateObj);
        // if the package changed, update its role's permissions
        if (ObjectUtil.notEqual(tenant.getPackageId(), updateRequest.getPackageId())) {
            updateTenantRoleMenu(tenant.getId(), tenantPackage.getMenuIds());
        }
    }

    private void validTenantNameDuplicate(String name, Long id) {
        TenantEntity tenant = tenantMapper.selectByName(name);
        if (tenant == null) {
            return;
        }
        // if id is null, no need to compare against a tenant of the same name
        if (id == null) {
            throw exception(TENANT_NAME_DUPLICATE, name);
        }
        if (!tenant.getId().equals(id)) {
            throw exception(TENANT_NAME_DUPLICATE, name);
        }
    }

    private void validTenantWebsiteDuplicate(List<String> websites, Long excludeId) {
        if (CollUtil.isEmpty(websites)) {
            return;
        }
        websites.forEach(website -> {
            List<TenantEntity> tenants = tenantMapper.selectListByWebsite(website);
            if (excludeId != null) {
                tenants.removeIf(tenant -> tenant.getId().equals(excludeId));
            }
            if (CollUtil.isNotEmpty(tenants)) {
                throw exception(TENANT_WEBSITE_DUPLICATE, website);
            }
        });
    }

    @Override
    @DSTransactional
    public void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds) {
        TenantUtils.execute(tenantId, () -> {
            // get all roles
            List<RoleEntity> roles = roleService.getRoleList();
            roles.forEach(role -> Assert.isTrue(tenantId.equals(role.getTenantId()), "role({}/{}) tenant mismatch",
                    role.getId(), role.getTenantId(), tenantId)); // fallback validation
            // reassign each role's permissions
            roles.forEach(role -> {
                // if tenant admin, reassign its permissions to those of the tenant package
                if (Objects.equals(role.getCode(), RoleCodeEnum.TENANT_ADMIN.getCode())) {
                    permissionService.assignRoleMenu(role.getId(), menuIds);
                    log.info("[updateTenantRoleMenu][tenant admin ({}/{}) permission update is ({})]", role.getId(), role.getTenantId(), menuIds);
                    return;
                }
                // for other roles, remove permissions that exceed the package
                Set<Long> roleMenuIds = permissionService.getRoleMenuListByRoleId(role.getId());
                roleMenuIds = CollUtil.intersectionDistinct(roleMenuIds, menuIds);
                permissionService.assignRoleMenu(role.getId(), roleMenuIds);
                log.info("[updateTenantRoleMenu][role ({}/{}) permission update is ({})]", role.getId(), role.getTenantId(), roleMenuIds);
            });
        });
    }

    @Override
    public void deleteTenant(Long id) {
        // validate existence
        validateUpdateTenant(id);
        // delete
        tenantMapper.deleteById(id);
    }

    @Override
    public void deleteTenantList(List<Long> ids) {
        // 1. validate existence
        ids.forEach(this::validateUpdateTenant);

        // 2. batch delete
        tenantMapper.deleteByIds(ids);
    }

    private TenantEntity validateUpdateTenant(Long id) {
        TenantEntity tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        // built-in tenant, deletion not allowed
        if (isSystemTenant(tenant)) {
            throw exception(TENANT_CAN_NOT_UPDATE_SYSTEM);
        }
        return tenant;
    }

    @Override
    public TenantEntity getTenant(Long id) {
        return tenantMapper.selectById(id);
    }

    @Override
    public PageResult<TenantEntity> getTenantPage(TenantPageRequest pageRequest) {
        return tenantMapper.selectPage(pageRequest);
    }

    @Override
    public TenantEntity getTenantByName(String name) {
        return tenantMapper.selectByName(name);
    }

    @Override
    public TenantEntity getTenantByWebsite(String website) {
        List<TenantEntity> tenants = tenantMapper.selectListByWebsite(website);
        return CollUtil.getFirst(tenants);
    }

    @Override
    public Long getTenantCountByPackageId(Long packageId) {
        return tenantMapper.selectCountByPackageId(packageId);
    }

    @Override
    public List<TenantEntity> getTenantListByPackageId(Long packageId) {
        return tenantMapper.selectListByPackageId(packageId);
    }

    @Override
    public List<TenantEntity> getTenantListByStatus(Integer status) {
        return tenantMapper.selectListByStatus(status);
    }

    @Override
    public void handleTenantInfo(TenantInfoHandler handler) {
        // if disabled, do not execute the logic
        if (isTenantDisable()) {
            return;
        }
        // get tenant
        TenantEntity tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        // execute the handler
        handler.handle(tenant);
    }

    @Override
    public void handleTenantMenu(TenantMenuHandler handler) {
        // if disabled, do not execute the logic
        if (isTenantDisable()) {
            return;
        }
        // get tenant, then get menus
        TenantEntity tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        Set<Long> menuIds;
        if (isSystemTenant(tenant)) { // system tenant: menus are the full set
            menuIds = CollectionUtils.convertSet(menuService.getMenuList(), MenuEntity::getId);
        } else {
            menuIds = tenantPackageService.getTenantPackage(tenant.getPackageId()).getMenuIds();
        }
        // execute the handler
        handler.handle(menuIds);
    }

    private static boolean isSystemTenant(TenantEntity tenant) {
        return Objects.equals(tenant.getPackageId(), TenantEntity.PACKAGE_ID_SYSTEM);
    }

    private boolean isTenantDisable() {
        return tenantProperties == null || Boolean.FALSE.equals(tenantProperties.getEnable());
    }

}
