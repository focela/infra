package com.focela.platform.system.service.department;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.utils.object.ObjectUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.department.dto.dept.DepartmentListRequest;
import com.focela.platform.system.controller.admin.department.dto.dept.DepartmentSaveRequest;
import com.focela.platform.system.entity.department.DepartmentEntity;
import com.focela.platform.system.repository.mapper.department.DepartmentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.ErrorCodeConstants.*;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link DefaultDepartmentService}  unit test class
 */
@Import(DefaultDepartmentService.class)
public class DefaultDepartmentServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultDepartmentService deptService;
    @Resource
    private DepartmentMapper deptMapper;

    @Test
    public void testCreateDept() {
        // prepare parameters
        DepartmentSaveRequest request = randomPojo(DepartmentSaveRequest.class, o -> {
            o.setId(null); // prevent id from being set
            o.setParentId(DepartmentEntity.PARENT_ID_ROOT);
            o.setStatus(randomCommonStatus());
        });

        // invoke
        Long deptId = deptService.createDept(request);
        // assert
        assertNotNull(deptId);
        // verify record properties are correct
        DepartmentEntity deptDO = deptMapper.selectById(deptId);
        assertPojoEquals(request, deptDO, "id");
    }

    @Test
    public void testUpdateDept() {
        // mock data
        DepartmentEntity dbDeptDO = randomPojo(DepartmentEntity.class, o -> o.setStatus(randomCommonStatus()));
        deptMapper.insert(dbDeptDO);// @Sql: first insert an existing record
        // prepare parameters
        DepartmentSaveRequest request = randomPojo(DepartmentSaveRequest.class, o -> {
            // set updated ID
            o.setParentId(DepartmentEntity.PARENT_ID_ROOT);
            o.setId(dbDeptDO.getId());
            o.setStatus(randomCommonStatus());
        });

        // invoke
        deptService.updateDept(request);
        // verify update is correct
        DepartmentEntity deptDO = deptMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, deptDO);
    }

    @Test
    public void testDeleteDept_success() {
        // mock data
        DepartmentEntity dbDeptDO = randomPojo(DepartmentEntity.class);
        deptMapper.insert(dbDeptDO);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbDeptDO.getId();

        // invoke
        deptService.deleteDept(id);
        // verify data no longer exists
        assertNull(deptMapper.selectById(id));
    }

    @Test
    public void testDeleteDept_exitsChildren() {
        // mock data
        DepartmentEntity parentDept = randomPojo(DepartmentEntity.class);
        deptMapper.insert(parentDept);// @Sql: first insert an existing record
        // prepare parameters
        DepartmentEntity childrenDeptDO = randomPojo(DepartmentEntity.class, o -> {
            o.setParentId(parentDept.getId());
            o.setStatus(randomCommonStatus());
        });
        // insert child department
        deptMapper.insert(childrenDeptDO);

        // invoke and assert exception
        assertServiceException(() -> deptService.deleteDept(parentDept.getId()), DEPT_EXITS_CHILDREN);
    }

    @Test
    public void testDeleteDeptList_success() {
        // mock data
        DepartmentEntity deptDO1 = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptDO1);
        DepartmentEntity deptDO2 = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptDO2);
        // prepare parameters
        List<Long> ids = Arrays.asList(deptDO1.getId(), deptDO2.getId());

        // invoke
        deptService.deleteDeptList(ids);
        // verify data no longer exists
        assertNull(deptMapper.selectById(deptDO1.getId()));
        assertNull(deptMapper.selectById(deptDO2.getId()));
    }

    @Test
    public void testDeleteDeptList_exitsChildren() {
        // mock data
        DepartmentEntity parentDept = randomPojo(DepartmentEntity.class);
        deptMapper.insert(parentDept);
        DepartmentEntity childrenDeptDO = randomPojo(DepartmentEntity.class, o -> {
            o.setParentId(parentDept.getId());
            o.setStatus(randomCommonStatus());
        });
        deptMapper.insert(childrenDeptDO);
        DepartmentEntity anotherDept = randomPojo(DepartmentEntity.class);
        deptMapper.insert(anotherDept);

        // prepare parameters（parentDept that contains child departments）
        List<Long> ids = Arrays.asList(parentDept.getId(), anotherDept.getId());

        // invoke and assert exception
        assertServiceException(() -> deptService.deleteDeptList(ids), DEPT_EXITS_CHILDREN);
    }

    @Test
    public void testValidateParentDept_parentError() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> deptService.validateParentDept(id, id),
                DEPT_PARENT_ERROR);
    }

    @Test
    public void testValidateParentDept_parentIsChild() {
        // mock data（parent node）
        DepartmentEntity parentDept = randomPojo(DepartmentEntity.class);
        deptMapper.insert(parentDept);
        // mock data（child node）
        DepartmentEntity childDept = randomPojo(DepartmentEntity.class, o -> {
            o.setParentId(parentDept.getId());
        });
        deptMapper.insert(childDept);

        // prepare parameters
        Long id = parentDept.getId();
        Long parentId = childDept.getId();

        // invoke and assert exception
        assertServiceException(() -> deptService.validateParentDept(id, parentId), DEPT_PARENT_IS_CHILD);
    }

    @Test
    public void testValidateNameUnique_duplicate() {
        // mock data
        DepartmentEntity deptDO = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptDO);

        // prepare parameters
        Long id = randomLongId();
        Long parentId = deptDO.getParentId();
        String name = deptDO.getName();

        // invoke and assert exception
        assertServiceException(() -> deptService.validateDeptNameUnique(id, parentId, name),
                DEPT_NAME_DUPLICATE);
    }

    @Test
    public void testGetDept() {
        // mock data
        DepartmentEntity deptDO = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptDO);
        // prepare parameters
        Long id = deptDO.getId();

        // invoke
        DepartmentEntity dbDept = deptService.getDept(id);
        // assert
        assertEquals(deptDO, dbDept);
    }

    @Test
    public void testGetDeptList_ids() {
        // mock data
        DepartmentEntity deptDO01 = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptDO01);
        DepartmentEntity deptDO02 = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptDO02);
        // prepare parameters
        List<Long> ids = Arrays.asList(deptDO01.getId(), deptDO02.getId());

        // invoke
        List<DepartmentEntity> deptDOList = deptService.getDeptList(ids);
        // assert
        assertEquals(2, deptDOList.size());
        assertEquals(deptDO01, deptDOList.get(0));
        assertEquals(deptDO02, deptDOList.get(1));
    }

    @Test
    public void testGetDeptList_reqVO() {
        // mock data
        DepartmentEntity dept = randomPojo(DepartmentEntity.class, o -> { // will be queried later
            o.setName("Development Department");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        deptMapper.insert(dept);
        // test name mismatch
        deptMapper.insert(ObjectUtils.cloneIgnoreId(dept, o -> o.setName("Sales")));
        // test status mismatch
        deptMapper.insert(ObjectUtils.cloneIgnoreId(dept, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // prepare parameters
        DepartmentListRequest request = new DepartmentListRequest();
        request.setName("Development");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // invoke
        List<DepartmentEntity> sysDeptDOS = deptService.getDeptList(request);
        // assert
        assertEquals(1, sysDeptDOS.size());
        assertPojoEquals(dept, sysDeptDOS.get(0));
    }

    @Test
    public void testGetChildDeptList() {
        // mock data（1 level child node）
        DepartmentEntity dept1 = randomPojo(DepartmentEntity.class, o -> o.setName("1"));
        deptMapper.insert(dept1);
        DepartmentEntity dept2 = randomPojo(DepartmentEntity.class, o -> o.setName("2"));
        deptMapper.insert(dept2);
        // mock data（2 level child node）
        DepartmentEntity dept1a = randomPojo(DepartmentEntity.class, o -> o.setName("1-a").setParentId(dept1.getId()));
        deptMapper.insert(dept1a);
        DepartmentEntity dept2a = randomPojo(DepartmentEntity.class, o -> o.setName("2-a").setParentId(dept2.getId()));
        deptMapper.insert(dept2a);
        // prepare parameters
        Long id = dept1.getParentId();

        // invoke
        List<DepartmentEntity> result = deptService.getChildDeptList(id);
        // assert
        assertEquals(result.size(), 2);
        assertPojoEquals(dept1, result.get(0));
        assertPojoEquals(dept1a, result.get(1));
    }

    @Test
    public void testGetChildDeptListFromCache() {
        // mock data（1 level child node）
        DepartmentEntity dept1 = randomPojo(DepartmentEntity.class, o -> o.setName("1"));
        deptMapper.insert(dept1);
        DepartmentEntity dept2 = randomPojo(DepartmentEntity.class, o -> o.setName("2"));
        deptMapper.insert(dept2);
        // mock data（2 level child node）
        DepartmentEntity dept1a = randomPojo(DepartmentEntity.class, o -> o.setName("1-a").setParentId(dept1.getId()));
        deptMapper.insert(dept1a);
        DepartmentEntity dept2a = randomPojo(DepartmentEntity.class, o -> o.setName("2-a").setParentId(dept2.getId()));
        deptMapper.insert(dept2a);
        // prepare parameters
        Long id = dept1.getParentId();

        // invoke
        Set<Long> result = deptService.getChildDeptIdListFromCache(id);
        // assert
        assertEquals(result.size(), 2);
        assertTrue(result.contains(dept1.getId()));
        assertTrue(result.contains(dept1a.getId()));
    }

    @Test
    public void testValidateDeptList_success() {
        // mock data
        DepartmentEntity deptDO = randomPojo(DepartmentEntity.class).setStatus(CommonStatusEnum.ENABLE.getStatus());
        deptMapper.insert(deptDO);
        // prepare parameters
        List<Long> ids = singletonList(deptDO.getId());

        // invoke, no assertion needed
        deptService.validateDeptList(ids);
    }

    @Test
    public void testValidateDeptList_notFound() {
        // prepare parameters
        List<Long> ids = singletonList(randomLongId());

        // invoke and assert exception
        assertServiceException(() -> deptService.validateDeptList(ids), DEPT_NOT_FOUND);
    }

    @Test
    public void testValidateDeptList_notEnable() {
        // mock data
        DepartmentEntity deptDO = randomPojo(DepartmentEntity.class).setStatus(CommonStatusEnum.DISABLE.getStatus());
        deptMapper.insert(deptDO);
        // prepare parameters
        List<Long> ids = singletonList(deptDO.getId());

        // invoke and assert exception
        assertServiceException(() -> deptService.validateDeptList(ids), DEPT_NOT_ENABLE, deptDO.getName());
    }

}
