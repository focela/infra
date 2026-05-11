package com.focela.platform.module.system.api.dept;

import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.system.api.dept.dto.DeptRespDTO;
import com.focela.platform.module.system.repository.entity.dept.DeptEntity;
import com.focela.platform.module.system.service.dept.DeptService;
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
public class DeptApiImpl implements DeptApi {

    @Resource
    private DeptService deptService;

    @Override
    public DeptRespDTO getDept(Long id) {
        DeptEntity dept = deptService.getDept(id);
        return BeanUtils.toBean(dept, DeptRespDTO.class);
    }

    @Override
    public List<DeptRespDTO> getDeptList(Collection<Long> ids) {
        List<DeptEntity> depts = deptService.getDeptList(ids);
        return BeanUtils.toBean(depts, DeptRespDTO.class);
    }

    @Override
    public void validateDeptList(Collection<Long> ids) {
        deptService.validateDeptList(ids);
    }

    @Override
    public List<DeptRespDTO> getChildDeptList(Long id) {
        List<DeptEntity> childDeptList = deptService.getChildDeptList(id);
        return BeanUtils.toBean(childDeptList, DeptRespDTO.class);
    }

}
