package com.focela.platform.system.service.department;

import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.system.controller.admin.department.request.department.DepartmentListRequest;
import com.focela.platform.system.controller.admin.department.request.department.DepartmentSaveRequest;
import com.focela.platform.system.domain.entity.department.DepartmentEntity;

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
    Long createDepartment(DepartmentSaveRequest createRequest);

    /**
     * Update a department
     *
     * @param updateRequest department information
     */
    void updateDepartment(DepartmentSaveRequest updateRequest);

    /**
     * Delete a department
     *
     * @param id department ID
     */
    void deleteDepartment(Long id);

    /**
     * Batch delete departments
     *
     * @param ids department ID array
     */
    void deleteDepartmentList(List<Long> ids);

    /**
     * Get department information
     *
     * @param id department ID
     * @return department information
     */
    DepartmentEntity getDepartment(Long id);

    /**
     * Get the department information array
     *
     * @param ids department ID array
     * @return department information array
     */
    List<DepartmentEntity> getDepartmentList(Collection<Long> ids);

    /**
     * Filter the department list
     *
     * @param request filter request
     * @return department list
     */
    List<DepartmentEntity> getDepartmentList(DepartmentListRequest request);

    /**
     * Get a department Map by the specified IDs
     *
     * @param ids department ID array
     * @return department Map
     */
    default Map<Long, DepartmentEntity> getDepartmentMap(Collection<Long> ids) {
        List<DepartmentEntity> departments = getDepartmentList(ids);
        return CollectionUtils.convertMap(departments, DepartmentEntity::getId);
    }

    /**
     * Get all child departments of the specified department
     *
     * @param id department ID
     * @return child department list
     */
    default List<DepartmentEntity> getChildDepartmentList(Long id) {
        return getChildDepartmentList(Collections.singleton(id));
    }

    /**
     * Get all child departments of the specified departments
     *
     * @param ids department ID array
     * @return child department list
     */
    List<DepartmentEntity> getChildDepartmentList(Collection<Long> ids);

    /**
     * Get the department list for the specified leader
     *
     * @param id leader ID
     * @return department list
     */
    List<DepartmentEntity> getDepartmentListByLeaderUserId(Long id);

    /**
     * Get all child departments from cache
     *
     * @param id parent department ID
     * @return child department list
     */
    Set<Long> getChildDepartmentIdListFromCache(Long id);

    /**
     * Validate whether the departments are valid. The following are considered invalid:
     * 1. Department ID does not exist
     * 2. Department is disabled
     *
     * @param ids department ID collection
     */
    void validateDepartmentList(Collection<Long> ids);

}
