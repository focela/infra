package com.focela.platform.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.datapermission.core.annotation.DataPermission;
import com.focela.platform.common.api.system.permission.dto.DepartmentDataPermissionRpcResponse;
import com.focela.platform.system.domain.entity.permission.MenuEntity;
import com.focela.platform.system.domain.entity.permission.RoleEntity;
import com.focela.platform.system.domain.entity.permission.RoleMenuEntity;
import com.focela.platform.system.domain.entity.permission.UserRoleEntity;
import com.focela.platform.system.repository.mapper.permission.RoleMenuMapper;
import com.focela.platform.system.repository.mapper.permission.UserRoleMapper;
import com.focela.platform.system.constants.RedisKeyConstants;
import com.focela.platform.system.enums.permission.DataScopeEnum;
import com.focela.platform.system.service.department.DepartmentService;
import com.focela.platform.system.service.user.UserService;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import static com.focela.platform.common.utils.collection.CollectionUtils.convertSet;
import static com.focela.platform.common.utils.json.JsonUtils.toJsonString;

/**
 * Permission Service implementation class
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultPermissionService implements PermissionService {

    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;

    private final RoleService roleService;
    private final MenuService menuService;
    private final DepartmentService deptService;

    /**
     * Lazy field injection breaks the {@code DefaultPermissionService} ↔
     * {@code DefaultUserService} cycle.
     * See MODULE_TEMPLATE.md §12.5.
     */
    @Resource
    @Lazy
    private UserService userService;

    @Override
    public boolean hasAnyPermissions(Long userId, String... permissions) {
        // If empty, treat as already authorized
        if (ArrayUtil.isEmpty(permissions)) {
            return true;
        }

        // Get the current login roles. If empty, the user has no permission
        List<RoleEntity> roles = getEnableUserRoleListByUserIdFromCache(userId);
        if (CollUtil.isEmpty(roles)) {
            return false;
        }

        // Case 1: iterate each permission; if any is satisfied, the user has permission
        for (String permission : permissions) {
            if (hasAnyPermission(roles, permission)) {
                return true;
            }
        }

        // Case 2: if the user is a super admin, also treat as having permission
        return roleService.hasAnySuperAdmin(convertSet(roles, RoleEntity::getId));
    }

    /**
     * Check whether the specified roles have the given permission
     *
     * @param roles specified role array
     * @param permission permission identifier
     * @return whether granted
     */
    private boolean hasAnyPermission(List<RoleEntity> roles, String permission) {
        List<Long> menuIds = menuService.getMenuIdListByPermissionFromCache(permission);
        // Strict mode: if the permission has no corresponding Menu, treat as no permission
        if (CollUtil.isEmpty(menuIds)) {
            return false;
        }

        // Check whether the user has permission
        Set<Long> roleIds = convertSet(roles, RoleEntity::getId);
        for (Long menuId : menuIds) {
            // Get the role ID set that owns this menu
            Set<Long> menuRoleIds = getSelf().getMenuRoleIdListByMenuIdFromCache(menuId);
            // If there is an intersection, the user has permission
            if (CollUtil.containsAny(menuRoleIds, roleIds)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAnyRoles(Long userId, String... roles) {
        // If empty, treat as already authorized
        if (ArrayUtil.isEmpty(roles)) {
            return true;
        }

        // Get the current login roles. If empty, the user has no permission
        List<RoleEntity> roleList = getEnableUserRoleListByUserIdFromCache(userId);
        if (CollUtil.isEmpty(roleList)) {
            return false;
        }

        // Check whether the user has any of the roles
        Set<String> userRoles = convertSet(roleList, RoleEntity::getCode);
        return CollUtil.containsAny(userRoles, Sets.newHashSet(roles));
    }

    // ========== Role-menu related methods ==========

    @Override
    @DSTransactional // multi-datasource: use @DSTransactional to keep local transactions and switch data sources
    @Caching(evict = {
            @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST,
            allEntries = true),
            @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST,
            allEntries = true) // allEntries clears all caches: a single update may involve many menuIds, so batch clearing is faster
    })
    public void assignRoleMenu(Long roleId, Set<Long> menuIds) {
        // Get the menu IDs owned by the role
        Set<Long> dbMenuIds = convertSet(roleMenuMapper.selectListByRoleId(roleId), RoleMenuEntity::getMenuId);
        // Compute menu IDs to add and remove
        Set<Long> menuIdList = CollUtil.emptyIfNull(menuIds);
        Collection<Long> createMenuIds = CollUtil.subtract(menuIdList, dbMenuIds);
        Collection<Long> deleteMenuIds = CollUtil.subtract(dbMenuIds, menuIdList);
        // Perform add and remove. For already authorized menus, no action is needed
        if (CollUtil.isNotEmpty(createMenuIds)) {
            roleMenuMapper.insertBatch(CollectionUtils.convertList(createMenuIds, menuId -> {
                RoleMenuEntity entity = new RoleMenuEntity();
                entity.setRoleId(roleId);
                entity.setMenuId(menuId);
                return entity;
            }));
        }
        if (CollUtil.isNotEmpty(deleteMenuIds)) {
            roleMenuMapper.deleteListByRoleIdAndMenuIds(roleId, deleteMenuIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST,
                    allEntries = true), // allEntries clears all caches: cannot easily obtain the menu caches related to roleId here
            @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST,
                    allEntries = true) // allEntries clears all caches: cannot easily obtain the user caches related to roleId here
    })
    public void processRoleDeleted(Long roleId) {
        // Mark UserRole as deleted
        userRoleMapper.deleteListByRoleId(roleId);
        // Mark RoleMenu as deleted
        roleMenuMapper.deleteListByRoleId(roleId);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST, key = "#menuId")
    public void processMenuDeleted(Long menuId) {
        roleMenuMapper.deleteListByMenuId(menuId);
    }

    @Override
    public Set<Long> getRoleMenuListByRoleId(Collection<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptySet();
        }

        // For admin, return all menu IDs
        if (roleService.hasAnySuperAdmin(roleIds)) {
            return convertSet(menuService.getMenuList(), MenuEntity::getId);
        }
        // For non-admin, return the owned menu IDs
        return convertSet(roleMenuMapper.selectListByRoleId(roleIds), RoleMenuEntity::getMenuId);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.MENU_ROLE_ID_LIST, key = "#menuId")
    public Set<Long> getMenuRoleIdListByMenuIdFromCache(Long menuId) {
        return convertSet(roleMenuMapper.selectListByMenuId(menuId), RoleMenuEntity::getRoleId);
    }

    // ========== User-role related methods ==========

    @Override
    @DSTransactional // multi-datasource: use @DSTransactional to keep local transactions and switch data sources
    @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public void assignUserRole(Long userId, Set<Long> roleIds) {
        // Get the role IDs owned by the user
        Set<Long> dbRoleIds = convertSet(userRoleMapper.selectListByUserId(userId),
                UserRoleEntity::getRoleId);
        // Compute role IDs to add and remove
        Set<Long> roleIdList = CollUtil.emptyIfNull(roleIds);
        Collection<Long> createRoleIds = CollUtil.subtract(roleIdList, dbRoleIds);
        Collection<Long> deleteMenuIds = CollUtil.subtract(dbRoleIds, roleIdList);
        // Perform add and remove. For already authorized roles, no action is needed
        if (!CollectionUtil.isEmpty(createRoleIds)) {
            userRoleMapper.insertBatch(CollectionUtils.convertList(createRoleIds, roleId -> {
                UserRoleEntity entity = new UserRoleEntity();
                entity.setUserId(userId);
                entity.setRoleId(roleId);
                return entity;
            }));
        }
        if (!CollectionUtil.isEmpty(deleteMenuIds)) {
            userRoleMapper.deleteListByUserIdAndRoleIdIds(userId, deleteMenuIds);
        }
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public void processUserDeleted(Long userId) {
        userRoleMapper.deleteListByUserId(userId);
    }

    @Override
    public Set<Long> getUserRoleIdListByUserId(Long userId) {
        return convertSet(userRoleMapper.selectListByUserId(userId), UserRoleEntity::getRoleId);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public Set<Long> getUserRoleIdListByUserIdFromCache(Long userId) {
        return getUserRoleIdListByUserId(userId);
    }

    @Override
    public Set<Long> getUserRoleIdListByRoleId(Collection<Long> roleIds) {
        return convertSet(userRoleMapper.selectListByRoleIds(roleIds), UserRoleEntity::getUserId);
    }

    /**
     * Get the roles owned by the user, with only enabled roles included
     *
     * @param userId user ID
     * @return roles owned by the user
     */
    @VisibleForTesting
    List<RoleEntity> getEnableUserRoleListByUserIdFromCache(Long userId) {
        // Get the role IDs owned by the user
        Set<Long> roleIds = getSelf().getUserRoleIdListByUserIdFromCache(userId);
        // Get the role array and remove disabled ones
        List<RoleEntity> roles = roleService.getRoleListFromCache(roleIds);
        roles.removeIf(role -> !CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus()));
        return roles;
    }

    // ========== User-department related methods ==========

    @Override
    public void assignRoleDataScope(Long roleId, Integer dataScope, Set<Long> dataScopeDeptIds) {
        roleService.updateRoleDataScope(roleId, dataScope, dataScopeDeptIds);
    }

    @Override
    @DataPermission(enable = false) // disable data permission, otherwise recursive data permission lookup would occur
    public DepartmentDataPermissionRpcResponse getDeptDataPermission(Long userId) {
        // Get the user's roles
        List<RoleEntity> roles = getEnableUserRoleListByUserIdFromCache(userId);

        // If roles are empty, the user can only view their own data
        DepartmentDataPermissionRpcResponse result = new DepartmentDataPermissionRpcResponse();
        if (CollUtil.isEmpty(roles)) {
            result.setSelf(true);
            return result;
        }

        // Cache the user's department ID via Guava Suppliers for lazy evaluation, so the DB is queried at most once
        Supplier<Long> userDeptId = Suppliers.memoize(() -> userService.getUser(userId).getDeptId());
        // Iterate each role and compute
        for (RoleEntity role : roles) {
            // Skip when null
            if (role.getDataScope() == null) {
                continue;
            }
            // Case 1: ALL
            if (Objects.equals(role.getDataScope(), DataScopeEnum.ALL.getScope())) {
                result.setAll(true);
                continue;
            }
            // Case 2: DEPT_CUSTOM
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_CUSTOM.getScope())) {
                CollUtil.addAll(result.getDeptIds(), role.getDataScopeDeptIds());
                // For custom visible departments, ensure the user's own department is also visible. Otherwise issues may occur.
                // For example, on login, the t_user lookup by username may be filtered out by dept_id
                CollUtil.addAll(result.getDeptIds(), userDeptId.get());
                continue;
            }
            // Case 3: DEPT_ONLY
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_ONLY.getScope())) {
                CollectionUtils.addIfNotNull(result.getDeptIds(), userDeptId.get());
                continue;
            }
            // Case 4: DEPT_DEPT_AND_CHILD
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_AND_CHILD.getScope())) {
                CollUtil.addAll(result.getDeptIds(), deptService.getChildDeptIdListFromCache(userDeptId.get()));
                // Add the user's own department ID
                CollUtil.addAll(result.getDeptIds(), userDeptId.get());
                continue;
            }
            // Case 5: SELF
            if (Objects.equals(role.getDataScope(), DataScopeEnum.SELF.getScope())) {
                result.setSelf(true);
                continue;
            }
            // Unknown case: log as error
            log.error("[getDeptDataPermission][LoginUser({}) role({}) cannot process]", userId, toJsonString(result));
        }
        return result;
    }

    /**
     * Get the self proxy object to make AOP work
     *
     * @return self
     */
    private DefaultPermissionService getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
