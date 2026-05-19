package com.focela.platform.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.permission.dto.role.RolePageRequest;
import com.focela.platform.system.controller.admin.permission.dto.role.RoleSaveRequest;
import com.focela.platform.system.domain.entity.permission.RoleEntity;
import com.focela.platform.system.repository.mapper.permission.RoleMapper;
import com.focela.platform.system.constants.RedisKeyConstants;
import com.focela.platform.system.enums.permission.DataScopeEnum;
import com.focela.platform.system.enums.permission.RoleCodeEnum;
import com.focela.platform.system.enums.permission.RoleTypeEnum;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.common.utils.collection.CollectionUtils.convertMap;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static com.focela.platform.system.constants.LogRecordConstants.*;

/**
 * Role Service implementation class
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultRoleService implements RoleService {

        private final PermissionService permissionService;

        private final RoleMapper roleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_CREATE_SUB_TYPE, bizNo = "{{#role.id}}",
            success = SYSTEM_ROLE_CREATE_SUCCESS)
    public Long createRole(RoleSaveRequest createRequest, Integer type) {
        // 1. Validate the role
        validateRoleDuplicate(createRequest.getName(), createRequest.getCode(), null);

        // 2. Insert into the database
        RoleEntity role = BeanUtils.toBean(createRequest, RoleEntity.class)
                .setType(ObjectUtil.defaultIfNull(type, RoleTypeEnum.CUSTOM.getType()))
                .setStatus(ObjUtil.defaultIfNull(createRequest.getStatus(), CommonStatusEnum.ENABLE.getStatus()))
                .setDataScope(DataScopeEnum.ALL.getScope()); // Default to view all data, since some projects may not need project-level permissions
        roleMapper.insert(role);

        // 3. Record operate log context
        LogRecordContext.putVariable("role", role);
        return role.getId();
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#updateRequest.id")
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_UPDATE_SUB_TYPE, bizNo = "{{#updateRequest.id}}",
            success = SYSTEM_ROLE_UPDATE_SUCCESS)
    public void updateRole(RoleSaveRequest updateRequest) {
        // 1.1 Validate whether it can be updated
        RoleEntity role = validateRoleForUpdate(updateRequest.getId());
        // 1.2 Validate uniqueness of the role's unique fields
        validateRoleDuplicate(updateRequest.getName(), updateRequest.getCode(), updateRequest.getId());

        // 2. Update in the database
        RoleEntity updateObj = BeanUtils.toBean(updateRequest, RoleEntity.class);
        roleMapper.updateById(updateObj);

        // 3. Record operate log context
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(role, RoleSaveRequest.class));
        LogRecordContext.putVariable("role", role);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#id")
    public void updateRoleDataScope(Long id, Integer dataScope, Set<Long> dataScopeDeptIds) {
        // Validate whether it can be updated
        validateRoleForUpdate(id);

        // Update data scope
        RoleEntity updateObject = new RoleEntity();
        updateObject.setId(id);
        updateObject.setDataScope(dataScope);
        updateObject.setDataScopeDeptIds(dataScopeDeptIds);
        roleMapper.updateById(updateObject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#id")
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_ROLE_DELETE_SUCCESS)
    public void deleteRole(Long id) {
        // 1. Validate whether it can be updated
        RoleEntity role = validateRoleForUpdate(id);

        // 2.1 Mark as deleted
        roleMapper.deleteById(id);
        // 2.2 Delete related data
        permissionService.processRoleDeleted(id);

        // 3. Record operate log context
        LogRecordContext.putVariable("role", role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleList(List<Long> ids) {
        // 1. Validate whether they can be deleted
        ids.forEach(this::validateRoleForUpdate);

        // 2.1 Mark as deleted
        roleMapper.deleteByIds(ids);
        // 2.2 Delete related data
        ids.forEach(id -> permissionService.processRoleDeleted(id));
    }

    /**
     * Validate uniqueness of the role's unique fields
     *
     * 1. Whether a role with the same name exists
     * 2. Whether a role with the same code exists
     *
     * @param name role name
     * @param code role code
     * @param id role ID
     */
    @VisibleForTesting
    void validateRoleDuplicate(String name, String code, Long id) {
        // 0. Super admin is not allowed to be created
        if (RoleCodeEnum.isSuperAdmin(code)) {
            throw exception(ROLE_ADMIN_CODE_ERROR, code);
        }
        // 1. The name is already used by another role
        RoleEntity role = roleMapper.selectByName(name);
        if (role != null && !role.getId().equals(id)) {
            throw exception(ROLE_NAME_DUPLICATE, name);
        }
        // 2. Whether a role with the same code exists
        if (!StringUtils.hasText(code)) {
            return;
        }
        // The code is already used by another role
        role = roleMapper.selectByCode(code);
        if (role != null && !role.getId().equals(id)) {
            throw exception(ROLE_CODE_DUPLICATE, code);
        }
    }

    /**
     * Validate whether the role can be updated
     *
     * @param id role ID
     */
    @VisibleForTesting
    RoleEntity validateRoleForUpdate(Long id) {
        RoleEntity role = roleMapper.selectById(id);
        if (role == null) {
            throw exception(ROLE_NOT_EXISTS);
        }
        // Built-in roles cannot be deleted
        if (RoleTypeEnum.SYSTEM.getType().equals(role.getType())) {
            throw exception(ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
        }
        return role;
    }

    @Override
    public RoleEntity getRole(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.ROLE, key = "#id",
            unless = "#result == null")
    public RoleEntity getRoleFromCache(Long id) {
        return roleMapper.selectById(id);
    }


    @Override
    public List<RoleEntity> getRoleListByStatus(Collection<Integer> statuses) {
        return roleMapper.selectListByStatus(statuses);
    }

    @Override
    public List<RoleEntity> getRoleList() {
        return roleMapper.selectList();
    }

    @Override
    public List<RoleEntity> getRoleList(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return roleMapper.selectByIds(ids);
    }

    @Override
    public List<RoleEntity> getRoleListFromCache(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        // Use a for loop to fetch from cache, mainly because Spring CacheManager does not support batch operations
        DefaultRoleService self = getSelf();
        return CollectionUtils.convertList(ids, self::getRoleFromCache);
    }

    @Override
    public PageResult<RoleEntity> getRolePage(RolePageRequest request) {
        return roleMapper.selectPage(request);
    }

    @Override
    public boolean hasAnySuperAdmin(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return false;
        }
        DefaultRoleService self = getSelf();
        return ids.stream().anyMatch(id -> {
            RoleEntity role = self.getRoleFromCache(id);
            return role != null && RoleCodeEnum.isSuperAdmin(role.getCode());
        });
    }

    @Override
    public void validateRoleList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // Get role info
        List<RoleEntity> roles = roleMapper.selectByIds(ids);
        Map<Long, RoleEntity> roleMap = convertMap(roles, RoleEntity::getId);
        // Validate
        ids.forEach(id -> {
            RoleEntity role = roleMap.get(id);
            if (role == null) {
                throw exception(ROLE_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())) {
                throw exception(ROLE_IS_DISABLE, role.getName());
            }
        });
    }

    /**
     * Get the self proxy object to make AOP work
     *
     * @return self
     */
    private DefaultRoleService getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
