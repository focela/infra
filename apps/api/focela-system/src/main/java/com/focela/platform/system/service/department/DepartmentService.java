package com.focela.platform.system.service.department;

import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.system.controller.admin.department.dto.dept.DepartmentListRequest;
import com.focela.platform.system.controller.admin.department.dto.dept.DepartmentSaveRequest;
import com.focela.platform.system.entity.department.DepartmentEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Department Service interface
 */
public interface DepartmentService {

    /**
     * Create a department
     *
     * @param createRequest department information
     * @return department ID
     */
    Long createDept(DepartmentSaveRequest createRequest);

    /**
     * Update a department
     *
     * @param updateRequest department information
     */
    void updateDept(DepartmentSaveRequest updateRequest);

    /**
     * Delete a department
     *
     * @param id department ID
     */
    void deleteDept(Long id);

    /**
     * Batch delete departments
     *
     * @param ids department ID array
     */
    void deleteDeptList(List<Long> ids);

    /**
     * Get department information
     *
     * @param id department ID
     * @return department information
     */
    DepartmentEntity getDept(Long id);

    /**
     * Get the department information array
     *
     * @param ids department ID array
     * @return department information array
     */
    List<DepartmentEntity> getDeptList(Collection<Long> ids);

    /**
     * Filter the department list
     *
     * @param request filter request DTO
     * @return department list
     */
    List<DepartmentEntity> getDeptList(DepartmentListRequest request);

    /**
     * Get a department Map by the specified IDs
     *
     * @param ids department ID array
     * @return department Map
     */
    default Map<Long, DepartmentEntity> getDeptMap(Collection<Long> ids) {
        List<DepartmentEntity> list = getDeptList(ids);
        return CollectionUtils.convertMap(list, DepartmentEntity::getId);
    }

    /**
     * Get all child departments of the specified department
     *
     * @param id department ID
     * @return child department list
     */
    default List<DepartmentEntity> getChildDeptList(Long id) {
        return getChildDeptList(Collections.singleton(id));
    }

    /**
     * Get all child departments of the specified departments
     *
     * @param ids department ID array
     * @return child department list
     */
    List<DepartmentEntity> getChildDeptList(Collection<Long> ids);

    /**
     * Get the department list for the specified leader
     *
     * @param id leader ID
     * @return department list
     */
    List<DepartmentEntity> getDeptListByLeaderUserId(Long id);

    /**
     * Get all child departments from cache
     *
     * @param id parent department ID
     * @return child department list
     */
    Set<Long> getChildDeptIdListFromCache(Long id);

    /**
     * Validate whether the departments are valid. The following are considered invalid:
     * 1. Department ID does not exist
     * 2. Department is disabled
     *
     * @param ids role ID array
     */
    void validateDeptList(Collection<Long> ids);

}
