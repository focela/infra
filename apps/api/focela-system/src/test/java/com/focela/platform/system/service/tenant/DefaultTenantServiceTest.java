package com.focela.platform.system.service.tenant;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.tenant.config.TenantProperties;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.tenant.request.TenantPageRequest;
import com.focela.platform.system.controller.admin.tenant.request.TenantSaveRequest;
import com.focela.platform.system.domain.entity.permission.MenuEntity;
import com.focela.platform.system.domain.entity.permission.RoleEntity;
import com.focela.platform.system.domain.entity.tenant.TenantEntity;
import com.focela.platform.system.domain.entity.tenant.TenantPackageEntity;
import com.focela.platform.system.repository.mapper.tenant.TenantMapper;
import com.focela.platform.system.enums.permission.RoleCodeEnum;
import com.focela.platform.system.enums.permission.RoleTypeEnum;
import com.focela.platform.system.service.permission.MenuService;
import com.focela.platform.system.service.permission.PermissionService;
import com.focela.platform.system.service.permission.RoleService;
import com.focela.platform.system.service.tenant.handler.TenantInfoHandler;
import com.focela.platform.system.service.tenant.handler.TenantMenuHandler;
import com.focela.platform.system.service.user.UserService;
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

import static com.focela.platform.common.utils.collection.SetUtils.asSet;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.domain.entity.tenant.TenantEntity.PACKAGE_ID_SYSTEM;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link DefaultTenantService}  unit test class
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
        // clear tenant context
        TenantContextHolder.clear();
    }

    @Test
    public void testGetTenantIdList() {
        // mock data
        TenantEntity tenant = randomPojo(TenantEntity.class, o -> o.setId(1L));
        tenantMapper.insert(tenant);

        // invoke, and assert business exception
        List<Long> result = tenantService.getTenantIdList();
        assertEquals(Collections.singletonList(1L), result);
    }

    @Test
    public void testValidTenant_notExists() {
        assertServiceException(() -> tenantService.validateTenant(randomLongId()), TENANT_NOT_FOUND);
    }

    @Test
    public void testValidTenant_disable() {
        // mock data
        TenantEntity tenant = randomPojo(TenantEntity.class, o -> o.setId(1L).setStatus(CommonStatusEnum.DISABLE.getStatus()));
        tenantMapper.insert(tenant);

        // invoke, and assert business exception
        assertServiceException(() -> tenantService.validateTenant(1L), TENANT_DISABLED, tenant.getName());
    }

    @Test
    public void testValidTenant_expired() {
        // mock data
        TenantEntity tenant = randomPojo(TenantEntity.class, o -> o.setId(1L).setStatus(CommonStatusEnum.ENABLE.getStatus())
                .setExpireTime(buildTime(2020, 2, 2)));
        tenantMapper.insert(tenant);

        // invoke, and assert business exception
        assertServiceException(() -> tenantService.validateTenant(1L), TENANT_EXPIRE, tenant.getName());
    }

    @Test
    public void testValidTenant_success() {
        // mock data
        TenantEntity tenant = randomPojo(TenantEntity.class, o -> o.setId(1L).setStatus(CommonStatusEnum.ENABLE.getStatus())
                .setExpireTime(LocalDateTime.now().plusDays(1)));
        tenantMapper.insert(tenant);

        // invoke, and assert business exception
        tenantService.validateTenant(1L);
    }

    @Test
    public void testCreateTenant() {
        // mock package 100L
        TenantPackageEntity tenantPackage = randomPojo(TenantPackageEntity.class, o -> o.setId(100L));
        when(tenantPackageService.validateTenantPackage(eq(100L))).thenReturn(tenantPackage);
        // mock role 200L
        when(roleService.createRole(argThat(role -> {
            assertEquals(RoleCodeEnum.TENANT_ADMIN.getName(), role.getName());
            assertEquals(RoleCodeEnum.TENANT_ADMIN.getCode(), role.getCode());
            assertEquals(0, role.getSort());
            assertEquals("system auto-generated", role.getRemark());
            return true;
        }), eq(RoleTypeEnum.SYSTEM.getType()))).thenReturn(200L);
        // mock user 300L
        when(userService.createUser(argThat(user -> {
            assertEquals("focela_sample", user.getUsername());
            assertEquals("focela_secret", user.getPassword());
            assertEquals("Focela", user.getNickname());
            assertEquals("15601691300", user.getMobile());
            return true;
        }))).thenReturn(300L);

        // prepare parameters
        TenantSaveRequest request = randomPojo(TenantSaveRequest.class, o -> {
            o.setContactName("Focela");
            o.setContactMobile("15601691300");
            o.setPackageId(100L);
            o.setStatus(randomCommonStatus());
            o.setWebsites(singletonList("https://www.example.com"));
            o.setUsername("focela_sample");
            o.setPassword("focela_secret");
        }).setId(null); // set to null, for later verification

        // invoke
        Long tenantId = tenantService.createTenant(request);
        // assert
        assertNotNull(tenantId);
        // verify record properties are correct
        TenantEntity tenant = tenantMapper.selectById(tenantId);
        assertPojoEquals(request, tenant, "id");
        assertEquals(300L, tenant.getContactUserId());
        // verify assign permission
        verify(permissionService).assignRoleMenu(eq(200L), same(tenantPackage.getMenuIds()));
        // verify assign role
        verify(permissionService).assignUserRole(eq(300L), eq(singleton(200L)));
    }

    @Test
    public void testUpdateTenant_success() {
        // mock data
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setStatus(randomCommonStatus()));
        tenantMapper.insert(dbTenant);// @Sql: first insert an existing record
        // prepare parameters
        TenantSaveRequest request = randomPojo(TenantSaveRequest.class, o -> {
            o.setId(dbTenant.getId()); // set updated ID
            o.setStatus(randomCommonStatus());
            o.setWebsites(singletonList(randomString()));
        });

        // mock package
        TenantPackageEntity tenantPackage = randomPojo(TenantPackageEntity.class,
                o -> o.setMenuIds(asSet(200L, 201L)));
        when(tenantPackageService.validateTenantPackage(eq(request.getPackageId()))).thenReturn(tenantPackage);
        // mock all roles
        RoleEntity role100 = randomPojo(RoleEntity.class, o -> o.setId(100L).setCode(RoleCodeEnum.TENANT_ADMIN.getCode()));
        role100.setTenantId(dbTenant.getId());
        RoleEntity role101 = randomPojo(RoleEntity.class, o -> o.setId(101L));
        role101.setTenantId(dbTenant.getId());
        when(roleService.getRoleList()).thenReturn(asList(role100, role101));
        // mock permissions per role
        when(permissionService.getRoleMenuListByRoleId(eq(101L))).thenReturn(asSet(201L, 202L));

        // invoke
        tenantService.updateTenant(request);
        // verify update is correct
        TenantEntity tenant = tenantMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, tenant);
        // verify set role permissions
        verify(permissionService).assignRoleMenu(eq(100L), eq(asSet(200L, 201L)));
        verify(permissionService).assignRoleMenu(eq(101L), eq(asSet(201L)));
    }

    @Test
    public void testUpdateTenant_notExists() {
        // prepare parameters
        TenantSaveRequest request = randomPojo(TenantSaveRequest.class);

        // invoke and assert exception
        assertServiceException(() -> tenantService.updateTenant(request), TENANT_NOT_FOUND);
    }

    @Test
    public void testUpdateTenant_system() {
        // mock data
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setPackageId(PACKAGE_ID_SYSTEM));
        tenantMapper.insert(dbTenant);// @Sql: first insert an existing record
        // prepare parameters
        TenantSaveRequest request = randomPojo(TenantSaveRequest.class, o -> {
            o.setId(dbTenant.getId()); // set updated ID
        });

        // invoke, verify business exception
        assertServiceException(() -> tenantService.updateTenant(request), TENANT_SYSTEM_UPDATE_NOT_ALLOWED);
    }

    @Test
    public void testDeleteTenant_success() {
        // mock data
        TenantEntity dbTenant = randomPojo(TenantEntity.class,
                o -> o.setStatus(randomCommonStatus()));
        tenantMapper.insert(dbTenant);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbTenant.getId();

        // invoke
        tenantService.deleteTenant(id);
        // verify data no longer exists
        assertNull(tenantMapper.selectById(id));
    }

    @Test
    public void testDeleteTenant_notExists() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> tenantService.deleteTenant(id), TENANT_NOT_FOUND);
    }

    @Test
    public void testDeleteTenant_system() {
        // mock data
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setPackageId(PACKAGE_ID_SYSTEM));
        tenantMapper.insert(dbTenant);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbTenant.getId();

        // invoke and assert exception
        assertServiceException(() -> tenantService.deleteTenant(id), TENANT_SYSTEM_UPDATE_NOT_ALLOWED);
    }

    @Test
    public void testGetTenant() {
        // mock data
        TenantEntity dbTenant = randomPojo(TenantEntity.class);
        tenantMapper.insert(dbTenant);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbTenant.getId();

        // invoke
        TenantEntity result = tenantService.getTenant(id);
        // verify exists
        assertPojoEquals(result, dbTenant);
    }

    @Test
    public void testGetTenantPage() {
        // mock data
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> { // will be queried later
            o.setName("Focelasource");
            o.setContactName("Focela");
            o.setContactMobile("15601691300");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCreateTime(buildTime(2020, 12, 12));
        });
        tenantMapper.insert(dbTenant);
        // test name mismatch
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setName(randomString())));
        // test contactName mismatch
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setContactName(randomString())));
        // test contactMobile mismatch
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setContactMobile(randomString())));
        // test status mismatch
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // test createTime mismatch
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setCreateTime(buildTime(2021, 12, 12))));
        // prepare parameters
        TenantPageRequest request = new TenantPageRequest();
        request.setName("Focela");
        request.setContactName("Focela");
        request.setContactMobile("1560");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());
        request.setCreateTime(buildBetweenTime(2020, 12, 1, 2020, 12, 24));

        // invoke
        PageResult<TenantEntity> pageResult = tenantService.getTenantPage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbTenant, pageResult.getList().get(0));
    }

    @Test
    public void testGetTenantByName() {
        // mock data
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setName("Focela"));
        tenantMapper.insert(dbTenant);// @Sql: first insert an existing record

        // invoke
        TenantEntity result = tenantService.getTenantByName("Focela");
        // verify exists
        assertPojoEquals(result, dbTenant);
    }

    @Test
    @Disabled // H2 find_in_set function is not supported
    public void testGetTenantByWebsite() {
        // mock data
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setWebsites(singletonList("https://www.example.com")));
        tenantMapper.insert(dbTenant);// @Sql: first insert an existing record

        // invoke
        TenantEntity result = tenantService.getTenantByWebsite("https://www.example.com");
        // verify exists
        assertPojoEquals(result, dbTenant);
    }

    @Test
    public void testGetTenantListByPackageId() {
        // mock data
        TenantEntity dbTenant1 = randomPojo(TenantEntity.class, o -> o.setPackageId(1L));
        tenantMapper.insert(dbTenant1);// @Sql: first insert an existing record
        TenantEntity dbTenant2 = randomPojo(TenantEntity.class, o -> o.setPackageId(2L));
        tenantMapper.insert(dbTenant2);// @Sql: first insert an existing record

        // invoke
        List<TenantEntity> result = tenantService.getTenantListByPackageId(1L);
        assertEquals(1, result.size());
        assertPojoEquals(dbTenant1, result.get(0));
    }

    @Test
    public void testGetTenantCountByPackageId() {
        // mock data
        TenantEntity dbTenant1 = randomPojo(TenantEntity.class, o -> o.setPackageId(1L));
        tenantMapper.insert(dbTenant1);// @Sql: first insert an existing record
        TenantEntity dbTenant2 = randomPojo(TenantEntity.class, o -> o.setPackageId(2L));
        tenantMapper.insert(dbTenant2);// @Sql: first insert an existing record

        // invoke
        Long count = tenantService.getTenantCountByPackageId(1L);
        assertEquals(1, count);
    }

    @Test
    public void testHandleTenantInfo_disable() {
        // prepare parameters
        TenantInfoHandler handler = mock(TenantInfoHandler.class);
        // mock disabled
        when(tenantProperties.getEnable()).thenReturn(false);

        // invoke
        tenantService.handleTenantInfo(handler);
        // assert
        verify(handler, never()).handle(any());
    }

    @Test
    public void testHandleTenantInfo_success() {
        // prepare parameters
        TenantInfoHandler handler = mock(TenantInfoHandler.class);
        // mock not disabled
        when(tenantProperties.getEnable()).thenReturn(true);
        // mock tenant
        TenantEntity dbTenant = randomPojo(TenantEntity.class);
        tenantMapper.insert(dbTenant);// @Sql: first insert an existing record
        TenantContextHolder.setTenantId(dbTenant.getId());

        // invoke
        tenantService.handleTenantInfo(handler);
        // assert
        verify(handler).handle(argThat(argument -> {
            assertPojoEquals(dbTenant, argument);
            return true;
        }));
    }

    @Test
    public void testHandleTenantMenu_disable() {
        // prepare parameters
        TenantMenuHandler handler = mock(TenantMenuHandler.class);
        // mock disabled
        when(tenantProperties.getEnable()).thenReturn(false);

        // invoke
        tenantService.handleTenantMenu(handler);
        // assert
        verify(handler, never()).handle(any());
    }

    @Test // system tenant case
    public void testHandleTenantMenu_system() {
        // prepare parameters
        TenantMenuHandler handler = mock(TenantMenuHandler.class);
        // mock not disabled
        when(tenantProperties.getEnable()).thenReturn(true);
        // mock tenant
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setPackageId(PACKAGE_ID_SYSTEM));
        tenantMapper.insert(dbTenant);// @Sql: first insert an existing record
        TenantContextHolder.setTenantId(dbTenant.getId());
        // mock menu
        when(menuService.getMenuList()).thenReturn(Arrays.asList(randomPojo(MenuEntity.class, o -> o.setId(100L)),
                randomPojo(MenuEntity.class, o -> o.setId(101L))));

        // invoke
        tenantService.handleTenantMenu(handler);
        // assert
        verify(handler).handle(asSet(100L, 101L));
    }

    @Test // regular tenant case
    public void testHandleTenantMenu_normal() {
        // prepare parameters
        TenantMenuHandler handler = mock(TenantMenuHandler.class);
        // mock not disabled
        when(tenantProperties.getEnable()).thenReturn(true);
        // mock tenant
        TenantEntity dbTenant = randomPojo(TenantEntity.class, o -> o.setPackageId(200L));
        tenantMapper.insert(dbTenant);// @Sql: first insert an existing record
        TenantContextHolder.setTenantId(dbTenant.getId());
        // mock menu
        when(tenantPackageService.getTenantPackage(eq(200L))).thenReturn(randomPojo(TenantPackageEntity.class,
                o -> o.setMenuIds(asSet(100L, 101L))));

        // invoke
        tenantService.handleTenantMenu(handler);
        // assert
        verify(handler).handle(asSet(100L, 101L));
    }
}
