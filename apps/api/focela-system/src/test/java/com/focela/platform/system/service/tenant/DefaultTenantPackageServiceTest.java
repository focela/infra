package com.focela.platform.system.service.tenant;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.tenant.dto.packages.TenantPackagePageRequest;
import com.focela.platform.system.controller.admin.tenant.dto.packages.TenantPackageSaveRequest;
import com.focela.platform.system.entity.tenant.TenantEntity;
import com.focela.platform.system.entity.tenant.TenantPackageEntity;
import com.focela.platform.system.repository.mapper.tenant.TenantPackageMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.framework.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.ErrorCodeConstants.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
* {@link DefaultTenantPackageService} 的单元测试类
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
        // 准备参数
        TenantPackageSaveRequest request = randomPojo(TenantPackageSaveRequest.class,
                o -> o.setStatus(randomCommonStatus()))
                .setId(null); // 防止 id 被赋值

        // 调用
        Long tenantPackageId = tenantPackageService.createTenantPackage(request);
        // 断言
        assertNotNull(tenantPackageId);
        // 校验记录的属性是否正确
        TenantPackageEntity tenantPackage = tenantPackageMapper.selectById(tenantPackageId);
        assertPojoEquals(request, tenantPackage, "id");
    }

    @Test
    public void testUpdateTenantPackage_success() {
        // mock 数据
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class,
                o -> o.setStatus(randomCommonStatus()));
        tenantPackageMapper.insert(dbTenantPackage);// @Sql: 先插入出一条存在的数据
        // 准备参数
        TenantPackageSaveRequest request = randomPojo(TenantPackageSaveRequest.class, o -> {
            o.setId(dbTenantPackage.getId()); // 设置更新的 ID
            o.setStatus(randomCommonStatus());
        });
        // mock 方法
        Long tenantId01 = randomLongId();
        Long tenantId02 = randomLongId();
        when(tenantService.getTenantListByPackageId(eq(request.getId()))).thenReturn(
                asList(randomPojo(TenantEntity.class, o -> o.setId(tenantId01)),
                        randomPojo(TenantEntity.class, o -> o.setId(tenantId02))));

        // 调用
        tenantPackageService.updateTenantPackage(request);
        // 校验是否更新正确
        TenantPackageEntity tenantPackage = tenantPackageMapper.selectById(request.getId()); // 获取最新的
        assertPojoEquals(request, tenantPackage);
        // 校验调用租户的菜单
        verify(tenantService).updateTenantRoleMenu(eq(tenantId01), eq(request.getMenuIds()));
        verify(tenantService).updateTenantRoleMenu(eq(tenantId02), eq(request.getMenuIds()));
    }

    @Test
    public void testUpdateTenantPackage_notExists() {
        // 准备参数
        TenantPackageSaveRequest request = randomPojo(TenantPackageSaveRequest.class);

        // 调用, 并断言异常
        assertServiceException(() -> tenantPackageService.updateTenantPackage(request), TENANT_PACKAGE_NOT_EXISTS);
    }

    @Test
    public void testDeleteTenantPackage_success() {
        // mock 数据
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class);
        tenantPackageMapper.insert(dbTenantPackage);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbTenantPackage.getId();
        // mock 租户未使用该套餐
        when(tenantService.getTenantCountByPackageId(eq(id))).thenReturn(0L);

        // 调用
        tenantPackageService.deleteTenantPackage(id);
       // 校验数据不存在了
       assertNull(tenantPackageMapper.selectById(id));
    }

    @Test
    public void testDeleteTenantPackage_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> tenantPackageService.deleteTenantPackage(id), TENANT_PACKAGE_NOT_EXISTS);
    }

    @Test
    public void testDeleteTenantPackage_used() {
        // mock 数据
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class);
        tenantPackageMapper.insert(dbTenantPackage);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbTenantPackage.getId();
        // mock 租户在使用该套餐
        when(tenantService.getTenantCountByPackageId(eq(id))).thenReturn(1L);

        // 调用, 并断言异常
        assertServiceException(() -> tenantPackageService.deleteTenantPackage(id), TENANT_PACKAGE_USED);
    }

    @Test
    public void testGetTenantPackagePage() {
       // mock 数据
       TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class, o -> { // 等会查询到
           o.setName("芋道源码");
           o.setStatus(CommonStatusEnum.ENABLE.getStatus());
           o.setRemark("源码解析");
           o.setCreateTime(buildTime(2022, 10, 10));
       });
       tenantPackageMapper.insert(dbTenantPackage);
       // 测试 name 不匹配
       tenantPackageMapper.insert(cloneIgnoreId(dbTenantPackage, o -> o.setName("源码")));
       // 测试 status 不匹配
       tenantPackageMapper.insert(cloneIgnoreId(dbTenantPackage, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
       // 测试 remark 不匹配
       tenantPackageMapper.insert(cloneIgnoreId(dbTenantPackage, o -> o.setRemark("解析")));
       // 测试 createTime 不匹配
       tenantPackageMapper.insert(cloneIgnoreId(dbTenantPackage, o -> o.setCreateTime(buildTime(2022, 11, 11))));
       // 准备参数
       TenantPackagePageRequest request = new TenantPackagePageRequest();
       request.setName("芋道");
       request.setStatus(CommonStatusEnum.ENABLE.getStatus());
       request.setRemark("源码");
       request.setCreateTime(buildBetweenTime(2022, 10, 9, 2022, 10, 11));

       // 调用
       PageResult<TenantPackageEntity> pageResult = tenantPackageService.getTenantPackagePage(request);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbTenantPackage, pageResult.getList().get(0));
    }

    @Test
    public void testValidTenantPackage_success() {
        // mock 数据
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class,
                o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        tenantPackageMapper.insert(dbTenantPackage);// @Sql: 先插入出一条存在的数据

        // 调用
        TenantPackageEntity result = tenantPackageService.validTenantPackage(dbTenantPackage.getId());
        // 断言
        assertPojoEquals(dbTenantPackage, result);
    }

    @Test
    public void testValidTenantPackage_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> tenantPackageService.validTenantPackage(id), TENANT_PACKAGE_NOT_EXISTS);
    }

    @Test
    public void testValidTenantPackage_disable() {
        // mock 数据
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class,
                o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        tenantPackageMapper.insert(dbTenantPackage);// @Sql: 先插入出一条存在的数据

        // 调用, 并断言异常
        assertServiceException(() -> tenantPackageService.validTenantPackage(dbTenantPackage.getId()),
                TENANT_PACKAGE_DISABLE, dbTenantPackage.getName());
    }

    @Test
    public void testGetTenantPackage() {
        // mock 数据
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class);
        tenantPackageMapper.insert(dbTenantPackage);// @Sql: 先插入出一条存在的数据

        // 调用
        TenantPackageEntity result = tenantPackageService.getTenantPackage(dbTenantPackage.getId());
        // 断言
        assertPojoEquals(result, dbTenantPackage);
    }

    @Test
    public void testGetTenantPackageListByStatus() {
        // mock 数据
        TenantPackageEntity dbTenantPackage = randomPojo(TenantPackageEntity.class,
                o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        tenantPackageMapper.insert(dbTenantPackage);
        // 测试 status 不匹配
        tenantPackageMapper.insert(cloneIgnoreId(dbTenantPackage,
                o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));

        // 调用
        List<TenantPackageEntity> list = tenantPackageService.getTenantPackageListByStatus(
                CommonStatusEnum.ENABLE.getStatus());
        assertEquals(1, list.size());
        assertPojoEquals(dbTenantPackage, list.get(0));
    }

}
