package com.focela.platform.system.api.department;

import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.api.department.dto.DepartmentRpcResponse;
import com.focela.platform.system.entity.department.DepartmentEntity;
import com.focela.platform.system.service.department.DepartmentService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * Department API implementation class
 */
@Service
public class LocalDepartmentApi implements DepartmentApi {

    @Resource
    private DepartmentService deptService;

    @Override
    public DepartmentRpcResponse getDept(Long id) {
        DepartmentEntity dept = deptService.getDept(id);
        return BeanUtils.toBean(dept, DepartmentRpcResponse.class);
    }

    @Override
    public List<DepartmentRpcResponse> getDeptList(Collection<Long> ids) {
        List<DepartmentEntity> depts = deptService.getDeptList(ids);
        return BeanUtils.toBean(depts, DepartmentRpcResponse.class);
    }

    @Override
    public void validateDeptList(Collection<Long> ids) {
        deptService.validateDeptList(ids);
    }

    @Override
    public List<DepartmentRpcResponse> getChildDeptList(Long id) {
        List<DepartmentEntity> childDeptList = deptService.getChildDeptList(id);
        return BeanUtils.toBean(childDeptList, DepartmentRpcResponse.class);
    }

}
