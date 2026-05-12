package com.focela.platform.module.system.service.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.common.util.collection.CollectionUtils;
import com.focela.platform.framework.common.util.date.DateUtils;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.framework.datapermission.core.annotation.DataPermission;
import com.focela.platform.framework.tenant.config.TenantProperties;
import com.focela.platform.framework.tenant.core.context.TenantContextHolder;
import com.focela.platform.framework.tenant.core.util.TenantUtils;
import com.focela.platform.module.system.controller.admin.permission.dto.role.RoleSaveRequest;
import com.focela.platform.module.system.controller.admin.tenant.dto.tenant.TenantPageRequest;
import com.focela.platform.module.system.controller.admin.tenant.dto.tenant.TenantSaveRequest;
import com.focela.platform.module.system.convert.tenant.TenantConvert;
import com.focela.platform.module.system.repository.entity.permission.MenuEntity;
import com.focela.platform.module.system.repository.entity.permission.RoleEntity;
import com.focela.platform.module.system.repository.entity.tenant.TenantEntity;
import com.focela.platform.module.system.repository.entity.tenant.TenantPackageEntity;
import com.focela.platform.module.system.repository.mapper.tenant.TenantMapper;
import com.focela.platform.module.system.enums.permission.RoleCodeEnum;
import com.focela.platform.module.system.enums.permission.RoleTypeEnum;
import com.focela.platform.module.system.service.permission.MenuService;
import com.focela.platform.module.system.service.permission.PermissionService;
import com.focela.platform.module.system.service.permission.RoleService;
import com.focela.platform.module.system.service.tenant.handler.TenantInfoHandler;
import com.focela.platform.module.system.service.tenant.handler.TenantMenuHandler;
import com.focela.platform.module.system.service.user.AdminUserService;
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

import static com.focela.platform.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.*;
import static java.util.Collections.singleton;

