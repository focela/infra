package com.focela.platform.system.service.department;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.utils.object.ObjectUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.department.dto.dept.DepartmentListRequest;
import com.focela.platform.system.controller.admin.department.dto.dept.DepartmentSaveRequest;
import com.focela.platform.system.domain.entity.department.DepartmentEntity;
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
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
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
        DepartmentEntity deptEntity = deptMapper.selectById(deptId);
        assertPojoEquals(request, deptEntity, "id");
    }

    @Test
    public void testUpdateDept() {
        // mock data
        DepartmentEntity dbDeptEntity = randomPojo(DepartmentEntity.class, o -> o.setStatus(randomCommonStatus()));
        deptMapper.insert(dbDeptEntity);// @Sql: first insert an existing record
        // prepare parameters
        DepartmentSaveRequest request = randomPojo(DepartmentSaveRequest.class, o -> {
            // set updated ID
            o.setParentId(DepartmentEntity.PARENT_ID_ROOT);
            o.setId(dbDeptEntity.getId());
            o.setStatus(randomCommonStatus());
        });

        // invoke
        deptService.updateDept(request);
        // verify update is correct
        DepartmentEntity deptEntity = deptMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, deptEntity);
    }

    @Test
    public void testDeleteDept_success() {
        // mock data
        DepartmentEntity dbDeptEntity = randomPojo(DepartmentEntity.class);
        deptMapper.insert(dbDeptEntity);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbDeptEntity.getId();

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
        DepartmentEntity childrenDeptEntity = randomPojo(DepartmentEntity.class, o -> {
            o.setParentId(parentDept.getId());
            o.setStatus(randomCommonStatus());
        });
        // insert child department
        deptMapper.insert(childrenDeptEntity);

        // invoke and assert exception
        assertServiceException(() -> deptService.deleteDept(parentDept.getId()), DEPT_EXITS_CHILDREN);
    }

    @Test
    public void testDeleteDeptList_success() {
        // mock data
        DepartmentEntity deptEntity1 = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptEntity1);
        DepartmentEntity deptEntity2 = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptEntity2);
        // prepare parameters
        List<Long> ids = Arrays.asList(deptEntity1.getId(), deptEntity2.getId());

        // invoke
        deptService.deleteDeptList(ids);
        // verify data no longer exists
        assertNull(deptMapper.selectById(deptEntity1.getId()));
        assertNull(deptMapper.selectById(deptEntity2.getId()));
    }

    @Test
    public void testDeleteDeptList_exitsChildren() {
        // mock data
        DepartmentEntity parentDept = randomPojo(DepartmentEntity.class);
        deptMapper.insert(parentDept);
        DepartmentEntity childrenDeptEntity = randomPojo(DepartmentEntity.class, o -> {
            o.setParentId(parentDept.getId());
            o.setStatus(randomCommonStatus());
        });
        deptMapper.insert(childrenDeptEntity);
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
        DepartmentEntity deptEntity = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptEntity);

        // prepare parameters
        Long id = randomLongId();
        Long parentId = deptEntity.getParentId();
        String name = deptEntity.getName();

        // invoke and assert exception
        assertServiceException(() -> deptService.validateDeptNameUnique(id, parentId, name),
                DEPT_NAME_DUPLICATE);
    }

    @Test
    public void testGetDept() {
        // mock data
        DepartmentEntity deptEntity = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptEntity);
        // prepare parameters
        Long id = deptEntity.getId();

        // invoke
        DepartmentEntity dbDept = deptService.getDept(id);
        // assert
        assertEquals(deptEntity, dbDept);
    }

    @Test
    public void testGetDeptList_ids() {
        // mock data
        DepartmentEntity deptEntity01 = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptEntity01);
        DepartmentEntity deptEntity02 = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptEntity02);
        // prepare parameters
        List<Long> ids = Arrays.asList(deptEntity01.getId(), deptEntity02.getId());

        // invoke
        List<DepartmentEntity> departmentEntities = deptService.getDeptList(ids);
        // assert
        assertEquals(2, departmentEntities.size());
        assertEquals(deptEntity01, departmentEntities.get(0));
        assertEquals(deptEntity02, departmentEntities.get(1));
    }

    @Test
    public void testGetDeptList_request() {
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
        List<DepartmentEntity> departmentEntities = deptService.getDeptList(request);
        // assert
        assertEquals(1, departmentEntities.size());
        assertPojoEquals(dept, departmentEntities.get(0));
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
        DepartmentEntity deptEntity = randomPojo(DepartmentEntity.class).setStatus(CommonStatusEnum.ENABLE.getStatus());
        deptMapper.insert(deptEntity);
        // prepare parameters
        List<Long> ids = singletonList(deptEntity.getId());

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
        DepartmentEntity deptEntity = randomPojo(DepartmentEntity.class).setStatus(CommonStatusEnum.DISABLE.getStatus());
        deptMapper.insert(deptEntity);
        // prepare parameters
        List<Long> ids = singletonList(deptEntity.getId());

        // invoke and assert exception
        assertServiceException(() -> deptService.validateDeptList(ids), DEPT_NOT_ENABLE, deptEntity.getName());
    }

}
