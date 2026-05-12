package com.focela.platform.module.system.api.department;

import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.system.api.department.dto.DepartmentRespDTO;
import com.focela.platform.module.system.repository.entity.department.DepartmentEntity;
import com.focela.platform.module.system.service.department.DepartmentService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 部门 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class LocalDepartmentApi implements DepartmentApi {

    @Resource
    private DepartmentService deptService;

    @Override
    public DepartmentRespDTO getDept(Long id) {
        DepartmentEntity dept = deptService.getDept(id);
        return BeanUtils.toBean(dept, DepartmentRespDTO.class);
    }

    @Override
    public List<DepartmentRespDTO> getDeptList(Collection<Long> ids) {
        List<DepartmentEntity> depts = deptService.getDeptList(ids);
        return BeanUtils.toBean(depts, DepartmentRespDTO.class);
    }

    @Override
    public void validateDeptList(Collection<Long> ids) {
        deptService.validateDeptList(ids);
    }

    @Override
    public List<DepartmentRespDTO> getChildDeptList(Long id) {
        List<DepartmentEntity> childDeptList = deptService.getChildDeptList(id);
        return BeanUtils.toBean(childDeptList, DepartmentRespDTO.class);
    }

}
