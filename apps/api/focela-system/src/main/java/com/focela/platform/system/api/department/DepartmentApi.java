package com.focela.platform.system.api.department;

import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.system.api.department.dto.DepartmentRpcResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Department API interface
 */
public interface DepartmentApi {

    /**
     * Get department information
     *
     * @param id department ID
     * @return department information
     */
    @SuppressWarnings("deprecation")
    default DepartmentRpcResponse getDepartment(Long id) {
        return getDept(id);
    }

    /**
     * Get department information
     *
     * @param id department ID
     * @return department information
     * @deprecated use {@link #getDepartment(Long)}
     */
    @Deprecated
    DepartmentRpcResponse getDept(Long id);

    /**
     * Get a list of departments
     *
     * @param ids department IDs
     * @return list of departments
     */
    @SuppressWarnings("deprecation")
    default List<DepartmentRpcResponse> getDepartmentList(Collection<Long> ids) {
        return getDeptList(ids);
    }

    /**
     * Get a list of departments
     *
     * @param ids department IDs
     * @return list of departments
     * @deprecated use {@link #getDepartmentList(Collection)}
     */
    @Deprecated
    List<DepartmentRpcResponse> getDeptList(Collection<Long> ids);

    /**
     * Validate whether the departments are valid. The following cases are considered invalid:
     * 1. department ID does not exist
     * 2. department is disabled
     *
     * @param ids department IDs
     */
    @SuppressWarnings("deprecation")
    default void validateDepartmentList(Collection<Long> ids) {
        validateDeptList(ids);
    }

    /**
     * Validate whether the departments are valid. The following cases are considered invalid:
     * 1. department ID does not exist
     * 2. department is disabled
     *
     * @param ids department IDs
     * @deprecated use {@link #validateDepartmentList(Collection)}
     */
    @Deprecated
    void validateDeptList(Collection<Long> ids);

    /**
     * Get the department Map for the specified IDs
     *
     * @param ids department IDs
     * @return department Map
     */
    default Map<Long, DepartmentRpcResponse> getDepartmentMap(Collection<Long> ids) {
        List<DepartmentRpcResponse> list = getDepartmentList(ids);
        return CollectionUtils.convertMap(list, DepartmentRpcResponse::getId);
    }

    /**
     * Get the department Map for the specified IDs
     *
     * @param ids department IDs
     * @return department Map
     * @deprecated use {@link #getDepartmentMap(Collection)}
     */
    @Deprecated
    default Map<Long, DepartmentRpcResponse> getDeptMap(Collection<Long> ids) {
        return getDepartmentMap(ids);
    }

    /**
     * Get all child departments of the specified department
     *
     * @param id department ID
     * @return list of child departments
     */
    @SuppressWarnings("deprecation")
    default List<DepartmentRpcResponse> getChildDepartmentList(Long id) {
        return getChildDeptList(id);
    }

    /**
     * Get all child departments of the specified department
     *
     * @param id department ID
     * @return list of child departments
     * @deprecated use {@link #getChildDepartmentList(Long)}
     */
    @Deprecated
    List<DepartmentRpcResponse> getChildDeptList(Long id);

}
