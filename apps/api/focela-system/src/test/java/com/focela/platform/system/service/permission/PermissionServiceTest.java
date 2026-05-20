package com.focela.platform.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.common.api.system.permission.dto.DepartmentDataPermissionRpcResponse;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.domain.entity.department.DepartmentEntity;
import com.focela.platform.system.domain.entity.permission.MenuEntity;
import com.focela.platform.system.domain.entity.permission.RoleEntity;
import com.focela.platform.system.domain.entity.permission.RoleMenuEntity;
import com.focela.platform.system.domain.entity.permission.UserRoleEntity;
import com.focela.platform.system.domain.entity.user.UserEntity;
import com.focela.platform.system.repository.mapper.permission.RoleMenuMapper;
import com.focela.platform.system.repository.mapper.permission.UserRoleMapper;
import com.focela.platform.system.enums.permission.DataScopeEnum;
import com.focela.platform.system.service.department.DepartmentService;
import com.focela.platform.system.service.user.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static cn.hutool.core.collection.ListUtil.toList;
import static com.focela.platform.common.utils.collection.SetUtils.asSet;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.RandomUtils.randomLongId;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Import({DefaultPermissionService.class})
public class PermissionServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultPermissionService permissionService;

    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    @MockitoBean
    private RoleService roleService;
    @MockitoBean
    private MenuService menuService;
    @MockitoBean
    private DepartmentService departmentService;
    @MockitoBean
    private UserService userService;

    @Test
    public void testHasAnyPermissions_superAdmin() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultPermissionService.class)))
                    .thenReturn(permissionService);

            // prepare parameters
            Long userId = 1L;
            String[] roles = new String[]{"system:user:query", "system:user:create"};
            // mock roles for user login
            userRoleMapper.insert(randomPojo(UserRoleEntity.class).setUserId(userId).setRoleId(100L));
            RoleEntity role = randomPojo(RoleEntity.class, o -> o.setId(100L)
                    .setStatus(CommonStatusEnum.ENABLE.getStatus()));
            when(roleService.getRoleListFromCache(eq(singleton(100L)))).thenReturn(toList(role));
            // mock other methods
            when(roleService.hasAnySuperAdmin(eq(asSet(100L)))).thenReturn(true);

            // invoke, and assert
            assertTrue(permissionService.hasAnyPermissions(userId, roles));
        }
    }

    @Test
    public void testHasAnyPermissions_normal() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultPermissionService.class)))
                    .thenReturn(permissionService);

            // prepare parameters
            Long userId = 1L;
            String[] roles = new String[]{"system:user:query", "system:user:create"};
            // mock roles for user login
            userRoleMapper.insert(randomPojo(UserRoleEntity.class).setUserId(userId).setRoleId(100L));
            RoleEntity role = randomPojo(RoleEntity.class, o -> o.setId(100L)
                    .setStatus(CommonStatusEnum.ENABLE.getStatus()));
            when(roleService.getRoleListFromCache(eq(singleton(100L)))).thenReturn(toList(role));
            // mock menu
            Long menuId = 1000L;
            when(menuService.getMenuIdListByPermissionFromCache(
                    eq("system:user:create"))).thenReturn(singletonList(menuId));
            roleMenuMapper.insert(randomPojo(RoleMenuEntity.class).setRoleId(100L).setMenuId(1000L));

            // invoke, and assert
            assertTrue(permissionService.hasAnyPermissions(userId, roles));
        }
    }

    @Test
    public void testHasAnyRoles() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultPermissionService.class)))
                    .thenReturn(permissionService);

            // prepare parameters
            Long userId = 1L;
            String[] roles = new String[]{"yunai", "tudou"};
            // mock user-role cache
            userRoleMapper.insert(randomPojo(UserRoleEntity.class).setUserId(userId).setRoleId(100L));
            RoleEntity role = randomPojo(RoleEntity.class, o -> o.setId(100L).setCode("tudou")
                    .setStatus(CommonStatusEnum.ENABLE.getStatus()));
            when(roleService.getRoleListFromCache(eq(singleton(100L)))).thenReturn(toList(role));

            // invoke, and assert
            assertTrue(permissionService.hasAnyRoles(userId, roles));
        }
    }

    // ========== role-menu-related methods  ==========

    @Test
    public void testAssignRoleMenu() {
        // prepare parameters
        Long roleId = 1L;
        Set<Long> menuIds = asSet(200L, 300L);
        // mock data
        RoleMenuEntity roleMenu01 = randomPojo(RoleMenuEntity.class).setRoleId(1L).setMenuId(100L);
        roleMenuMapper.insert(roleMenu01);
        RoleMenuEntity roleMenu02 = randomPojo(RoleMenuEntity.class).setRoleId(1L).setMenuId(200L);
        roleMenuMapper.insert(roleMenu02);

        // invoke
        permissionService.assignRoleMenu(roleId, menuIds);
        // assert
        List<RoleMenuEntity> roleMenuList = roleMenuMapper.selectList();
        assertEquals(2, roleMenuList.size());
        assertEquals(1L, roleMenuList.get(0).getRoleId());
        assertEquals(200L, roleMenuList.get(0).getMenuId());
        assertEquals(1L, roleMenuList.get(1).getRoleId());
        assertEquals(300L, roleMenuList.get(1).getMenuId());
    }

    @Test
    public void testProcessRoleDeleted() {
        // prepare parameters
        Long roleId = randomLongId();
        // mock data UserRole
        UserRoleEntity userRoleEntity01 = randomPojo(UserRoleEntity.class, o -> o.setRoleId(roleId)); // to be deleted
        userRoleMapper.insert(userRoleEntity01);
        UserRoleEntity userRoleEntity02 = randomPojo(UserRoleEntity.class); // not to be deleted
        userRoleMapper.insert(userRoleEntity02);
        // mock data RoleMenu
        RoleMenuEntity roleMenuEntity01 = randomPojo(RoleMenuEntity.class, o -> o.setRoleId(roleId)); // to be deleted
        roleMenuMapper.insert(roleMenuEntity01);
        RoleMenuEntity roleMenuEntity02 = randomPojo(RoleMenuEntity.class); // not to be deleted
        roleMenuMapper.insert(roleMenuEntity02);

        // invoke
        permissionService.processRoleDeleted(roleId);
        // assert RoleMenuEntity data
        List<RoleMenuEntity> dbRoleMenus = roleMenuMapper.selectList();
        assertEquals(1, dbRoleMenus.size());
        assertPojoEquals(dbRoleMenus.get(0), roleMenuEntity02);
        // assert UserRoleEntity data
        List<UserRoleEntity> dbUserRoles = userRoleMapper.selectList();
        assertEquals(1, dbUserRoles.size());
        assertPojoEquals(dbUserRoles.get(0), userRoleEntity02);
    }

    @Test
    public void testProcessMenuDeleted() {
        // prepare parameters
        Long menuId = randomLongId();
        // mock data
        RoleMenuEntity roleMenuEntity01 = randomPojo(RoleMenuEntity.class, o -> o.setMenuId(menuId)); // to be deleted
        roleMenuMapper.insert(roleMenuEntity01);
        RoleMenuEntity roleMenuEntity02 = randomPojo(RoleMenuEntity.class); // not to be deleted
        roleMenuMapper.insert(roleMenuEntity02);

        // invoke
        permissionService.processMenuDeleted(menuId);
        // assert data
        List<RoleMenuEntity> dbRoleMenus = roleMenuMapper.selectList();
        assertEquals(1, dbRoleMenus.size());
        assertPojoEquals(dbRoleMenus.get(0), roleMenuEntity02);
    }

    @Test
    public void testGetRoleMenuIds_superAdmin() {
        // prepare parameters
        Long roleId = 100L;
        // mock the method
        when(roleService.hasAnySuperAdmin(eq(singleton(100L)))).thenReturn(true);
        List<MenuEntity> menuList = singletonList(randomPojo(MenuEntity.class).setId(1L));
        when(menuService.getMenuList()).thenReturn(menuList);

        // invoke
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(roleId);
        // assert
        assertEquals(singleton(1L), menuIds);
    }

    @Test
    public void testGetRoleMenuIds_normal() {
        // prepare parameters
        Long roleId = 100L;
        // mock data
        RoleMenuEntity roleMenu01 = randomPojo(RoleMenuEntity.class).setRoleId(100L).setMenuId(1L);
        roleMenuMapper.insert(roleMenu01);
        RoleMenuEntity roleMenu02 = randomPojo(RoleMenuEntity.class).setRoleId(100L).setMenuId(2L);
        roleMenuMapper.insert(roleMenu02);

        // invoke
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(roleId);
        // assert
        assertEquals(asSet(1L, 2L), menuIds);
    }

    @Test
    public void testGetMenuRoleIdListByMenuIdFromCache() {
        // prepare parameters
        Long menuId = 1L;
        // mock data
        RoleMenuEntity roleMenu01 = randomPojo(RoleMenuEntity.class).setRoleId(100L).setMenuId(1L);
        roleMenuMapper.insert(roleMenu01);
        RoleMenuEntity roleMenu02 = randomPojo(RoleMenuEntity.class).setRoleId(200L).setMenuId(1L);
        roleMenuMapper.insert(roleMenu02);

        // invoke
        Set<Long> roleIds = permissionService.getMenuRoleIdListByMenuIdFromCache(menuId);
        // assert
        assertEquals(asSet(100L, 200L), roleIds);
    }

    // ========== user-role-related methods  ==========

    @Test
    public void testAssignUserRole() {
        // prepare parameters
        Long userId = 1L;
        Set<Long> roleIds = asSet(200L, 300L);
        // mock data
        UserRoleEntity userRole01 = randomPojo(UserRoleEntity.class).setUserId(1L).setRoleId(100L);
        userRoleMapper.insert(userRole01);
        UserRoleEntity userRole02 = randomPojo(UserRoleEntity.class).setUserId(1L).setRoleId(200L);
        userRoleMapper.insert(userRole02);

        // invoke
        permissionService.assignUserRole(userId, roleIds);
        // assert
        List<UserRoleEntity> userRoleEntities = userRoleMapper.selectList();
        assertEquals(2, userRoleEntities.size());
        assertEquals(1L, userRoleEntities.get(0).getUserId());
        assertEquals(200L, userRoleEntities.get(0).getRoleId());
        assertEquals(1L, userRoleEntities.get(1).getUserId());
        assertEquals(300L, userRoleEntities.get(1).getRoleId());
    }

    @Test
    public void testProcessUserDeleted() {
        // prepare parameters
        Long userId = randomLongId();
        // mock data
        UserRoleEntity userRoleEntity01 = randomPojo(UserRoleEntity.class, o -> o.setUserId(userId)); // to be deleted
        userRoleMapper.insert(userRoleEntity01);
        UserRoleEntity userRoleEntity02 = randomPojo(UserRoleEntity.class); // not to be deleted
        userRoleMapper.insert(userRoleEntity02);

        // invoke
        permissionService.processUserDeleted(userId);
        // assert data
        List<UserRoleEntity> dbUserRoles = userRoleMapper.selectList();
        assertEquals(1, dbUserRoles.size());
        assertPojoEquals(dbUserRoles.get(0), userRoleEntity02);
    }

    @Test
    public void testGetUserRoleIdListByUserId() {
        // prepare parameters
        Long userId = 1L;
        // mock data
        UserRoleEntity userRoleEntity01 = randomPojo(UserRoleEntity.class, o -> o.setUserId(1L).setRoleId(10L));
        userRoleMapper.insert(userRoleEntity01);
        UserRoleEntity roleMenuEntity02 = randomPojo(UserRoleEntity.class, o -> o.setUserId(1L).setRoleId(20L));
        userRoleMapper.insert(roleMenuEntity02);

        // invoke
        Set<Long> result = permissionService.getUserRoleIdListByUserId(userId);
        // assert
        assertEquals(asSet(10L, 20L), result);
    }

    @Test
    public void testGetUserRoleIdListByUserIdFromCache() {
        // prepare parameters
        Long userId = 1L;
        // mock data
        UserRoleEntity userRoleEntity01 = randomPojo(UserRoleEntity.class, o -> o.setUserId(1L).setRoleId(10L));
        userRoleMapper.insert(userRoleEntity01);
        UserRoleEntity roleMenuEntity02 = randomPojo(UserRoleEntity.class, o -> o.setUserId(1L).setRoleId(20L));
        userRoleMapper.insert(roleMenuEntity02);

        // invoke
        Set<Long> result = permissionService.getUserRoleIdListByUserIdFromCache(userId);
        // assert
        assertEquals(asSet(10L, 20L), result);
    }

    @Test
    public void testGetUserRoleIdsFromCache() {
        // prepare parameters
        Long userId = 1L;
        // mock data
        UserRoleEntity userRoleEntity01 = randomPojo(UserRoleEntity.class, o -> o.setUserId(1L).setRoleId(10L));
        userRoleMapper.insert(userRoleEntity01);
        UserRoleEntity roleMenuEntity02 = randomPojo(UserRoleEntity.class, o -> o.setUserId(1L).setRoleId(20L));
        userRoleMapper.insert(roleMenuEntity02);

        // invoke
        Set<Long> result = permissionService.getUserRoleIdListByUserIdFromCache(userId);
        // assert
        assertEquals(asSet(10L, 20L), result);
    }

    @Test
    public void testGetUserRoleIdListByRoleId() {
        // prepare parameters
        Collection<Long> roleIds = asSet(10L, 20L);
        // mock data
        UserRoleEntity userRoleEntity01 = randomPojo(UserRoleEntity.class, o -> o.setUserId(1L).setRoleId(10L));
        userRoleMapper.insert(userRoleEntity01);
        UserRoleEntity roleMenuEntity02 = randomPojo(UserRoleEntity.class, o -> o.setUserId(2L).setRoleId(20L));
        userRoleMapper.insert(roleMenuEntity02);

        // invoke
        Set<Long> result = permissionService.getUserRoleIdListByRoleId(roleIds);
        // assert
        assertEquals(asSet(1L, 2L), result);
    }

    @Test
    public void testGetEnableUserRoleListByUserIdFromCache() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultPermissionService.class)))
                    .thenReturn(permissionService);

            // prepare parameters
            Long userId = 1L;
            // mock roles for user login
            userRoleMapper.insert(randomPojo(UserRoleEntity.class).setUserId(userId).setRoleId(100L));
            userRoleMapper.insert(randomPojo(UserRoleEntity.class).setUserId(userId).setRoleId(200L));
            RoleEntity role01 = randomPojo(RoleEntity.class, o -> o.setId(100L)
                    .setStatus(CommonStatusEnum.ENABLE.getStatus()));
            RoleEntity role02 = randomPojo(RoleEntity.class, o -> o.setId(200L)
                    .setStatus(CommonStatusEnum.DISABLE.getStatus()));
            when(roleService.getRoleListFromCache(eq(asSet(100L, 200L))))
                    .thenReturn(toList(role01, role02));

            // invoke
            List<RoleEntity> result = permissionService.getEnableUserRoleListByUserIdFromCache(userId);
            // assert
            assertEquals(1, result.size());
            assertPojoEquals(role01, result.get(0));
        }
    }

    // ========== user-department-related methods  ==========

    @Test
    public void testAssignRoleDataScope() {
        // prepare parameters
        Long roleId = 1L;
        Integer dataScope = 2;
        Set<Long> dataScopeDeptIds = asSet(10L, 20L);

        // invoke
        permissionService.assignRoleDataScope(roleId, dataScope, dataScopeDeptIds);
        // assert
        verify(roleService).updateRoleDataScope(eq(roleId), eq(dataScope), eq(dataScopeDeptIds));
    }

    @Test
    public void testGetDeptDataPermission_All() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultPermissionService.class)))
                    .thenReturn(permissionService);

            // prepare parameters
            Long userId = 1L;
            // mock user role IDs
            userRoleMapper.insert(randomPojo(UserRoleEntity.class).setUserId(userId).setRoleId(2L));
            // mock get user roles
            RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setDataScope(DataScopeEnum.ALL.getScope())
                    .setStatus(CommonStatusEnum.ENABLE.getStatus()));
            when(roleService.getRoleListFromCache(eq(singleton(2L)))).thenReturn(toList(roleEntity));

            // invoke
            DepartmentDataPermissionRpcResponse result = permissionService.getDeptDataPermission(userId);
            // assert
            assertTrue(result.getAll());
            assertFalse(result.getSelf());
            assertTrue(CollUtil.isEmpty(result.getDeptIds()));
        }
    }

    @Test
    public void testGetDeptDataPermission_DeptCustom() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultPermissionService.class)))
                    .thenReturn(permissionService);

            // prepare parameters
            Long userId = 1L;
            // mock user role IDs
            userRoleMapper.insert(randomPojo(UserRoleEntity.class).setUserId(userId).setRoleId(2L));
            // mock get user roles
            RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setDataScope(DataScopeEnum.DEPARTMENT_CUSTOM.getScope())
                    .setStatus(CommonStatusEnum.ENABLE.getStatus()));
            when(roleService.getRoleListFromCache(eq(singleton(2L)))).thenReturn(toList(roleEntity));
            // mock department return
            when(userService.getUser(eq(1L))).thenReturn(new UserEntity().setDeptId(3L),
                    null, null); // returning null at the end is intentional, to verify there is no duplicate invoke

            // invoke
            DepartmentDataPermissionRpcResponse result = permissionService.getDeptDataPermission(userId);
            // assert
            assertFalse(result.getAll());
            assertFalse(result.getSelf());
            assertEquals(roleEntity.getDataScopeDeptIds().size() + 1, result.getDeptIds().size());
            assertTrue(CollUtil.containsAll(result.getDeptIds(), roleEntity.getDataScopeDeptIds()));
            assertTrue(CollUtil.contains(result.getDeptIds(), 3L));
        }
    }

    @Test
    public void testGetDeptDataPermission_DeptOnly() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultPermissionService.class)))
                    .thenReturn(permissionService);

            // prepare parameters
            Long userId = 1L;
            // mock user role IDs
            userRoleMapper.insert(randomPojo(UserRoleEntity.class).setUserId(userId).setRoleId(2L));
            // mock get user roles
            RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setDataScope(DataScopeEnum.DEPARTMENT_ONLY.getScope())
                    .setStatus(CommonStatusEnum.ENABLE.getStatus()));
            when(roleService.getRoleListFromCache(eq(singleton(2L)))).thenReturn(toList(roleEntity));
            // mock department return
            when(userService.getUser(eq(1L))).thenReturn(new UserEntity().setDeptId(3L),
                    null, null); // returning null at the end is intentional, to verify there is no duplicate invoke

            // invoke
            DepartmentDataPermissionRpcResponse result = permissionService.getDeptDataPermission(userId);
            // assert
            assertFalse(result.getAll());
            assertFalse(result.getSelf());
            assertEquals(1, result.getDeptIds().size());
            assertTrue(CollUtil.contains(result.getDeptIds(), 3L));
        }
    }

    @Test
    public void testGetDeptDataPermission_DeptAndChild() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultPermissionService.class)))
                    .thenReturn(permissionService);

            // prepare parameters
            Long userId = 1L;
            // mock user role IDs
            userRoleMapper.insert(randomPojo(UserRoleEntity.class).setUserId(userId).setRoleId(2L));
            // mock get user roles
            RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setDataScope(DataScopeEnum.DEPARTMENT_AND_CHILD.getScope())
                    .setStatus(CommonStatusEnum.ENABLE.getStatus()));
            when(roleService.getRoleListFromCache(eq(singleton(2L)))).thenReturn(toList(roleEntity));
            // mock department return
            when(userService.getUser(eq(1L))).thenReturn(new UserEntity().setDeptId(3L),
                    null, null); // returning null at the end is intentional, to verify there is no duplicate invoke
            // mock the method（department)
            DepartmentEntity deptEntity = randomPojo(DepartmentEntity.class);
            when(departmentService.getChildDepartmentIdListFromCache(eq(3L))).thenReturn(singleton(deptEntity.getId()));

            // invoke
            DepartmentDataPermissionRpcResponse result = permissionService.getDeptDataPermission(userId);
            // assert
            assertFalse(result.getAll());
            assertFalse(result.getSelf());
            assertEquals(2, result.getDeptIds().size());
            assertTrue(CollUtil.contains(result.getDeptIds(), deptEntity.getId()));
            assertTrue(CollUtil.contains(result.getDeptIds(), 3L));
        }
    }

    @Test
    public void testGetDeptDataPermission_Self() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(DefaultPermissionService.class)))
                    .thenReturn(permissionService);

            // prepare parameters
            Long userId = 1L;
            // mock user role IDs
            userRoleMapper.insert(randomPojo(UserRoleEntity.class).setUserId(userId).setRoleId(2L));
            // mock get user roles
            RoleEntity roleEntity = randomPojo(RoleEntity.class, o -> o.setDataScope(DataScopeEnum.SELF.getScope())
                    .setStatus(CommonStatusEnum.ENABLE.getStatus()));
            when(roleService.getRoleListFromCache(eq(singleton(2L)))).thenReturn(toList(roleEntity));

            // invoke
            DepartmentDataPermissionRpcResponse result = permissionService.getDeptDataPermission(userId);
            // assert
            assertFalse(result.getAll());
            assertTrue(result.getSelf());
            assertTrue(CollUtil.isEmpty(result.getDeptIds()));
        }
    }

}
