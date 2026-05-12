package com.focela.platform.module.infra.service.db;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.crypto.symmetric.AES;
import com.focela.platform.framework.mybatis.core.type.EncryptTypeHandler;
import com.focela.platform.framework.mybatis.core.utils.JdbcUtils;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.module.infra.controller.admin.db.dto.DataSourceConfigSaveRequest;
import com.focela.platform.module.infra.repository.entity.db.DataSourceConfigEntity;
import com.focela.platform.module.infra.repository.mapper.db.DataSourceConfigMapper;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.utils.RandomUtils.randomLongId;
import static com.focela.platform.framework.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.module.infra.enums.ErrorCodeConstants.DATA_SOURCE_CONFIG_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * {@link DefaultDataSourceConfigService} 的单元测试类
 */
@Import(DefaultDataSourceConfigService.class)
public class DefaultDataSourceConfigServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultDataSourceConfigService dataSourceConfigService;

    @Resource
    private DataSourceConfigMapper dataSourceConfigMapper;

    @MockitoBean
    private AES aes;

    @MockitoBean
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @BeforeEach
    public void setUp() {
        // mock 一个空实现的 StringEncryptor，避免 EncryptTypeHandler 报错
        ReflectUtil.setFieldValue(EncryptTypeHandler.class, "aes", aes);
        when(aes.encryptBase64(anyString())).then((Answer<String>) invocation -> invocation.getArgument(0));
        when(aes.decryptStr(anyString())).then((Answer<String>) invocation -> invocation.getArgument(0));

        // mock DynamicDataSourceProperties
        when(dynamicDataSourceProperties.getPrimary()).thenReturn("primary");
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setUrl("http://localhost:3306");
        dataSourceProperty.setUsername("yunai");
        dataSourceProperty.setPassword("tudou");
        when(dynamicDataSourceProperties.getDatasource()).thenReturn(MapUtil.of("primary", dataSourceProperty));
    }

    @Test
    public void testCreateDataSourceConfig_success() {
        try (MockedStatic<JdbcUtils> databaseUtilsMock = mockStatic(JdbcUtils.class)) {
            // 准备参数
            DataSourceConfigSaveRequest request = randomPojo(DataSourceConfigSaveRequest.class)
                    .setId(null); // 避免 id 被设置
            // mock 方法
            databaseUtilsMock.when(() -> JdbcUtils.isConnectionOK(eq(request.getUrl()),
                    eq(request.getUsername()), eq(request.getPassword()))).thenReturn(true);

            // 调用
            Long dataSourceConfigId = dataSourceConfigService.createDataSourceConfig(request);
            // 断言
            assertNotNull(dataSourceConfigId);
            // 校验记录的属性是否正确
            DataSourceConfigEntity dataSourceConfig = dataSourceConfigMapper.selectById(dataSourceConfigId);
            assertPojoEquals(request, dataSourceConfig, "id");
        }
    }

    @Test
    public void testUpdateDataSourceConfig_success() {
        try (MockedStatic<JdbcUtils> databaseUtilsMock = mockStatic(JdbcUtils.class)) {
            // mock 数据
            DataSourceConfigEntity dbDataSourceConfig = randomPojo(DataSourceConfigEntity.class);
            dataSourceConfigMapper.insert(dbDataSourceConfig);// @Sql: 先插入出一条存在的数据
            // 准备参数
            DataSourceConfigSaveRequest request = randomPojo(DataSourceConfigSaveRequest.class, o -> {
                o.setId(dbDataSourceConfig.getId()); // 设置更新的 ID
            });
            // mock 方法
            databaseUtilsMock.when(() -> JdbcUtils.isConnectionOK(eq(request.getUrl()),
                    eq(request.getUsername()), eq(request.getPassword()))).thenReturn(true);

            // 调用
            dataSourceConfigService.updateDataSourceConfig(request);
            // 校验是否更新正确
            DataSourceConfigEntity dataSourceConfig = dataSourceConfigMapper.selectById(request.getId()); // 获取最新的
            assertPojoEquals(request, dataSourceConfig);
        }
    }

    @Test
    public void testUpdateDataSourceConfig_notExists() {
        // 准备参数
        DataSourceConfigSaveRequest request = randomPojo(DataSourceConfigSaveRequest.class);

        // 调用, 并断言异常
        assertServiceException(() -> dataSourceConfigService.updateDataSourceConfig(request), DATA_SOURCE_CONFIG_NOT_EXISTS);
    }

    @Test
    public void testDeleteDataSourceConfig_success() {
        // mock 数据
        DataSourceConfigEntity dbDataSourceConfig = randomPojo(DataSourceConfigEntity.class);
        dataSourceConfigMapper.insert(dbDataSourceConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbDataSourceConfig.getId();

        // 调用
        dataSourceConfigService.deleteDataSourceConfig(id);
        // 校验数据不存在了
        assertNull(dataSourceConfigMapper.selectById(id));
    }

    @Test
    public void testDeleteDataSourceConfig_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> dataSourceConfigService.deleteDataSourceConfig(id), DATA_SOURCE_CONFIG_NOT_EXISTS);
    }

    @Test // 测试使用 password 查询，可以查询到数据
    public void testSelectPassword() {
        // mock 数据
        DataSourceConfigEntity dbDataSourceConfig = randomPojo(DataSourceConfigEntity.class);
        dataSourceConfigMapper.insert(dbDataSourceConfig);// @Sql: 先插入出一条存在的数据

        // 调用
        DataSourceConfigEntity result = dataSourceConfigMapper.selectOne(DataSourceConfigEntity::getPassword,
                EncryptTypeHandler.encrypt(dbDataSourceConfig.getPassword()));
        assertPojoEquals(dbDataSourceConfig, result);
    }

    @Test
    public void testGetDataSourceConfig_master() {
        // 准备参数
        Long id = 0L;
        // mock 方法

        // 调用
        DataSourceConfigEntity dataSourceConfig = dataSourceConfigService.getDataSourceConfig(id);
        // 断言
        assertEquals(id, dataSourceConfig.getId());
        assertEquals("primary", dataSourceConfig.getName());
        assertEquals("http://localhost:3306", dataSourceConfig.getUrl());
        assertEquals("yunai", dataSourceConfig.getUsername());
        assertEquals("tudou", dataSourceConfig.getPassword());
    }

    @Test
    public void testGetDataSourceConfig_normal() {
        // mock 数据
        DataSourceConfigEntity dbDataSourceConfig = randomPojo(DataSourceConfigEntity.class);
        dataSourceConfigMapper.insert(dbDataSourceConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbDataSourceConfig.getId();

        // 调用
        DataSourceConfigEntity dataSourceConfig = dataSourceConfigService.getDataSourceConfig(id);
        // 断言
        assertPojoEquals(dbDataSourceConfig, dataSourceConfig);
    }

    @Test
    public void testGetDataSourceConfigList() {
        // mock 数据
        DataSourceConfigEntity dbDataSourceConfig = randomPojo(DataSourceConfigEntity.class);
        dataSourceConfigMapper.insert(dbDataSourceConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数

        // 调用
        List<DataSourceConfigEntity> dataSourceConfigList = dataSourceConfigService.getDataSourceConfigList();
        // 断言
        assertEquals(2, dataSourceConfigList.size());
        // master
        assertEquals(0L, dataSourceConfigList.get(0).getId());
        assertEquals("primary", dataSourceConfigList.get(0).getName());
        assertEquals("http://localhost:3306", dataSourceConfigList.get(0).getUrl());
        assertEquals("yunai", dataSourceConfigList.get(0).getUsername());
        assertEquals("tudou", dataSourceConfigList.get(0).getPassword());
        // normal
        assertPojoEquals(dbDataSourceConfig, dataSourceConfigList.get(1));
    }

}
