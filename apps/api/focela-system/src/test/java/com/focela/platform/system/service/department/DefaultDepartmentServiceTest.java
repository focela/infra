package com.focela.platform.system.service.department;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.utils.object.ObjectUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.department.request.department.DepartmentListRequest;
import com.focela.platform.system.controller.admin.department.request.department.DepartmentSaveRequest;
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
    private DefaultDepartmentService departmentService;
    @Resource
    private DepartmentMapper departmentMapper;

    @Test
    public void createDepartment_validRequest_createsDepartment() {
        // prepare parameters
        DepartmentSaveRequest request = randomPojo(DepartmentSaveRequest.class, o -> {
            o.setId(null); // prevent id from being set
            o.setParentId(DepartmentEntity.PARENT_ID_ROOT);
            o.setStatus(randomCommonStatus());
        });

        // invoke
        Long deptId = departmentService.createDepartment(request);
        // assert
        assertNotNull(deptId);
        // verify record properties are correct
        DepartmentEntity deptEntity = departmentMapper.selectById(deptId);
        assertPojoEquals(request, deptEntity, "id");
    }

    @Test
    public void updateDepartment_validRequest_updatesDepartment() {
        // mock data
        DepartmentEntity dbDeptEntity = randomPojo(DepartmentEntity.class, o -> o.setStatus(randomCommonStatus()));
        departmentMapper.insert(dbDeptEntity);// @Sql: first insert an existing record
        // prepare parameters
        DepartmentSaveRequest request = randomPojo(DepartmentSaveRequest.class, o -> {
            // set updated ID
            o.setParentId(DepartmentEntity.PARENT_ID_ROOT);
            o.setId(dbDeptEntity.getId());
            o.setStatus(randomCommonStatus());
        });

        // invoke
        departmentService.updateDepartment(request);
        // verify update is correct
        DepartmentEntity deptEntity = departmentMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, deptEntity);
    }

    @Test
    public void deleteDepartment_existingDepartment_deletesDepartment() {
        // mock data
        DepartmentEntity dbDeptEntity = randomPojo(DepartmentEntity.class);
        departmentMapper.insert(dbDeptEntity);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbDeptEntity.getId();

        // invoke
        departmentService.deleteDepartment(id);
        // verify data no longer exists
        assertNull(departmentMapper.selectById(id));
    }

    @Test
    public void deleteDepartment_withChildren_throwsServiceException() {
        // mock data
        DepartmentEntity parentDept = randomPojo(DepartmentEntity.class);
        departmentMapper.insert(parentDept);// @Sql: first insert an existing record
        // prepare parameters
        DepartmentEntity childrenDeptEntity = randomPojo(DepartmentEntity.class, o -> {
            o.setParentId(parentDept.getId());
            o.setStatus(randomCommonStatus());
        });
        // insert child department
        departmentMapper.insert(childrenDeptEntity);

        // invoke and assert exception
        assertServiceException(() -> departmentService.deleteDepartment(parentDept.getId()), DEPARTMENT_HAS_CHILDREN);
    }

    @Test
    public void deleteDepartmentList_existingDepartments_deletesDepartments() {
        // mock data
        DepartmentEntity deptEntity1 = randomPojo(DepartmentEntity.class);
        departmentMapper.insert(deptEntity1);
        DepartmentEntity deptEntity2 = randomPojo(DepartmentEntity.class);
        departmentMapper.insert(deptEntity2);
        // prepare parameters
        List<Long> ids = Arrays.asList(deptEntity1.getId(), deptEntity2.getId());

        // invoke
        departmentService.deleteDepartmentList(ids);
        // verify data no longer exists
        assertNull(departmentMapper.selectById(deptEntity1.getId()));
        assertNull(departmentMapper.selectById(deptEntity2.getId()));
    }

    @Test
    public void deleteDepartmentList_withChildDepartment_throwsServiceException() {
        // mock data
        DepartmentEntity parentDept = randomPojo(DepartmentEntity.class);
        departmentMapper.insert(parentDept);
        DepartmentEntity childrenDeptEntity = randomPojo(DepartmentEntity.class, o -> {
            o.setParentId(parentDept.getId());
            o.setStatus(randomCommonStatus());
        });
        departmentMapper.insert(childrenDeptEntity);
        DepartmentEntity anotherDept = randomPojo(DepartmentEntity.class);
        departmentMapper.insert(anotherDept);

        // prepare parameters（parentDept that contains child departments）
        List<Long> ids = Arrays.asList(parentDept.getId(), anotherDept.getId());

        // invoke and assert exception
        assertServiceException(() -> departmentService.deleteDepartmentList(ids), DEPARTMENT_HAS_CHILDREN);
    }

    @Test
    public void validateParentDepartment_selfParent_throwsServiceException() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> departmentService.validateParentDepartment(id, id),
                DEPARTMENT_PARENT_SELF_REFERENCE);
    }

    @Test
    public void validateParentDepartment_childAsParent_throwsServiceException() {
        // mock data（parent node）
        DepartmentEntity parentDept = randomPojo(DepartmentEntity.class);
        departmentMapper.insert(parentDept);
        // mock data（child node）
        DepartmentEntity childDept = randomPojo(DepartmentEntity.class, o -> {
            o.setParentId(parentDept.getId());
        });
        departmentMapper.insert(childDept);

        // prepare parameters
        Long id = parentDept.getId();
        Long parentId = childDept.getId();

        // invoke and assert exception
        assertServiceException(() -> departmentService.validateParentDepartment(id, parentId), DEPARTMENT_PARENT_IS_CHILD);
    }

    @Test
    public void validateDepartmentNameUnique_duplicateName_throwsServiceException() {
        // mock data
        DepartmentEntity deptEntity = randomPojo(DepartmentEntity.class);
        departmentMapper.insert(deptEntity);

        // prepare parameters
        Long id = randomLongId();
        Long parentId = deptEntity.getParentId();
        String name = deptEntity.getName();

        // invoke and assert exception
        assertServiceException(() -> departmentService.validateDepartmentNameUnique(id, parentId, name),
                DEPARTMENT_NAME_DUPLICATE);
    }

    @Test
    public void getDepartment_existingId_returnsDepartment() {
        // mock data
        DepartmentEntity deptEntity = randomPojo(DepartmentEntity.class);
        departmentMapper.insert(deptEntity);
        // prepare parameters
        Long id = deptEntity.getId();

        // invoke
        DepartmentEntity dbDept = departmentService.getDepartment(id);
        // assert
        assertEquals(deptEntity, dbDept);
    }

    @Test
    public void getDepartmentList_byIds_returnsDepartments() {
        // mock data
        DepartmentEntity deptEntity01 = randomPojo(DepartmentEntity.class);
        departmentMapper.insert(deptEntity01);
        DepartmentEntity deptEntity02 = randomPojo(DepartmentEntity.class);
        departmentMapper.insert(deptEntity02);
        // prepare parameters
        List<Long> ids = Arrays.asList(deptEntity01.getId(), deptEntity02.getId());

        // invoke
        List<DepartmentEntity> departmentEntities = departmentService.getDepartmentList(ids);
        // assert
        assertEquals(2, departmentEntities.size());
        assertEquals(deptEntity01, departmentEntities.get(0));
        assertEquals(deptEntity02, departmentEntities.get(1));
    }

    @Test
    public void getDepartmentList_matchingRequest_returnsDepartments() {
        // mock data
        DepartmentEntity department = randomPojo(DepartmentEntity.class, o -> { // will be queried later
            o.setName("Development Department");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        departmentMapper.insert(department);
        // test name mismatch
        departmentMapper.insert(ObjectUtils.cloneIgnoreId(department, o -> o.setName("Sales")));
        // test status mismatch
        departmentMapper.insert(ObjectUtils.cloneIgnoreId(department, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // prepare parameters
        DepartmentListRequest request = new DepartmentListRequest();
        request.setName("Development");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // invoke
        List<DepartmentEntity> departmentEntities = departmentService.getDepartmentList(request);
        // assert
        assertEquals(1, departmentEntities.size());
        assertPojoEquals(department, departmentEntities.get(0));
    }

    @Test
    public void getChildDepartmentList_matchingParent_returnsChildren() {
        // mock data（1 level child node）
        DepartmentEntity dept1 = randomPojo(DepartmentEntity.class, o -> o.setName("1"));
        departmentMapper.insert(dept1);
        DepartmentEntity dept2 = randomPojo(DepartmentEntity.class, o -> o.setName("2"));
        departmentMapper.insert(dept2);
        // mock data（2 level child node）
        DepartmentEntity dept1a = randomPojo(DepartmentEntity.class, o -> o.setName("1-a").setParentId(dept1.getId()));
        departmentMapper.insert(dept1a);
        DepartmentEntity dept2a = randomPojo(DepartmentEntity.class, o -> o.setName("2-a").setParentId(dept2.getId()));
        departmentMapper.insert(dept2a);
        // prepare parameters
        Long id = dept1.getParentId();

        // invoke
        List<DepartmentEntity> result = departmentService.getChildDepartmentList(id);
        // assert
        assertEquals(result.size(), 2);
        assertPojoEquals(dept1, result.get(0));
        assertPojoEquals(dept1a, result.get(1));
    }

    @Test
    public void getChildDepartmentIdListFromCache_matchingParent_returnsChildIds() {
        // mock data（1 level child node）
        DepartmentEntity dept1 = randomPojo(DepartmentEntity.class, o -> o.setName("1"));
        departmentMapper.insert(dept1);
        DepartmentEntity dept2 = randomPojo(DepartmentEntity.class, o -> o.setName("2"));
        departmentMapper.insert(dept2);
        // mock data（2 level child node）
        DepartmentEntity dept1a = randomPojo(DepartmentEntity.class, o -> o.setName("1-a").setParentId(dept1.getId()));
        departmentMapper.insert(dept1a);
        DepartmentEntity dept2a = randomPojo(DepartmentEntity.class, o -> o.setName("2-a").setParentId(dept2.getId()));
        departmentMapper.insert(dept2a);
        // prepare parameters
        Long id = dept1.getParentId();

        // invoke
        Set<Long> result = departmentService.getChildDepartmentIdListFromCache(id);
        // assert
        assertEquals(result.size(), 2);
        assertTrue(result.contains(dept1.getId()));
        assertTrue(result.contains(dept1a.getId()));
    }

    @Test
    public void validateDepartmentList_enabledDepartments_passes() {
        // mock data
        DepartmentEntity deptEntity = randomPojo(DepartmentEntity.class).setStatus(CommonStatusEnum.ENABLE.getStatus());
        departmentMapper.insert(deptEntity);
        // prepare parameters
        List<Long> ids = singletonList(deptEntity.getId());

        // invoke, no assertion needed
        departmentService.validateDepartmentList(ids);
    }

    @Test
    public void validateDepartmentList_missingDepartment_throwsServiceException() {
        // prepare parameters
        List<Long> ids = singletonList(randomLongId());

        // invoke and assert exception
        assertServiceException(() -> departmentService.validateDepartmentList(ids), DEPARTMENT_NOT_FOUND);
    }

    @Test
    public void validateDepartmentList_disabledDepartment_throwsServiceException() {
        // mock data
        DepartmentEntity deptEntity = randomPojo(DepartmentEntity.class).setStatus(CommonStatusEnum.DISABLE.getStatus());
        departmentMapper.insert(deptEntity);
        // prepare parameters
        List<Long> ids = singletonList(deptEntity.getId());

        // invoke and assert exception
        assertServiceException(() -> departmentService.validateDepartmentList(ids), DEPARTMENT_NOT_ENABLED, deptEntity.getName());
    }

}
