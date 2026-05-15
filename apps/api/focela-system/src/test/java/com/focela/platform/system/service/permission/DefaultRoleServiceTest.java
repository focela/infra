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
        // 准备参数
        RoleSaveRequest request = randomPojo(RoleSaveRequest.class)
                .setId(null)  // 防止 id 被赋值
                .setStatus(randomCommonStatus());

        // 调用
        Long roleId = roleService.createRole(request, null);
        // 断言
        RoleEntity roleDO = roleMapper.selectById(roleId);
        assertPojoEquals(request, roleDO, "id");
        assertEquals(RoleTypeEnum.CUSTOM.getType(), roleDO.getType());
        assertEquals(DataScopeEnum.ALL.getScope(), roleDO.getDataScope());
    }

    @Test
    public void testUpdateRole() {
        // mock 数据
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);
        // 准备参数
        Long id = roleDO.getId();
        RoleSaveRequest request = randomPojo(RoleSaveRequest.class, o -> o.setId(id)
                .setStatus(randomCommonStatus()));

        // 调用
        roleService.updateRole(request);
        // 断言
        RoleEntity newRoleDO = roleMapper.selectById(id);
        assertPojoEquals(request, newRoleDO);
    }

    @Test
    public void testUpdateRoleDataScope() {
        // mock 数据
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);
        // 准备参数
        Long id = roleDO.getId();
        Integer dataScope = randomEle(DataScopeEnum.values()).getScope();
        Set<Long> dataScopeRoleIds = randomSet(Long.class);

        // 调用
        roleService.updateRoleDataScope(id, dataScope, dataScopeRoleIds);
        // 断言
        RoleEntity dbRoleDO = roleMapper.selectById(id);
        assertEquals(dataScope, dbRoleDO.getDataScope());
        assertEquals(dataScopeRoleIds, dbRoleDO.getDataScopeDeptIds());
    }

    @Test
    public void testDeleteRole() {
        // mock 数据
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);
        // 参数准备
        Long id = roleDO.getId();

        // 调用
        roleService.deleteRole(id);
        // 断言
        assertNull(roleMapper.selectById(id));
        // verify 删除相关数据
        verify(permissionService).processRoleDeleted(id);
    }

    @Test
    public void testValidateRoleDuplicate_success() {
        // 调用，不会抛异常
        roleService.validateRoleDuplicate(randomString(), randomString(), null);
    }

    @Test
    public void testValidateRoleDuplicate_nameDuplicate() {
        // mock 数据
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setName("role_name"));
        roleMapper.insert(roleDO);
        // 准备参数
        String name = "role_name";

        // 调用，并断言异常
        assertServiceException(() -> roleService.validateRoleDuplicate(name, randomString(), null),
                ROLE_NAME_DUPLICATE, name);
    }

    @Test
    public void testValidateRoleDuplicate_codeDuplicate() {
        // mock 数据
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setCode("code"));
        roleMapper.insert(roleDO);
        // 准备参数
        String code = "code";

        // 调用，并断言异常
        assertServiceException(() -> roleService.validateRoleDuplicate(randomString(), code, null),
                ROLE_CODE_DUPLICATE, code);
    }

    @Test
    public void testValidateUpdateRole_success() {
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);
        // 准备参数
        Long id = roleDO.getId();

        // 调用，无异常
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
        // 准备参数
        Long id = roleDO.getId();

        assertServiceException(() -> roleService.validateRoleForUpdate(id),
                ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
    }

    @Test
    public void testGetRole() {
        // mock 数据
        RoleEntity roleDO = randomPojo(RoleEntity.class);
        roleMapper.insert(roleDO);
        // 参数准备
        Long id = roleDO.getId();

        // 调用
        RoleEntity dbRoleDO = roleService.getRole(id);
        // 断言
        assertPojoEquals(roleDO, dbRoleDO);
    }

    @Test
    public void testGetRoleFromCache() {
        // mock 数据（缓存）
        RoleEntity roleDO = randomPojo(RoleEntity.class);
        roleMapper.insert(roleDO);
        // 参数准备
        Long id = roleDO.getId();

        // 调用
        RoleEntity dbRoleDO = roleService.getRoleFromCache(id);
        // 断言
        assertPojoEquals(roleDO, dbRoleDO);
    }

    @Test
    public void testGetRoleListByStatus() {
        // mock 数据
        RoleEntity dbRole01 = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(dbRole01);
        RoleEntity dbRole02 = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(dbRole02);

        // 调用
        List<RoleEntity> list = roleService.getRoleListByStatus(
                singleton(CommonStatusEnum.ENABLE.getStatus()));
        // 断言
        assertEquals(1, list.size());
        assertPojoEquals(dbRole01, list.get(0));
    }

    @Test
    public void testGetRoleList() {
        // mock 数据
        RoleEntity dbRole01 = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(dbRole01);
        RoleEntity dbRole02 = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(dbRole02);

        // 调用
        List<RoleEntity> list = roleService.getRoleList();
        // 断言
        assertEquals(2, list.size());
        assertPojoEquals(dbRole01, list.get(0));
        assertPojoEquals(dbRole02, list.get(1));
    }

    @Test
    public void testGetRoleList_ids() {
        // mock 数据
        RoleEntity dbRole01 = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(dbRole01);
        RoleEntity dbRole02 = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(dbRole02);
        // 准备参数
        Collection<Long> ids = singleton(dbRole01.getId());

        // 调用
        List<RoleEntity> list = roleService.getRoleList(ids);
        // 断言
        assertEquals(1, list.size());
        assertPojoEquals(dbRole01, list.get(0));
    }

    @Test
    public void testGetRoleListFromCache() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultRoleService.class)))
                    .thenReturn(roleService);

            // mock 数据
            RoleEntity dbRole = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
            roleMapper.insert(dbRole);
            // 测试 id 不匹配
            roleMapper.insert(cloneIgnoreId(dbRole, o -> {}));
            // 准备参数
            Collection<Long> ids = singleton(dbRole.getId());

            // 调用
            List<RoleEntity> list = roleService.getRoleListFromCache(ids);
            // 断言
            assertEquals(1, list.size());
            assertPojoEquals(dbRole, list.get(0));
        }
    }

    @Test
    public void testGetRolePage() {
        // mock 数据
        RoleEntity dbRole = randomPojo(RoleEntity.class, o -> { // 等会查询到
            o.setName("土豆");
            o.setCode("tudou");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCreateTime(buildTime(2022, 2, 8));
        });
        roleMapper.insert(dbRole);
        // 测试 name 不匹配
        roleMapper.insert(cloneIgnoreId(dbRole, o -> o.setName("红薯")));
        // 测试 code 不匹配
        roleMapper.insert(cloneIgnoreId(dbRole, o -> o.setCode("hong")));
        // 测试 createTime 不匹配
        roleMapper.insert(cloneIgnoreId(dbRole, o -> o.setCreateTime(buildTime(2022, 2, 16))));
        // 准备参数
        RolePageRequest request = new RolePageRequest();
        request.setName("土豆");
        request.setCode("tu");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());
        request.setCreateTime(buildBetweenTime(2022, 2, 1, 2022, 2, 12));

        // 调用
        PageResult<RoleEntity> pageResult = roleService.getRolePage(request);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbRole, pageResult.getList().get(0));
    }

    @Test
    public void testHasAnySuperAdmin_true() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultRoleService.class)))
                    .thenReturn(roleService);

            // mock 数据
            RoleEntity dbRole = randomPojo(RoleEntity.class).setCode("super_admin");
            roleMapper.insert(dbRole);
            // 准备参数
            Long id = dbRole.getId();

            // 调用，并调用
            assertTrue(roleService.hasAnySuperAdmin(singletonList(id)));
        }
    }

    @Test
    public void testHasAnySuperAdmin_false() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultRoleService.class)))
                    .thenReturn(roleService);

            // mock 数据
            RoleEntity dbRole = randomPojo(RoleEntity.class).setCode("tenant_admin");
            roleMapper.insert(dbRole);
            // 准备参数
            Long id = dbRole.getId();

            // 调用，并调用
            assertFalse(roleService.hasAnySuperAdmin(singletonList(id)));
        }
    }

    @Test
    public void testValidateRoleList_success() {
        // mock 数据
        RoleEntity roleDO = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(roleDO);
        // 准备参数
        List<Long> ids = singletonList(roleDO.getId());

        // 调用，无需断言
        roleService.validateRoleList(ids);
    }

    @Test
    public void testValidateRoleList_notFound() {
        // 准备参数
        List<Long> ids = singletonList(randomLongId());

        // 调用, 并断言异常
        assertServiceException(() -> roleService.validateRoleList(ids), ROLE_NOT_EXISTS);
    }

    @Test
    public void testValidateRoleList_notEnable() {
        // mock 数据
        RoleEntity RoleEntity = randomPojo(RoleEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(RoleEntity);
        // 准备参数
        List<Long> ids = singletonList(RoleEntity.getId());

        // 调用, 并断言异常
        assertServiceException(() -> roleService.validateRoleList(ids), ROLE_IS_DISABLE, RoleEntity.getName());
    }
}
