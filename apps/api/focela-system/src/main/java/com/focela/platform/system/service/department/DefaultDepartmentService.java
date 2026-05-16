package com.focela.platform.system.service.department;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.datapermission.core.annotation.DataPermission;
import com.focela.platform.system.controller.admin.department.dto.dept.DepartmentListRequest;
import com.focela.platform.system.controller.admin.department.dto.dept.DepartmentSaveRequest;
import com.focela.platform.system.entity.department.DepartmentEntity;
import com.focela.platform.system.repository.mapper.department.DepartmentMapper;
import com.focela.platform.system.repository.redis.RedisKeyConstants;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.*;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.common.utils.collection.CollectionUtils.convertSet;
import static com.focela.platform.system.constants.ErrorCodeConstants.*;

/**
 * Department Service implementation class
 */
@Service
@Validated
@Slf4j
public class DefaultDepartmentService implements DepartmentService {

    @Resource
    private DepartmentMapper deptMapper;

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries clears all caches because operating on a department affects multiple caches
    public Long createDept(DepartmentSaveRequest createRequest) {
        if (createRequest.getParentId() == null) {
            createRequest.setParentId(DepartmentEntity.PARENT_ID_ROOT);
        }
        // Validate the parent department
        validateParentDept(null, createRequest.getParentId());
        // Validate the uniqueness of the department name
        validateDeptNameUnique(null, createRequest.getParentId(), createRequest.getName());

        // Insert department
        DepartmentEntity dept = BeanUtils.toBean(createRequest, DepartmentEntity.class);
        deptMapper.insert(dept);
        return dept.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries clears all caches because operating on a department affects multiple caches
    public void updateDept(DepartmentSaveRequest updateRequest) {
        if (updateRequest.getParentId() == null) {
            updateRequest.setParentId(DepartmentEntity.PARENT_ID_ROOT);
        }
        // Validate that this entity exists
        validateDeptExists(updateRequest.getId());
        // Validate the parent department
        validateParentDept(updateRequest.getId(), updateRequest.getParentId());
        // Validate the uniqueness of the department name
        validateDeptNameUnique(updateRequest.getId(), updateRequest.getParentId(), updateRequest.getName());

        // Update department
        DepartmentEntity updateObj = BeanUtils.toBean(updateRequest, DepartmentEntity.class);
        deptMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries clears all caches because operating on a department affects multiple caches
    public void deleteDept(Long id) {
        // Validate existence
        validateDeptExists(id);
        // Validate whether it has child departments
        if (deptMapper.selectCountByParentId(id) > 0) {
            throw exception(DEPT_EXITS_CHILDREN);
        }
        // Delete department
        deptMapper.deleteById(id);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries clears all caches because operating on a department affects multiple caches
    public void deleteDeptList(List<Long> ids) {
        // Validate whether any has child departments
        for (Long id : ids) {
            if (deptMapper.selectCountByParentId(id) > 0) {
                throw exception(DEPT_EXITS_CHILDREN);
            }
        }

        // Batch delete departments
        deptMapper.deleteByIds(ids);
    }

    @VisibleForTesting
    void validateDeptExists(Long id) {
        if (id == null) {
            return;
        }
        DepartmentEntity dept = deptMapper.selectById(id);
        if (dept == null) {
            throw exception(DEPT_NOT_FOUND);
        }
    }

    @VisibleForTesting
    void validateParentDept(Long id, Long parentId) {
        if (parentId == null || DepartmentEntity.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        // 1. Cannot set self as parent department
        if (Objects.equals(id, parentId)) {
            throw exception(DEPT_PARENT_ERROR);
        }
        // 2. Parent department does not exist
        DepartmentEntity parentDept = deptMapper.selectById(parentId);
        if (parentDept == null) {
            throw exception(DEPT_PARENT_NOT_EXITS);
        }
        // 3. Recursively validate the parent department; if a parent is one of its own children, report an error to avoid cycles
        if (id == null) { // id is null means create, no need to consider cycles
            return;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 3.1 Validate cycle
            parentId = parentDept.getParentId();
            if (Objects.equals(id, parentId)) {
                throw exception(DEPT_PARENT_IS_CHILD);
            }
            // 3.2 Continue recursing to the next-level parent department
            if (parentId == null || DepartmentEntity.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentDept = deptMapper.selectById(parentId);
            if (parentDept == null) {
                break;
            }
        }
    }

    @VisibleForTesting
    void validateDeptNameUnique(Long id, Long parentId, String name) {
        DepartmentEntity dept = deptMapper.selectByParentIdAndName(parentId, name);
        if (dept == null) {
            return;
        }
        // If id is null, no need to compare whether it is a department with the same id
        if (id == null) {
            throw exception(DEPT_NAME_DUPLICATE);
        }
        if (ObjectUtil.notEqual(dept.getId(), id)) {
            throw exception(DEPT_NAME_DUPLICATE);
        }
    }

    @Override
    public DepartmentEntity getDept(Long id) {
        return deptMapper.selectById(id);
    }

    @Override
    public List<DepartmentEntity> getDeptList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return deptMapper.selectByIds(ids);
    }

    @Override
    public List<DepartmentEntity> getDeptList(DepartmentListRequest request) {
        List<DepartmentEntity> list = deptMapper.selectList(request);
        list.sort(Comparator.comparing(DepartmentEntity::getSort));
        return list;
    }

    @Override
    public List<DepartmentEntity> getChildDeptList(Collection<Long> ids) {
        List<DepartmentEntity> children = new LinkedList<>();
        // Traverse each level
        Collection<Long> parentIds = ids;
        for (int i = 0; i < Short.MAX_VALUE; i++) { // use Short.MAX_VALUE to avoid an infinite loop in bug scenarios
            // Query all child departments at the current level
            List<DepartmentEntity> depts = deptMapper.selectListByParentId(parentIds);
            // 1. If there are no child departments, end the traversal
            if (CollUtil.isEmpty(depts)) {
                break;
            }
            // 2. If there are child departments, continue traversing
            children.addAll(depts);
            parentIds = convertSet(depts, DepartmentEntity::getId);
        }
        return children;
    }

    @Override
    public List<DepartmentEntity> getDeptListByLeaderUserId(Long id) {
        return deptMapper.selectListByLeaderUserId(id);
    }

    @Override
    @DataPermission(enable = false) // disable data permission to avoid building an incorrect cache
    @Cacheable(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, key = "#id")
    public Set<Long> getChildDeptIdListFromCache(Long id) {
        List<DepartmentEntity> children = getChildDeptList(id);
        return convertSet(children, DepartmentEntity::getId);
    }

    @Override
    public void validateDeptList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // Get department information
        Map<Long, DepartmentEntity> deptMap = getDeptMap(ids);
        // Validate
        ids.forEach(id -> {
            DepartmentEntity dept = deptMap.get(id);
            if (dept == null) {
                throw exception(DEPT_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dept.getStatus())) {
                throw exception(DEPT_NOT_ENABLE, dept.getName());
            }
        });
    }

}
