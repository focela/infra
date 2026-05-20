package com.focela.platform.system.api.department;

import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.api.department.dto.DepartmentRpcResponse;
import com.focela.platform.system.domain.entity.department.DepartmentEntity;
import com.focela.platform.system.service.department.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Department API implementation class
 */
@Service
@RequiredArgsConstructor
public class LocalDepartmentApi implements DepartmentApi {

    private final DepartmentService departmentService;

    @Override
    public DepartmentRpcResponse getDept(Long id) {
        DepartmentEntity department = departmentService.getDepartment(id);
        return BeanUtils.toBean(department, DepartmentRpcResponse.class);
    }

    @Override
    public List<DepartmentRpcResponse> getDeptList(Collection<Long> ids) {
        List<DepartmentEntity> departments = departmentService.getDepartmentList(ids);
        return BeanUtils.toBean(departments, DepartmentRpcResponse.class);
    }

    @Override
    public void validateDeptList(Collection<Long> ids) {
        departmentService.validateDepartmentList(ids);
    }

    @Override
    public List<DepartmentRpcResponse> getChildDeptList(Long id) {
        List<DepartmentEntity> childDepartmentList = departmentService.getChildDepartmentList(id);
        return BeanUtils.toBean(childDepartmentList, DepartmentRpcResponse.class);
    }

}
