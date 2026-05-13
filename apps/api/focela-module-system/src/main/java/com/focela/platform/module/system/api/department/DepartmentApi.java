package com.focela.platform.module.system.api.department;

import com.focela.platform.framework.common.utils.collection.CollectionUtils;
import com.focela.platform.module.system.api.department.dto.DepartmentRpcResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 部门 API 接口
 */
public interface DepartmentApi {

    /**
     * 获得部门信息
     *
     * @param id 部门编号
     * @return 部门信息
     */
    DepartmentRpcResponse getDept(Long id);

    /**
     * 获得部门信息数组
     *
     * @param ids 部门编号数组
     * @return 部门信息数组
     */
    List<DepartmentRpcResponse> getDeptList(Collection<Long> ids);

    /**
     * 校验部门们是否有效。如下情况，视为无效：
     * 1. 部门编号不存在
     * 2. 部门被禁用
     *
     * @param ids 角色编号数组
     */
    void validateDeptList(Collection<Long> ids);

    /**
     * 获得指定编号的部门 Map
     *
     * @param ids 部门编号数组
     * @return 部门 Map
     */
    default Map<Long, DepartmentRpcResponse> getDeptMap(Collection<Long> ids) {
        List<DepartmentRpcResponse> list = getDeptList(ids);
        return CollectionUtils.convertMap(list, DepartmentRpcResponse::getId);
    }

    /**
     * 获得指定部门的所有子部门
     *
     * @param id 部门编号
     * @return 子部门列表
     */
    List<DepartmentRpcResponse> getChildDeptList(Long id);

}
