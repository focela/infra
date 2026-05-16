package com.focela.platform.system.service.permission;

import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.permission.dto.role.RolePageRequest;
import com.focela.platform.system.controller.admin.permission.dto.role.RoleSaveRequest;
import com.focela.platform.system.entity.permission.RoleEntity;
import com.focela.platform.system.repository.mapper.permission.RoleMapper;
import com.focela.platform.system.enums.permission.DataScopeEnum;
import com.focela.platform.system.enums.permission.RoleTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.ErrorCodeConstants.*;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@Import(DefaultRoleService.class)
public class DefaultRoleServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultRoleService roleService;

    @Resource
    private RoleMapper roleMapper;

    @MockitoBean
    private PermissionService permissionService;

    @Test
    public void testCreateRole() {
        // prepare parameters
        RoleSaveRequest request = randomPojo(RoleSaveRequest.class)
                .setId(null)  // prevent id from being assigned
                .setStatus(randomCommonStatus());

        // invoke
        Long roleId = roleService.createRole(request, null);
        // assert
        RoleEntity roleDO = roleMapper.selectById(roleId);
        assertPojoEquals(request, roleDO, "id");
        assertEquals(RoleTypeEnum.CUSTOM.getType(), roleDO.getType());
        assertEquals(DataScopeEnum.ALL.getScope(), roleDO.getDataScope());
    }

    @Test
    public void testUpdateRole() {
        // mock data
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);
        // prepare parameters
        Long id = roleDO.getId();
        RoleSaveRequest request = randomPojo(RoleSaveRequest.class, o -> o.setId(id)
                .setStatus(randomCommonStatus()));

        // invoke
        roleService.updateRole(request);
        // assert
        RoleEntity newRoleDO = roleMapper.selectById(id);
        assertPojoEquals(request, newRoleDO);
    }

    @Test
    public void testUpdateRoleDataScope() {
        // mock data
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);
        // prepare parameters
        Long id = roleDO.getId();
        Integer dataScope = randomEle(DataScopeEnum.values()).getScope();
        Set<Long> dataScopeRoleIds = randomSet(Long.class);

        // invoke
        roleService.updateRoleDataScope(id, dataScope, dataScopeRoleIds);
        // assert
        RoleEntity dbRoleDO = roleMapper.selectById(id);
        assertEquals(dataScope, dbRoleDO.getDataScope());
        assertEquals(dataScopeRoleIds, dbRoleDO.getDataScopeDeptIds());
    }

    @Test
    public void testDeleteRole() {
        // mock data
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);
        // prepare parameters
        Long id = roleDO.getId();

        // invoke
        roleService.deleteRole(id);
        // assert
        assertNull(roleMapper.selectById(id));
        // verify delete related data
        verify(permissionService).processRoleDeleted(id);
    }

    @Test
    public void testValidateRoleDuplicate_success() {
        // invoke, will not throw
        roleService.validateRoleDuplicate(randomString(), randomString(), null);
    }

    @Test
    public void testValidateRoleDuplicate_nameDuplicate() {
        // mock data
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setName("role_name"));
        roleMapper.insert(roleDO);
        // prepare parameters
        String name = "role_name";

        // invoke, and assert exception
        assertServiceException(() -> roleService.validateRoleDuplicate(name, randomString(), null),
                ROLE_NAME_DUPLICATE, name);
    }

    @Test
    public void testValidateRoleDuplicate_codeDuplicate() {
        // mock data
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setCode("code"));
        roleMapper.insert(roleDO);
        // prepare parameters
        String code = "code";

        // invoke, and assert exception
        assertServiceException(() -> roleService.validateRoleDuplicate(randomString(), code, null),
                ROLE_CODE_DUPLICATE, code);
    }

    @Test
    public void testValidateUpdateRole_success() {
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);
        // prepare parameters
        Long id = roleDO.getId();

        // invoke, no exception
        roleService.validateRoleForUpdate(id);
    }

    @Test
    public void testValidateUpdateRole_roleIdNotExist() {
        assertServiceException(() -> roleService.validateRoleForUpdate(randomLongId()), ROLE_NOT_EXISTS);
    }

    @Test
    public void testValidateUpdateRole_systemRoleCanNotBeUpdate() {
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.SYSTEM.getType()));
        roleMapper.insert(roleDO);
        // prepare parameters
        Long id = roleDO.getId();

        assertServiceException(() -> roleService.validateRoleForUpdate(id),
                ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
    }

    @Test
    public void testGetRole() {
        // mock data
        RoleEntity roleDO = randomPojo(RoleEntity.class);
        roleMapper.insert(roleDO);
        // prepare parameters
        Long id = roleDO.getId();

        // invoke
        RoleEntity dbRoleDO = roleService.getRole(id);
        // assert
        assertPojoEquals(roleDO, dbRoleDO);
    }

    @Test
    public void testGetRoleFromCache() {
        // mock data（cache）
        RoleEntity roleDO = randomPojo(RoleEntity.class);
        roleMapper.insert(roleDO);
        // prepare parameters
        Long id = roleDO.getId();

        // invoke
        RoleEntity dbRoleDO = roleService.getRoleFromCache(id);
        // assert
        assertPojoEquals(roleDO, dbRoleDO);
    }

    @Test
    public void testGetRoleListByStatus() {
        // mock data
        RoleEntity dbRole01 = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(dbRole01);
        RoleEntity dbRole02 = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(dbRole02);

        // invoke
        List<RoleEntity> list = roleService.getRoleListByStatus(
                singleton(CommonStatusEnum.ENABLE.getStatus()));
        // assert
        assertEquals(1, list.size());
        assertPojoEquals(dbRole01, list.get(0));
    }

    @Test
    public void testGetRoleList() {
        // mock data
        RoleEntity dbRole01 = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(dbRole01);
        RoleEntity dbRole02 = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(dbRole02);

        // invoke
        List<RoleEntity> list = roleService.getRoleList();
        // assert
        assertEquals(2, list.size());
        assertPojoEquals(dbRole01, list.get(0));
        assertPojoEquals(dbRole02, list.get(1));
    }

    @Test
    public void testGetRoleList_ids() {
        // mock data
        RoleEntity dbRole01 = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(dbRole01);
        RoleEntity dbRole02 = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(dbRole02);
        // prepare parameters
        Collection<Long> ids = singleton(dbRole01.getId());

        // invoke
        List<RoleEntity> list = roleService.getRoleList(ids);
        // assert
        assertEquals(1, list.size());
        assertPojoEquals(dbRole01, list.get(0));
    }

    @Test
    public void testGetRoleListFromCache() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultRoleService.class)))
                    .thenReturn(roleService);

            // mock data
            RoleEntity dbRole = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
            roleMapper.insert(dbRole);
            // test id mismatch
            roleMapper.insert(cloneIgnoreId(dbRole, o -> {}));
            // prepare parameters
            Collection<Long> ids = singleton(dbRole.getId());

            // invoke
            List<RoleEntity> list = roleService.getRoleListFromCache(ids);
            // assert
            assertEquals(1, list.size());
            assertPojoEquals(dbRole, list.get(0));
        }
    }

    @Test
    public void testGetRolePage() {
        // mock data
        RoleEntity dbRole = randomPojo(RoleEntity.class, o -> { // will be queried later
            o.setName("Potato");
            o.setCode("tudou");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCreateTime(buildTime(2022, 2, 8));
        });
        roleMapper.insert(dbRole);
        // test name mismatch
        roleMapper.insert(cloneIgnoreId(dbRole, o -> o.setName("Carrot")));
        // test code mismatch
        roleMapper.insert(cloneIgnoreId(dbRole, o -> o.setCode("hong")));
        // test createTime mismatch
        roleMapper.insert(cloneIgnoreId(dbRole, o -> o.setCreateTime(buildTime(2022, 2, 16))));
        // prepare parameters
        RolePageRequest request = new RolePageRequest();
        request.setName("Potato");
        request.setCode("tu");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());
        request.setCreateTime(buildBetweenTime(2022, 2, 1, 2022, 2, 12));

        // invoke
        PageResult<RoleEntity> pageResult = roleService.getRolePage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbRole, pageResult.getList().get(0));
    }

    @Test
    public void testHasAnySuperAdmin_true() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultRoleService.class)))
                    .thenReturn(roleService);

            // mock data
            RoleEntity dbRole = randomPojo(RoleEntity.class).setCode("super_admin");
            roleMapper.insert(dbRole);
            // prepare parameters
            Long id = dbRole.getId();

            // invoke and call
            assertTrue(roleService.hasAnySuperAdmin(singletonList(id)));
        }
    }

    @Test
    public void testHasAnySuperAdmin_false() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultRoleService.class)))
                    .thenReturn(roleService);

            // mock data
            RoleEntity dbRole = randomPojo(RoleEntity.class).setCode("tenant_admin");
            roleMapper.insert(dbRole);
            // prepare parameters
            Long id = dbRole.getId();

            // invoke and call
            assertFalse(roleService.hasAnySuperAdmin(singletonList(id)));
        }
    }

    @Test
    public void testValidateRoleList_success() {
        // mock data
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(roleDO);
        // prepare parameters
        List<Long> ids = singletonList(roleDO.getId());

        // invoke, no assertion needed
        roleService.validateRoleList(ids);
    }

    @Test
    public void testValidateRoleList_notFound() {
        // prepare parameters
        List<Long> ids = singletonList(randomLongId());

        // invoke and assert exception
        assertServiceException(() -> roleService.validateRoleList(ids), ROLE_NOT_EXISTS);
    }

    @Test
    public void testValidateRoleList_notEnable() {
        // mock data
        RoleEntity RoleEntity = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(RoleEntity);
        // prepare parameters
        List<Long> ids = singletonList(RoleEntity.getId());

        // invoke and assert exception
        assertServiceException(() -> roleService.validateRoleList(ids), ROLE_IS_DISABLE, RoleEntity.getName());
    }
}