/**
 * 租户 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class TenantServiceImpl implements TenantService {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false) // 由于 yudao.tenant.enable 配置项，可以关闭多租户的功能，所以这里只能不强制注入
    private TenantProperties tenantProperties;

    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private AdminUserService userService;
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
    @DSTransactional // 多数据源，使用 @DSTransactional 保证本地事务，以及数据源的切换
    @DataPermission(enable = false) // 参见 https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/1154 说明
    public Long createTenant(TenantSaveRequest createRequest) {
        // 校验租户名称是否重复
        validTenantNameDuplicate(createRequest.getName(), null);
        // 校验租户域名是否重复
        validTenantWebsiteDuplicate(createRequest.getWebsites(), null);
        // 校验套餐被禁用
        TenantPackageEntity tenantPackage = tenantPackageService.validTenantPackage(createRequest.getPackageId());

        // 创建租户
        TenantEntity tenant = BeanUtils.toBean(createRequest, TenantEntity.class);
        tenantMapper.insert(tenant);
        // 创建租户的管理员
        TenantUtils.execute(tenant.getId(), () -> {
            // 创建角色
            Long roleId = createRole(tenantPackage);
            // 创建用户，并分配角色
            Long userId = createUser(roleId, createRequest);
            // 修改租户的管理员
            tenantMapper.updateById(new TenantEntity().setId(tenant.getId()).setContactUserId(userId));
        });
        return tenant.getId();
    }

    private Long createUser(Long roleId, TenantSaveRequest createRequest) {
        // 创建用户
        Long userId = userService.createUser(TenantConvert.INSTANCE.convert02(createRequest));
        // 分配角色
        permissionService.assignUserRole(userId, singleton(roleId));
        return userId;
    }

    private Long createRole(TenantPackageEntity tenantPackage) {
        // 创建角色
        RoleSaveRequest reqVO = new RoleSaveRequest();
        reqVO.setName(RoleCodeEnum.TENANT_ADMIN.getName()).setCode(RoleCodeEnum.TENANT_ADMIN.getCode())
                .setSort(0).setRemark("系统自动生成");
        Long roleId = roleService.createRole(reqVO, RoleTypeEnum.SYSTEM.getType());
        // 分配权限
        permissionService.assignRoleMenu(roleId, tenantPackage.getMenuIds());
        return roleId;
    }

    @Override
    @DSTransactional // 多数据源，使用 @DSTransactional 保证本地事务，以及数据源的切换
    public void updateTenant(TenantSaveRequest updateRequest) {
        // 校验存在
        TenantEntity tenant = validateUpdateTenant(updateRequest.getId());
        // 校验租户名称是否重复
        validTenantNameDuplicate(updateRequest.getName(), updateRequest.getId());
        // 校验租户域名是否重复
        validTenantWebsiteDuplicate(updateRequest.getWebsites(), updateRequest.getId());
        // 校验套餐被禁用
        TenantPackageEntity tenantPackage = tenantPackageService.validTenantPackage(updateRequest.getPackageId());

        // 更新租户
        TenantEntity updateObj = BeanUtils.toBean(updateRequest, TenantEntity.class);
        tenantMapper.updateById(updateObj);
        // 如果套餐发生变化，则修改其角色的权限
        if (ObjectUtil.notEqual(tenant.getPackageId(), updateRequest.getPackageId())) {
            updateTenantRoleMenu(tenant.getId(), tenantPackage.getMenuIds());
        }
    }

    private void validTenantNameDuplicate(String name, Long id) {
        TenantEntity tenant = tenantMapper.selectByName(name);
        if (tenant == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同名字的租户
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
            // 获得所有角色
            List<RoleEntity> roles = roleService.getRoleList();
            roles.forEach(role -> Assert.isTrue(tenantId.equals(role.getTenantId()), "角色({}/{}) 租户不匹配",
                    role.getId(), role.getTenantId(), tenantId)); // 兜底校验
            // 重新分配每个角色的权限
            roles.forEach(role -> {
                // 如果是租户管理员，重新分配其权限为租户套餐的权限
                if (Objects.equals(role.getCode(), RoleCodeEnum.TENANT_ADMIN.getCode())) {
                    permissionService.assignRoleMenu(role.getId(), menuIds);
                    log.info("[updateTenantRoleMenu][租户管理员({}/{}) 的权限修改为({})]", role.getId(), role.getTenantId(), menuIds);
                    return;
                }
                // 如果是其他角色，则去掉超过套餐的权限
                Set<Long> roleMenuIds = permissionService.getRoleMenuListByRoleId(role.getId());
                roleMenuIds = CollUtil.intersectionDistinct(roleMenuIds, menuIds);
                permissionService.assignRoleMenu(role.getId(), roleMenuIds);
                log.info("[updateTenantRoleMenu][角色({}/{}) 的权限修改为({})]", role.getId(), role.getTenantId(), roleMenuIds);
            });
        });
    }

    @Override
    public void deleteTenant(Long id) {
        // 校验存在
        validateUpdateTenant(id);
        // 删除
        tenantMapper.deleteById(id);
    }

    @Override
    public void deleteTenantList(List<Long> ids) {
        // 1. 校验存在
        ids.forEach(this::validateUpdateTenant);

        // 2. 批量删除
        tenantMapper.deleteByIds(ids);
    }

    private TenantEntity validateUpdateTenant(Long id) {
        TenantEntity tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        // 内置租户，不允许删除
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
        // 如果禁用，则不执行逻辑
        if (isTenantDisable()) {
            return;
        }
        // 获得租户
        TenantEntity tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        // 执行处理器
        handler.handle(tenant);
    }

    @Override
    public void handleTenantMenu(TenantMenuHandler handler) {
        // 如果禁用，则不执行逻辑
        if (isTenantDisable()) {
            return;
        }
        // 获得租户，然后获得菜单
        TenantEntity tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        Set<Long> menuIds;
        if (isSystemTenant(tenant)) { // 系统租户，菜单是全量的
            menuIds = CollectionUtils.convertSet(menuService.getMenuList(), MenuEntity::getId);
        } else {
            menuIds = tenantPackageService.getTenantPackage(tenant.getPackageId()).getMenuIds();
        }
        // 执行处理器
        handler.handle(menuIds);
    }

    private static boolean isSystemTenant(TenantEntity tenant) {
        return Objects.equals(tenant.getPackageId(), TenantEntity.PACKAGE_ID_SYSTEM);
    }

    private boolean isTenantDisable() {
        return tenantProperties == null || Boolean.FALSE.equals(tenantProperties.getEnable());
    }

}
