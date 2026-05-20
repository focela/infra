package com.focela.platform.system.service.permission;

import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.permission.request.role.RolePageRequest;
import com.focela.platform.system.controller.admin.permission.request.role.RoleSaveRequest;
import com.focela.platform.system.domain.entity.permission.RoleEntity;
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
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
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
        RoleEntity roleEntity = roleMapper.selectById(roleId);
        assertPojoEquals(request, roleEntity, "id");
        assertEquals(RoleTypeEnum.CUSTOM.getType(), roleEntity.getType());
        assertEquals(DataScopeEnum.ALL.getScope(), roleEntity.getDataScope());
    }

    @Test
    public void testUpdateRole() {
        // mock data
        RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleEntity);
        // prepare parameters
        Long id = roleEntity.getId();
        RoleSaveRequest request = randomPojo(RoleSaveRequest.class, o -> o.setId(id)
                .setStatus(randomCommonStatus()));

        // invoke
        roleService.updateRole(request);
        // assert
        RoleEntity newRoleEntity = roleMapper.selectById(id);
        assertPojoEquals(request, newRoleEntity);
    }

    @Test
    public void testUpdateRoleDataScope() {
        // mock data
        RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleEntity);
        // prepare parameters
        Long id = roleEntity.getId();
        Integer dataScope = randomEle(DataScopeEnum.values()).getScope();
        Set<Long> dataScopeRoleIds = randomSet(Long.class);

        // invoke
        roleService.updateRoleDataScope(id, dataScope, dataScopeRoleIds);
        // assert
        RoleEntity dbRoleEntity = roleMapper.selectById(id);
        assertEquals(dataScope, dbRoleEntity.getDataScope());
        assertEquals(dataScopeRoleIds, dbRoleEntity.getDataScopeDeptIds());
    }

    @Test
    public void testDeleteRole() {
        // mock data
        RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleEntity);
        // prepare parameters
        Long id = roleEntity.getId();

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
        RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setName("role_name"));
        roleMapper.insert(roleEntity);
        // prepare parameters
        String name = "role_name";

        // invoke, and assert exception
        assertServiceException(() -> roleService.validateRoleDuplicate(name, randomString(), null),
                ROLE_NAME_DUPLICATE, name);
    }

    @Test
    public void testValidateRoleDuplicate_codeDuplicate() {
        // mock data
        RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setCode("code"));
        roleMapper.insert(roleEntity);
        // prepare parameters
        String code = "code";

        // invoke, and assert exception
        assertServiceException(() -> roleService.validateRoleDuplicate(randomString(), code, null),
                ROLE_CODE_DUPLICATE, code);
    }

    @Test
    public void testValidateUpdateRole_success() {
        RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleEntity);
        // prepare parameters
        Long id = roleEntity.getId();

        // invoke, no exception
        roleService.validateRoleForUpdate(id);
    }

    @Test
    public void testValidateUpdateRole_roleIdNotExist() {
        assertServiceException(() -> roleService.validateRoleForUpdate(randomLongId()), ROLE_NOT_FOUND);
    }

    @Test
    public void testValidateUpdateRole_systemRoleCanNotBeUpdate() {
        RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.SYSTEM.getType()));
        roleMapper.insert(roleEntity);
        // prepare parameters
        Long id = roleEntity.getId();

        assertServiceException(() -> roleService.validateRoleForUpdate(id),
                ROLE_SYSTEM_TYPE_UPDATE_NOT_ALLOWED);
    }

    @Test
    public void testGetRole() {
        // mock data
        RoleEntity roleEntity = randomPojo(RoleEntity.class);
        roleMapper.insert(roleEntity);
        // prepare parameters
        Long id = roleEntity.getId();

        // invoke
        RoleEntity dbRoleEntity = roleService.getRole(id);
        // assert
        assertPojoEquals(roleEntity, dbRoleEntity);
    }

    @Test
    public void testGetRoleFromCache() {
        // mock data（cache）
        RoleEntity roleEntity = randomPojo(RoleEntity.class);
        roleMapper.insert(roleEntity);
        // prepare parameters
        Long id = roleEntity.getId();

        // invoke
        RoleEntity dbRoleEntity = roleService.getRoleFromCache(id);
        // assert
        assertPojoEquals(roleEntity, dbRoleEntity);
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
            o.setCode("focela_alternate");
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
        request.setCode("focela_alternate");
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
        RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(roleEntity);
        // prepare parameters
        List<Long> ids = singletonList(roleEntity.getId());

        // invoke, no assertion needed
        roleService.validateRoleList(ids);
    }

    @Test
    public void testValidateRoleList_notFound() {
        // prepare parameters
        List<Long> ids = singletonList(randomLongId());

        // invoke and assert exception
        assertServiceException(() -> roleService.validateRoleList(ids), ROLE_NOT_FOUND);
    }

    @Test
    public void testValidateRoleList_notEnable() {
        // mock data
        RoleEntity RoleEntity = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(RoleEntity);
        // prepare parameters
        List<Long> ids = singletonList(RoleEntity.getId());

        // invoke and assert exception
        assertServiceException(() -> roleService.validateRoleList(ids), ROLE_DISABLED, RoleEntity.getName());
    }
}
