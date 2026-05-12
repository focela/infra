package com.focela.platform.module.system.service.department;

import com.focela.platform.framework.common.utils.collection.CollectionUtils;
import com.focela.platform.module.system.controller.admin.department.dto.dept.DepartmentListRequest;
import com.focela.platform.module.system.controller.admin.department.dto.dept.DepartmentSaveRequest;
import com.focela.platform.module.system.repository.entity.department.DepartmentEntity;

import java.util.*;

/**
 * 部门 Service 接口
 *
 * @author 芋道源码
 */
public interface DepartmentService {

    /**
     * 创建部门
     *
     * @param createRequest 部门信息
     * @return 部门编号
     */
    Long createDept(DepartmentSaveRequest createRequest);

    /**
     * 更新部门
     *
     * @param updateRequest 部门信息
     */
    void updateDept(DepartmentSaveRequest updateRequest);

    /**
     * 删除部门
     *
     * @param id 部门编号
     */
    void deleteDept(Long id);

    /**
     * 批量删除部门
     *
     * @param ids 部门编号数组
     */
    void deleteDeptList(List<Long> ids);

    /**
     * 获得部门信息
     *
     * @param id 部门编号
     * @return 部门信息
     */
    DepartmentEntity getDept(Long id);

    /**
     * 获得部门信息数组
     *
     * @param ids 部门编号数组
     * @return 部门信息数组
     */
    List<DepartmentEntity> getDeptList(Collection<Long> ids);

    /**
     * 筛选部门列表
     *
     * @param request 筛选条件请求 VO
     * @return 部门列表
     */
    List<DepartmentEntity> getDeptList(DepartmentListRequest request);

    /**
     * 获得指定编号的部门 Map
     *
     * @param ids 部门编号数组
     * @return 部门 Map
     */
    default Map<Long, DepartmentEntity> getDeptMap(Collection<Long> ids) {
        List<DepartmentEntity> list = getDeptList(ids);
        return CollectionUtils.convertMap(list, DepartmentEntity::getId);
    }

    /**
     * 获得指定部门的所有子部门
     *
     * @param id 部门编号
     * @return 子部门列表
     */
    default List<DepartmentEntity> getChildDeptList(Long id) {
        return getChildDeptList(Collections.singleton(id));
    }

    /**
     * 获得指定部门的所有子部门
     *
     * @param ids 部门编号数组
     * @return 子部门列表
     */
    List<DepartmentEntity> getChildDeptList(Collection<Long> ids);

    /**
     * 获得指定领导者的部门列表
     *
     * @param id 领导者编号
     * @return 部门列表
     */
    List<DepartmentEntity> getDeptListByLeaderUserId(Long id);

    /**
     * 获得所有子部门，从缓存中
     *
     * @param id 父部门编号
     * @return 子部门列表
     */
    Set<Long> getChildDeptIdListFromCache(Long id);

    /**
     * 校验部门们是否有效。如下情况，视为无效：
     * 1. 部门编号不存在
     * 2. 部门被禁用
     *
     * @param ids 角色编号数组
     */
    void validateDeptList(Collection<Long> ids);

}
