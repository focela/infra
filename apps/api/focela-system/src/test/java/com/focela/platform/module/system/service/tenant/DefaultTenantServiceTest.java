package com.focela.platform.module.system.service.tenant;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.tenant.config.TenantProperties;
import com.focela.platform.framework.tenant.core.context.TenantContextHolder;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.module.system.controller.admin.tenant.dto.TenantPageRequest;
import com.focela.platform.module.system.controller.admin.tenant.dto.TenantSaveRequest;
import com.focela.platform.module.system.entity.permission.MenuEntity;
import com.focela.platform.module.system.entity.permission.RoleEntity;
import com.focela.platform.module.system.entity.tenant.TenantEntity;
import com.focela.platform.module.system.entity.tenant.TenantPackageEntity;
import com.focela.platform.module.system.repository.mapper.tenant.TenantMapper;
import com.focela.platform.module.system.enums.permission.RoleCodeEnum;
import com.focela.platform.module.system.enums.permission.RoleTypeEnum;
import com.focela.platform.module.system.service.permission.MenuService;
import com.focela.platform.module.system.service.permission.PermissionService;
import com.focela.platform.module.system.service.permission.RoleService;
import com.focela.platform.module.system.service.tenant.handler.TenantInfoHandler;
import com.focela.platform.module.system.service.tenant.handler.TenantMenuHandler;
import com.focela.platform.module.system.service.user.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.focela.platform.framework.common.utils.collection.SetUtils.asSet;
import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.framework.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.utils.RandomUtils.*;
import static com.focela.platform.module.system.entity.tenant.TenantEntity.PACKAGE_ID_SYSTEM;
import static com.focela.platform.module.system.constants.ErrorCodeConstants.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link DefaultTenantService} 的单元测试类
 */
