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
 * {@link DefaultDepartmentService} 的单元测试类
 */
@Import(DefaultDepartmentService.class)
public class DefaultDepartmentServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultDepartmentService deptService;
    @Resource
    private DepartmentMapper deptMapper;

    @Test
    public void testCreateDept() {
        // 准备参数
        DepartmentSaveRequest request = randomPojo(DepartmentSaveRequest.class, o -> {
            o.setId(null); // 防止 id 被设置
            o.setParentId(DepartmentEntity.PARENT_ID_ROOT);
            o.setStatus(randomCommonStatus());
        });

        // 调用
        Long deptId = deptService.createDept(request);
        // 断言
        assertNotNull(deptId);
        // 校验记录的属性是否正确
        DepartmentEntity deptDO = deptMapper.selectById(deptId);
        assertPojoEquals(request, deptDO, "id");
    }

    @Test
    public void testUpdateDept() {
        // mock 数据
        DepartmentEntity dbDeptDO = randomPojo(DepartmentEntity.class, o -> o.setStatus(randomCommonStatus()));
        deptMapper.insert(dbDeptDO);// @Sql: 先插入出一条存在的数据
        // 准备参数
        DepartmentSaveRequest request = randomPojo(DepartmentSaveRequest.class, o -> {
            // 设置更新的 ID
            o.setParentId(DepartmentEntity.PARENT_ID_ROOT);
            o.setId(dbDeptDO.getId());
            o.setStatus(randomCommonStatus());
        });

        // 调用
        deptService.updateDept(request);
        // 校验是否更新正确
        DepartmentEntity deptDO = deptMapper.selectById(request.getId()); // 获取最新的
        assertPojoEquals(request, deptDO);
    }

    @Test
    public void testDeleteDept_success() {
        // mock 数据
        DepartmentEntity dbDeptDO = randomPojo(DepartmentEntity.class);
        deptMapper.insert(dbDeptDO);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbDeptDO.getId();

        // 调用
        deptService.deleteDept(id);
        // 校验数据不存在了
        assertNull(deptMapper.selectById(id));
    }

    @Test
    public void testDeleteDept_exitsChildren() {
        // mock 数据
        DepartmentEntity parentDept = randomPojo(DepartmentEntity.class);
        deptMapper.insert(parentDept);// @Sql: 先插入出一条存在的数据
        // 准备参数
        DepartmentEntity childrenDeptDO = randomPojo(DepartmentEntity.class, o -> {
            o.setParentId(parentDept.getId());
            o.setStatus(randomCommonStatus());
        });
        // 插入子部门
        deptMapper.insert(childrenDeptDO);

        // 调用, 并断言异常
        assertServiceException(() -> deptService.deleteDept(parentDept.getId()), DEPT_EXITS_CHILDREN);
    }

    @Test
    public void testDeleteDeptList_success() {
        // mock 数据
        DepartmentEntity deptDO1 = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptDO1);
        DepartmentEntity deptDO2 = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptDO2);
        // 准备参数
        List<Long> ids = Arrays.asList(deptDO1.getId(), deptDO2.getId());

        // 调用
        deptService.deleteDeptList(ids);
        // 校验数据不存在了
        assertNull(deptMapper.selectById(deptDO1.getId()));
        assertNull(deptMapper.selectById(deptDO2.getId()));
    }

    @Test
    public void testDeleteDeptList_exitsChildren() {
        // mock 数据
        DepartmentEntity parentDept = randomPojo(DepartmentEntity.class);
        deptMapper.insert(parentDept);
        DepartmentEntity childrenDeptDO = randomPojo(DepartmentEntity.class, o -> {
            o.setParentId(parentDept.getId());
            o.setStatus(randomCommonStatus());
        });
        deptMapper.insert(childrenDeptDO);
        DepartmentEntity anotherDept = randomPojo(DepartmentEntity.class);
        deptMapper.insert(anotherDept);

        // 准备参数（包含有子部门的 parentDept）
        List<Long> ids = Arrays.asList(parentDept.getId(), anotherDept.getId());

        // 调用, 并断言异常
        assertServiceException(() -> deptService.deleteDeptList(ids), DEPT_EXITS_CHILDREN);
    }

    @Test
    public void testValidateParentDept_parentError() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> deptService.validateParentDept(id, id),
                DEPT_PARENT_ERROR);
    }

    @Test
    public void testValidateParentDept_parentIsChild() {
        // mock 数据（父节点）
        DepartmentEntity parentDept = randomPojo(DepartmentEntity.class);
        deptMapper.insert(parentDept);
        // mock 数据（子节点）
        DepartmentEntity childDept = randomPojo(DepartmentEntity.class, o -> {
            o.setParentId(parentDept.getId());
        });
        deptMapper.insert(childDept);

        // 准备参数
        Long id = parentDept.getId();
        Long parentId = childDept.getId();

        // 调用, 并断言异常
        assertServiceException(() -> deptService.validateParentDept(id, parentId), DEPT_PARENT_IS_CHILD);
    }

    @Test
    public void testValidateNameUnique_duplicate() {
        // mock 数据
        DepartmentEntity deptDO = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptDO);

        // 准备参数
        Long id = randomLongId();
        Long parentId = deptDO.getParentId();
        String name = deptDO.getName();

        // 调用, 并断言异常
        assertServiceException(() -> deptService.validateDeptNameUnique(id, parentId, name),
                DEPT_NAME_DUPLICATE);
    }

    @Test
    public void testGetDept() {
        // mock 数据
        DepartmentEntity deptDO = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptDO);
        // 准备参数
        Long id = deptDO.getId();

        // 调用
        DepartmentEntity dbDept = deptService.getDept(id);
        // 断言
        assertEquals(deptDO, dbDept);
    }

    @Test
    public void testGetDeptList_ids() {
        // mock 数据
        DepartmentEntity deptDO01 = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptDO01);
        DepartmentEntity deptDO02 = randomPojo(DepartmentEntity.class);
        deptMapper.insert(deptDO02);
        // 准备参数
        List<Long> ids = Arrays.asList(deptDO01.getId(), deptDO02.getId());

        // 调用
        List<DepartmentEntity> deptDOList = deptService.getDeptList(ids);
        // 断言
        assertEquals(2, deptDOList.size());
        assertEquals(deptDO01, deptDOList.get(0));
        assertEquals(deptDO02, deptDOList.get(1));
    }

    @Test
    public void testGetDeptList_reqVO() {
        // mock 数据
        DepartmentEntity dept = randomPojo(DepartmentEntity.class, o -> { // 等会查询到
            o.setName("开发部");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        deptMapper.insert(dept);
        // 测试 name 不匹配
        deptMapper.insert(ObjectUtils.cloneIgnoreId(dept, o -> o.setName("发")));
        // 测试 status 不匹配
        deptMapper.insert(ObjectUtils.cloneIgnoreId(dept, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // 准备参数
        DepartmentListRequest request = new DepartmentListRequest();
        request.setName("开");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // 调用
        List<DepartmentEntity> sysDeptDOS = deptService.getDeptList(request);
        // 断言
        assertEquals(1, sysDeptDOS.size());
        assertPojoEquals(dept, sysDeptDOS.get(0));
    }

    @Test
    public void testGetChildDeptList() {
        // mock 数据（1 级别子节点）
        DepartmentEntity dept1 = randomPojo(DepartmentEntity.class, o -> o.setName("1"));
        deptMapper.insert(dept1);
        DepartmentEntity dept2 = randomPojo(DepartmentEntity.class, o -> o.setName("2"));
        deptMapper.insert(dept2);
        // mock 数据（2 级子节点）
        DepartmentEntity dept1a = randomPojo(DepartmentEntity.class, o -> o.setName("1-a").setParentId(dept1.getId()));
        deptMapper.insert(dept1a);
        DepartmentEntity dept2a = randomPojo(DepartmentEntity.class, o -> o.setName("2-a").setParentId(dept2.getId()));
        deptMapper.insert(dept2a);
        // 准备参数
        Long id = dept1.getParentId();

        // 调用
        List<DepartmentEntity> result = deptService.getChildDeptList(id);
        // 断言
        assertEquals(result.size(), 2);
        assertPojoEquals(dept1, result.get(0));
        assertPojoEquals(dept1a, result.get(1));
    }

    @Test
    public void testGetChildDeptListFromCache() {
        // mock 数据（1 级别子节点）
        DepartmentEntity dept1 = randomPojo(DepartmentEntity.class, o -> o.setName("1"));
        deptMapper.insert(dept1);
        DepartmentEntity dept2 = randomPojo(DepartmentEntity.class, o -> o.setName("2"));
        deptMapper.insert(dept2);
        // mock 数据（2 级子节点）
        DepartmentEntity dept1a = randomPojo(DepartmentEntity.class, o -> o.setName("1-a").setParentId(dept1.getId()));
        deptMapper.insert(dept1a);
        DepartmentEntity dept2a = randomPojo(DepartmentEntity.class, o -> o.setName("2-a").setParentId(dept2.getId()));
        deptMapper.insert(dept2a);
        // 准备参数
        Long id = dept1.getParentId();

        // 调用
        Set<Long> result = deptService.getChildDeptIdListFromCache(id);
        // 断言
        assertEquals(result.size(), 2);
        assertTrue(result.contains(dept1.getId()));
        assertTrue(result.contains(dept1a.getId()));
    }

    @Test
    public void testValidateDeptList_success() {
        // mock 数据
        DepartmentEntity deptDO = randomPojo(DepartmentEntity.class).setStatus(CommonStatusEnum.ENABLE.getStatus());
        deptMapper.insert(deptDO);
        // 准备参数
        List<Long> ids = singletonList(deptDO.getId());

        // 调用，无需断言
        deptService.validateDeptList(ids);
    }

    @Test
    public void testValidateDeptList_notFound() {
        // 准备参数
        List<Long> ids = singletonList(randomLongId());

        // 调用, 并断言异常
        assertServiceException(() -> deptService.validateDeptList(ids), DEPT_NOT_FOUND);
    }

    @Test
    public void testValidateDeptList_notEnable() {
        // mock 数据
        DepartmentEntity deptDO = randomPojo(DepartmentEntity.class).setStatus(CommonStatusEnum.DISABLE.getStatus());
        deptMapper.insert(deptDO);
        // 准备参数
        List<Long> ids = singletonList(deptDO.getId());

        // 调用, 并断言异常
        assertServiceException(() -> deptService.validateDeptList(ids), DEPT_NOT_ENABLE, deptDO.getName());
    }

}
