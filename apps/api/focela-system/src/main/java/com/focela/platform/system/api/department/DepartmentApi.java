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
    DepartmentRpcResponse getDept(Long id);

    /**
     * Get a list of departments
     *
     * @param ids department IDs
     * @return list of departments
     */
    List<DepartmentRpcResponse> getDeptList(Collection<Long> ids);

    /**
     * Validate whether the departments are valid. The following cases are considered invalid:
     * 1. department ID does not exist
     * 2. department is disabled
     *
     * @param ids department IDs
     */
    void validateDeptList(Collection<Long> ids);

    /**
     * Get the department Map for the specified IDs
     *
     * @param ids department IDs
     * @return department Map
     */
    default Map<Long, DepartmentRpcResponse> getDeptMap(Collection<Long> ids) {
        List<DepartmentRpcResponse> list = getDeptList(ids);
        return CollectionUtils.convertMap(list, DepartmentRpcResponse::getId);
    }

    /**
     * Get all child departments of the specified department
     *
     * @param id department ID
     * @return list of child departments
     */
    List<DepartmentRpcResponse> getChildDeptList(Long id);

}