@Import(DefaultTenantService.class)
public class DefaultTenantServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultTenantService tenantService;

    @Resource
    private TenantMapper tenantMapper;

    @MockitoBean
    private TenantProperties tenantProperties;
    @MockitoBean
    private TenantPackageService tenantPackageService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private RoleService roleService;
    @MockitoBean
    private MenuService menuService;
    @MockitoBean
    private PermissionService permissionService;

    @BeforeEach
    public void setUp() {
        // 清理租户上下文
        TenantContextHolder.clear();
    }

    @Test
    public void testGetTenantIdList() {
        // mock 数据
        TenantEntity tenant = randomPojo(TenantEntity.class, o -> o.setId(1L));
        tenantMapper.insert(tenant);

        // 调用，并断言业务异常
        List<Long> result = tenantService.getTenantIdList();
        assertEquals(Collections.singletonList(1L), result);
    }

    @Test
    public void testValidTenant_notExists() {
        assertServiceException(() -> tenantService.validTenant(randomLongId()), TENANT_NOT_EXISTS);
    }

    @Test
    public void testValidTenant_disable() {
        // mock 数据
        TenantEntity tenant = randomPojo(TenantEntity.class, o -> o.setId(1L).setStatus(CommonStatusEnum.DISABLE.getStatus()));
        tenantMapper.insert(tenant);

        // 调用，并断言业务异常
        assertServiceException(() -> tenantService.validTenant(1L), TENANT_DISABLE, tenant.getName());
    }

    @Test
    public void testValidTenant_expired() {
        // mock 数据
        TenantEntity tenant = randomPojo(TenantEntity.class, o -> o.setId(1L).setStatus(CommonStatusEnum.ENABLE.getStatus())
                .setExpireTime(buildTime(2020, 2, 2)));
        tenantMapper.insert(tenant);

        // 调用，并断言业务异常
        assertServiceException(() -> tenantService.validTenant(1L), TENANT_EXPIRE, tenant.getName());
    }

    @Test
    public void testValidTenant_success() {
        // mock 数据
        TenantEntity tenant = randomPojo(TenantEntity.class, o -> o.setId(1L).setStatus(CommonStatusEnum.ENABLE.getStatus())
                .setExpireTime(LocalDateTime.now().plusDays(1)));
        tenantMapper.insert(tenant);

        // 调用，并断言业务异常
        tenantService.validTenant(1L);
    }

    @Test
    public void testCreateTenant() {
        // mock 套餐 100L
        TenantPackageEntity tenantPackage = randomPojo(TenantPackageEntity.class, o -> o.setId(100L));
        when(tenantPackageService.validTenantPackage(eq(100L))).thenReturn(tenantPackage);
        // mock 角色 200L
        when(roleService.createRole(argThat(role -> {
            assertEquals(RoleCodeEnum.TENANT_ADMIN.getName(), role.getName());
            assertEquals(RoleCodeEnum.TENANT_ADMIN.getCode(), role.getCode());
            assertEquals(0, role.getSort());
            assertEquals("系统自动生成", role.getRemark());
            return true;
        }), eq(RoleTypeEnum.SYSTEM.getType()))).thenReturn(200L);
        // mock 用户 300L
        when(userService.createUser(argThat(user -> {
            assertEquals("yunai", user.getUsername());
            assertEquals("yuanma", user.getPassword());
            assertEquals("芋道", user.getNickname());
            assertEquals("15601691300", user.getMobile());
            return true;
        }))).thenReturn(300L);

        // 准备参数
        TenantSaveRequest request = randomPojo(TenantSaveRequest.class, o -> {
            o.setContactName("芋道");
            o.setContactMobile("15601691300");
            o.setPackageId(100L);
            o.setStatus(randomCommonStatus());
            o.setWebsites(singletonList("https://www.example.com"));
            o.setUsername("yunai");
            o.setPassword("yuanma");
        }).setId(null); // 设置为 null，方便后面校验

        // 调用
        Long tenantId = tenantService.createTenant(request);
        // 断言
        assertNotNull(tenantId);
        // 校验记录的属性是否正确
        TenantEntity tenant = tenantMapper.selectById(tenantId);
        assertPojoEquals(request, tenant, "id");
        assertEquals(300L, tenant.getContactUserId());
        // verify 分配权限
        verify(permissionService).assignRoleMenu(eq(200L), same(tenantPackage.getMenuIds()));
        // verify 分配角色
        verify(permissionService).assignUserRole(eq(300L), eq(singleton(200L)));
    }

    @Test
    public void testUpdateTenant_success() {
        // mock 数据
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setStatus(randomCommonStatus()));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        // 准备参数
        TenantSaveRequest request = randomPojo(TenantSaveRequest.class, o -> {
            o.setId(dbTenant.getId()); // 设置更新的 ID
            o.setStatus(randomCommonStatus());
            o.setWebsites(singletonList(randomString()));
        });

        // mock 套餐
        TenantPackageEntity tenantPackage = randomPojo(TenantPackageEntity.class,
                o -> o.setMenuIds(asSet(200L, 201L)));
        when(tenantPackageService.validTenantPackage(eq(request.getPackageId()))).thenReturn(tenantPackage);
        // mock 所有角色
        RoleEntity role100 = randomPojo(RoleEntity.class, o -> o.setId(100L).setCode(RoleCodeEnum.TENANT_ADMIN.getCode()));
        role100.setTenantId(dbTenant.getId());
        RoleEntity role101 = randomPojo(RoleEntity.class, o -> o.setId(101L));
        role101.setTenantId(dbTenant.getId());
        when(roleService.getRoleList()).thenReturn(asList(role100, role101));
        // mock 每个角色的权限
        when(permissionService.getRoleMenuListByRoleId(eq(101L))).thenReturn(asSet(201L, 202L));

        // 调用
        tenantService.updateTenant(request);
        // 校验是否更新正确
        TenantEntity tenant = tenantMapper.selectById(request.getId()); // 获取最新的
        assertPojoEquals(request, tenant);
        // verify 设置角色权限
        verify(permissionService).assignRoleMenu(eq(100L), eq(asSet(200L, 201L)));
        verify(permissionService).assignRoleMenu(eq(101L), eq(asSet(201L)));
    }

    @Test
    public void testUpdateTenant_notExists() {
        // 准备参数
        TenantSaveRequest request = randomPojo(TenantSaveRequest.class);

        // 调用, 并断言异常
        assertServiceException(() -> tenantService.updateTenant(request), TENANT_NOT_EXISTS);
    }

    @Test
    public void testUpdateTenant_system() {
        // mock 数据
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setPackageId(PACKAGE_ID_SYSTEM));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        // 准备参数
        TenantSaveRequest request = randomPojo(TenantSaveRequest.class, o -> {
            o.setId(dbTenant.getId()); // 设置更新的 ID
        });

        // 调用，校验业务异常
        assertServiceException(() -> tenantService.updateTenant(request), TENANT_CAN_NOT_UPDATE_SYSTEM);
    }

    @Test
    public void testDeleteTenant_success() {
        // mock 数据
        TenantEntity dbTenant = randomPojo(TenantEntity.class,
                o -> o.setStatus(randomCommonStatus()));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbTenant.getId();

        // 调用
        tenantService.deleteTenant(id);
        // 校验数据不存在了
        assertNull(tenantMapper.selectById(id));
    }

    @Test
    public void testDeleteTenant_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> tenantService.deleteTenant(id), TENANT_NOT_EXISTS);
    }

    @Test
    public void testDeleteTenant_system() {
        // mock 数据
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setPackageId(PACKAGE_ID_SYSTEM));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbTenant.getId();

        // 调用, 并断言异常
        assertServiceException(() -> tenantService.deleteTenant(id), TENANT_CAN_NOT_UPDATE_SYSTEM);
    }

    @Test
    public void testGetTenant() {
        // mock 数据
        TenantEntity dbTenant = randomPojo(TenantEntity.class);
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbTenant.getId();

        // 调用
        TenantEntity result = tenantService.getTenant(id);
        // 校验存在
        assertPojoEquals(result, dbTenant);
    }

    @Test
    public void testGetTenantPage() {
        // mock 数据
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> { // 等会查询到
            o.setName("芋道源码");
            o.setContactName("芋艿");
            o.setContactMobile("15601691300");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCreateTime(buildTime(2020, 12, 12));
        });
        tenantMapper.insert(dbTenant);
        // 测试 name 不匹配
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setName(randomString())));
        // 测试 contactName 不匹配
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setContactName(randomString())));
        // 测试 contactMobile 不匹配
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setContactMobile(randomString())));
        // 测试 status 不匹配
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // 测试 createTime 不匹配
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setCreateTime(buildTime(2021, 12, 12))));
        // 准备参数
        TenantPageRequest request = new TenantPageRequest();
        request.setName("芋道");
        request.setContactName("艿");
        request.setContactMobile("1560");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());
        request.setCreateTime(buildBetweenTime(2020, 12, 1, 2020, 12, 24));

        // 调用
        PageResult<TenantEntity> pageResult = tenantService.getTenantPage(request);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbTenant, pageResult.getList().get(0));
    }

    @Test
    public void testGetTenantByName() {
        // mock 数据
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setName("芋道"));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据

        // 调用
        TenantEntity result = tenantService.getTenantByName("芋道");
        // 校验存在
        assertPojoEquals(result, dbTenant);
    }

    @Test
    @Disabled // H2 不支持 find_in_set 函数
    public void testGetTenantByWebsite() {
        // mock 数据
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setWebsites(singletonList("https://www.example.com")));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据

        // 调用
        TenantEntity result = tenantService.getTenantByWebsite("https://www.example.com");
        // 校验存在
        assertPojoEquals(result, dbTenant);
    }

    @Test
    public void testGetTenantListByPackageId() {
        // mock 数据
        TenantEntity dbTenant1 = randomPojo(TenantEntity.class, o -> o.setPackageId(1L));
        tenantMapper.insert(dbTenant1);// @Sql: 先插入出一条存在的数据
        TenantEntity dbTenant2 = randomPojo(TenantEntity.class, o -> o.setPackageId(2L));
        tenantMapper.insert(dbTenant2);// @Sql: 先插入出一条存在的数据

        // 调用
        List<TenantEntity> result = tenantService.getTenantListByPackageId(1L);
        assertEquals(1, result.size());
        assertPojoEquals(dbTenant1, result.get(0));
    }

    @Test
    public void testGetTenantCountByPackageId() {
        // mock 数据
        TenantEntity dbTenant1 = randomPojo(TenantEntity.class, o -> o.setPackageId(1L));
        tenantMapper.insert(dbTenant1);// @Sql: 先插入出一条存在的数据
        TenantEntity dbTenant2 = randomPojo(TenantEntity.class, o -> o.setPackageId(2L));
        tenantMapper.insert(dbTenant2);// @Sql: 先插入出一条存在的数据

        // 调用
        Long count = tenantService.getTenantCountByPackageId(1L);
        assertEquals(1, count);
    }

    @Test
    public void testHandleTenantInfo_disable() {
        // 准备参数
        TenantInfoHandler handler = mock(TenantInfoHandler.class);
        // mock 禁用
        when(tenantProperties.getEnable()).thenReturn(false);

        // 调用
        tenantService.handleTenantInfo(handler);
        // 断言
        verify(handler, never()).handle(any());
    }

    @Test
    public void testHandleTenantInfo_success() {
        // 准备参数
        TenantInfoHandler handler = mock(TenantInfoHandler.class);
        // mock 未禁用
        when(tenantProperties.getEnable()).thenReturn(true);
        // mock 租户
        TenantEntity dbTenant = randomPojo(TenantEntity.class);
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        TenantContextHolder.setTenantId(dbTenant.getId());

        // 调用
        tenantService.handleTenantInfo(handler);
        // 断言
        verify(handler).handle(argThat(argument -> {
            assertPojoEquals(dbTenant, argument);
            return true;
        }));
    }

    @Test
    public void testHandleTenantMenu_disable() {
        // 准备参数
        TenantMenuHandler handler = mock(TenantMenuHandler.class);
        // mock 禁用
        when(tenantProperties.getEnable()).thenReturn(false);

        // 调用
        tenantService.handleTenantMenu(handler);
        // 断言
        verify(handler, never()).handle(any());
    }

    @Test // 系统租户的情况
    public void testHandleTenantMenu_system() {
        // 准备参数
        TenantMenuHandler handler = mock(TenantMenuHandler.class);
        // mock 未禁用
        when(tenantProperties.getEnable()).thenReturn(true);
        // mock 租户
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setPackageId(PACKAGE_ID_SYSTEM));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        TenantContextHolder.setTenantId(dbTenant.getId());
        // mock 菜单
        when(menuService.getMenuList()).thenReturn(Arrays.asList(randomPojo(MenuEntity.class, o -> o.setId(100L)),
                randomPojo(MenuEntity.class, o -> o.setId(101L))));

        // 调用
        tenantService.handleTenantMenu(handler);
        // 断言
        verify(handler).handle(asSet(100L, 101L));
    }

    @Test // 普通租户的情况
    public void testHandleTenantMenu_normal() {
        // 准备参数
        TenantMenuHandler handler = mock(TenantMenuHandler.class);
        // mock 未禁用
        when(tenantProperties.getEnable()).thenReturn(true);
        // mock 租户
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setPackageId(200L));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        TenantContextHolder.setTenantId(dbTenant.getId());
        // mock 菜单
        when(tenantPackageService.getTenantPackage(eq(200L))).thenReturn(randomPojo(TenantPackageEntity.class,
                o -> o.setMenuIds(asSet(100L, 101L))));

        // 调用
        tenantService.handleTenantMenu(handler);
        // 断言
        verify(handler).handle(asSet(100L, 101L));
    }
}
