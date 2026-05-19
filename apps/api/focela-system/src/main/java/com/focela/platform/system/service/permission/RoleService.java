package com.focela.platform.system.service.permission;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.permission.request.role.RolePageRequest;
import com.focela.platform.system.controller.admin.permission.request.role.RoleSaveRequest;
import com.focela.platform.system.domain.entity.permission.RoleEntity;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Role Service interface
 */
public interface RoleService {

    /**
     * Create a role
     *
     * @param createRequest role creation info
     * @param type role type
     * @return role ID
     */
    Long createRole(@Valid RoleSaveRequest createRequest, Integer type);

    /**
     * Update a role
     *
     * @param updateRequest role update info
     */
    void updateRole(@Valid RoleSaveRequest updateRequest);

    /**
     * Delete a role
     *
     * @param id role ID
     */
    void deleteRole(Long id);

    /**
     * Batch delete roles
     *
     * @param ids role ID array
     */
    void deleteRoleList(List<Long> ids);

    /**
     * Set the data permission for a role
     *
     * @param id role ID
     * @param dataScope data scope
     * @param dataScopeDeptIds department ID array
     */
    void updateRoleDataScope(Long id, Integer dataScope, Set<Long> dataScopeDeptIds);

    /**
     * Get a role
     *
     * @param id role ID
     * @return role
     */
    RoleEntity getRole(Long id);

    /**
     * Get a role from cache
     *
     * @param id role ID
     * @return role
     */
    RoleEntity getRoleFromCache(Long id);

    /**
     * Get role list
     *
     * @param ids role ID array
     * @return role list
     */
    List<RoleEntity> getRoleList(Collection<Long> ids);

    /**
     * Get role array from cache
     *
     * @param ids role ID array
     * @return role array
     */
    List<RoleEntity> getRoleListFromCache(Collection<Long> ids);

    /**
     * Get role list
     *
     * @param statuses filter statuses
     * @return role list
     */
    List<RoleEntity> getRoleListByStatus(Collection<Integer> statuses);

    /**
     * Get the full list of roles
     *
     * @return role list
     */
    List<RoleEntity> getRoleList();

    /**
     * Get paginated roles
     *
     * @param request role pagination query
     * @return paginated role result
     */
    PageResult<RoleEntity> getRolePage(RolePageRequest request);

    /**
     * Check whether the role ID array contains a super admin
     *
     * @param ids role ID array
     * @return whether a super admin is present
     */
    boolean hasAnySuperAdmin(Collection<Long> ids);

    /**
     * Validate whether the roles are valid. The following cases are considered invalid:
     * 1. The role ID does not exist
     * 2. The role is disabled
     *
     * @param ids role ID array
     */
    void validateRoleList(Collection<Long> ids);

}
