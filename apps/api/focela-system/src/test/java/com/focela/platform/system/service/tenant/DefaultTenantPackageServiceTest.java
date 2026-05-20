package com.focela.platform.system.service.tenant;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.tenant.request.tenantpackage.TenantPackagePageRequest;
import com.focela.platform.system.controller.admin.tenant.request.tenantpackage.TenantPackageSaveRequest;
import com.focela.platform.system.domain.entity.tenant.TenantEntity;
import com.focela.platform.system.domain.entity.tenant.TenantPackageEntity;
import com.focela.platform.system.repository.mapper.tenant.TenantPackageMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
* {@link DefaultTenantPackageService}  unit test class
*/
@Import(DefaultTenantPackageService.class)
public class DefaultTenantPackageServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultTenantPackageService tenantPackageService;

    @Resource
    private TenantPackageMapper tenantPackageMapper;

    @MockitoBean
    private TenantService tenantService;

    @Test
    public void testCreateTenantPackage_success() {
        // prepare parameters
        TenantPackageSaveRequest request = randomPojo(TenantPackageSaveRequest.class,
                o -> o.setStatus(randomCommonStatus()))
                .setId(null); // prevent id from being assigned

        // invoke
        Long tenantPackageId = tenantPackageService.createTenantPackage(request);
        // assert
        assertNotNull(tenantPackageId);
        // verify record properties are correct
        TenantPackageEntity tenantPackage = tenantPackageMapper.selectById(tenantPackageId);
        assertPojoEquals(request, tenantPackage, "id");
    }

    @Test
    public void testUpdateTenantPackage_success() {
        // mock data
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class,
                o -> o.setStatus(randomCommonStatus()));
        tenantPackageMapper.insert(dbTenantPackage);// @Sql: first insert an existing record
        // prepare parameters
        TenantPackageSaveRequest request = randomPojo(TenantPackageSaveRequest.class, o -> {
            o.setId(dbTenantPackage.getId()); // set updated ID
            o.setStatus(randomCommonStatus());
        });
        // mock the method
        Long tenantId01 = randomLongId();
        Long tenantId02 = randomLongId();
        when(tenantService.getTenantListByPackageId(eq(request.getId()))).thenReturn(
                asList(randomPojo(TenantEntity.class, o -> o.setId(tenantId01)),
                        randomPojo(TenantEntity.class, o -> o.setId(tenantId02))));

        // invoke
        tenantPackageService.updateTenantPackage(request);
        // verify update is correct
        TenantPackageEntity tenantPackage = tenantPackageMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, tenantPackage);
        // verify call to tenant menus
        verify(tenantService).updateTenantRoleMenu(eq(tenantId01), eq(request.getMenuIds()));
        verify(tenantService).updateTenantRoleMenu(eq(tenantId02), eq(request.getMenuIds()));
    }

    @Test
    public void testUpdateTenantPackage_notExists() {
        // prepare parameters
        TenantPackageSaveRequest request = randomPojo(TenantPackageSaveRequest.class);

        // invoke and assert exception
        assertServiceException(() -> tenantPackageService.updateTenantPackage(request), TENANT_PACKAGE_NOT_EXISTS);
    }

    @Test
    public void testDeleteTenantPackage_success() {
        // mock data
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class);
        tenantPackageMapper.insert(dbTenantPackage);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbTenantPackage.getId();
        // mock tenant is not using this package
        when(tenantService.getTenantCountByPackageId(eq(id))).thenReturn(0L);

        // invoke
        tenantPackageService.deleteTenantPackage(id);
       // verify data no longer exists
       assertNull(tenantPackageMapper.selectById(id));
    }

    @Test
    public void testDeleteTenantPackage_notExists() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> tenantPackageService.deleteTenantPackage(id), TENANT_PACKAGE_NOT_EXISTS);
    }

    @Test
    public void testDeleteTenantPackage_used() {
        // mock data
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class);
        tenantPackageMapper.insert(dbTenantPackage);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbTenantPackage.getId();
        // mock tenant is using this package
        when(tenantService.getTenantCountByPackageId(eq(id))).thenReturn(1L);

        // invoke and assert exception
        assertServiceException(() -> tenantPackageService.deleteTenantPackage(id), TENANT_PACKAGE_USED);
    }

    @Test
    public void testGetTenantPackagePage() {
       // mock data
       TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class, o -> { // will be queried later
           o.setName("Focelasource");
           o.setStatus(CommonStatusEnum.ENABLE.getStatus());
           o.setRemark("source analysis");
           o.setCreateTime(buildTime(2022, 10, 10));
       });
       tenantPackageMapper.insert(dbTenantPackage);
       // test name mismatch
       tenantPackageMapper.insert(cloneIgnoreId(dbTenantPackage, o -> o.setName("source")));
       // test status mismatch
       tenantPackageMapper.insert(cloneIgnoreId(dbTenantPackage, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
       // test remark mismatch
       tenantPackageMapper.insert(cloneIgnoreId(dbTenantPackage, o -> o.setRemark("Parse")));
       // test createTime mismatch
       tenantPackageMapper.insert(cloneIgnoreId(dbTenantPackage, o -> o.setCreateTime(buildTime(2022, 11, 11))));
       // prepare parameters
       TenantPackagePageRequest request = new TenantPackagePageRequest();
       request.setName("Focela");
       request.setStatus(CommonStatusEnum.ENABLE.getStatus());
       request.setRemark("source");
       request.setCreateTime(buildBetweenTime(2022, 10, 9, 2022, 10, 11));

       // invoke
       PageResult<TenantPackageEntity> pageResult = tenantPackageService.getTenantPackagePage(request);
       // assert
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbTenantPackage, pageResult.getList().get(0));
    }

    @Test
    public void testValidTenantPackage_success() {
        // mock data
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class,
                o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        tenantPackageMapper.insert(dbTenantPackage);// @Sql: first insert an existing record

        // invoke
        TenantPackageEntity result = tenantPackageService.validateTenantPackage(dbTenantPackage.getId());
        // assert
        assertPojoEquals(dbTenantPackage, result);
    }

    @Test
    public void testValidTenantPackage_notExists() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> tenantPackageService.validateTenantPackage(id), TENANT_PACKAGE_NOT_EXISTS);
    }

    @Test
    public void testValidTenantPackage_disable() {
        // mock data
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class,
                o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        tenantPackageMapper.insert(dbTenantPackage);// @Sql: first insert an existing record

        // invoke and assert exception
        assertServiceException(() -> tenantPackageService.validateTenantPackage(dbTenantPackage.getId()),
                TENANT_PACKAGE_DISABLE, dbTenantPackage.getName());
    }

    @Test
    public void testGetTenantPackage() {
        // mock data
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class);
        tenantPackageMapper.insert(dbTenantPackage);// @Sql: first insert an existing record

        // invoke
        TenantPackageEntity result = tenantPackageService.getTenantPackage(dbTenantPackage.getId());
        // assert
        assertPojoEquals(result, dbTenantPackage);
    }

    @Test
    public void testGetTenantPackageListByStatus() {
        // mock data
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class,
                o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        tenantPackageMapper.insert(dbTenantPackage);
        // test status mismatch
        tenantPackageMapper.insert(cloneIgnoreId(dbTenantPackage,
                o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));

        // invoke
        List<TenantPackageEntity> list = tenantPackageService.getTenantPackageListByStatus(
                CommonStatusEnum.ENABLE.getStatus());
        assertEquals(1, list.size());
        assertPojoEquals(dbTenantPackage, list.get(0));
    }

}
