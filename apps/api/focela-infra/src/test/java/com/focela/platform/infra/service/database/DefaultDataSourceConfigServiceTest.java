package com.focela.platform.infra.service.database;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.crypto.symmetric.AES;
import com.focela.platform.mybatis.core.type.EncryptTypeHandler;
import com.focela.platform.mybatis.core.utils.JdbcUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.infra.controller.admin.database.request.DataSourceConfigSaveRequest;
import com.focela.platform.infra.domain.entity.database.DataSourceConfigEntity;
import com.focela.platform.infra.repository.mapper.database.DataSourceConfigMapper;
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

import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.randomLongId;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.DATA_SOURCE_CONFIG_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Unit test class for {@link DefaultDataSourceConfigService}
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
        // Mock an empty StringEncryptor to avoid EncryptTypeHandler errors
        ReflectUtil.setFieldValue(EncryptTypeHandler.class, "aes", aes);
        when(aes.encryptBase64(anyString())).then((Answer<String>) invocation -> invocation.getArgument(0));
        when(aes.decryptStr(anyString())).then((Answer<String>) invocation -> invocation.getArgument(0));

        // mock DynamicDataSourceProperties
        when(dynamicDataSourceProperties.getPrimary()).thenReturn("primary");
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setUrl("http://localhost:3306");
        dataSourceProperty.setUsername("focela");
        dataSourceProperty.setPassword("password");
        when(dynamicDataSourceProperties.getDatasource()).thenReturn(MapUtil.of("primary", dataSourceProperty));
    }

    @Test
    public void testCreateDataSourceConfig_success() {
        try (MockedStatic<JdbcUtils> databaseUtilsMock = mockStatic(JdbcUtils.class)) {
            // Prepare parameters
            DataSourceConfigSaveRequest request = randomPojo(DataSourceConfigSaveRequest.class)
                    .setId(null); // avoid id being set
            // mock the method
            databaseUtilsMock.when(() -> JdbcUtils.isConnectionOK(eq(request.getUrl()),
                    eq(request.getUsername()), eq(request.getPassword()))).thenReturn(true);

            // Invoke
            Long dataSourceConfigId = dataSourceConfigService.createDataSourceConfig(request);
            // Assert
            assertNotNull(dataSourceConfigId);
            // Verify record properties are correct
            DataSourceConfigEntity dataSourceConfig = dataSourceConfigMapper.selectById(dataSourceConfigId);
            assertPojoEquals(request, dataSourceConfig, "id");
        }
    }

    @Test
    public void testUpdateDataSourceConfig_success() {
        try (MockedStatic<JdbcUtils> databaseUtilsMock = mockStatic(JdbcUtils.class)) {
            // mock data
            DataSourceConfigEntity dbDataSourceConfig = randomPojo(DataSourceConfigEntity.class);
            dataSourceConfigMapper.insert(dbDataSourceConfig);// @Sql: first insert an existing record
            // Prepare parameters
            DataSourceConfigSaveRequest request = randomPojo(DataSourceConfigSaveRequest.class, o -> {
                o.setId(dbDataSourceConfig.getId()); // set the ID to update
            });
            // mock the method
            databaseUtilsMock.when(() -> JdbcUtils.isConnectionOK(eq(request.getUrl()),
                    eq(request.getUsername()), eq(request.getPassword()))).thenReturn(true);

            // Invoke
            dataSourceConfigService.updateDataSourceConfig(request);
            // Verify update is correct
            DataSourceConfigEntity dataSourceConfig = dataSourceConfigMapper.selectById(request.getId()); // get the latest
            assertPojoEquals(request, dataSourceConfig);
        }
    }

    @Test
    public void testUpdateDataSourceConfig_notExists() {
        // Prepare parameters
        DataSourceConfigSaveRequest request = randomPojo(DataSourceConfigSaveRequest.class);

        // Invoke and verify exception
        assertServiceException(() -> dataSourceConfigService.updateDataSourceConfig(request), DATA_SOURCE_CONFIG_NOT_EXISTS);
    }

    @Test
    public void testDeleteDataSourceConfig_success() {
        // mock data
        DataSourceConfigEntity dbDataSourceConfig = randomPojo(DataSourceConfigEntity.class);
        dataSourceConfigMapper.insert(dbDataSourceConfig);// @Sql: first insert an existing record
        // Prepare parameters
        Long id = dbDataSourceConfig.getId();

        // Invoke
        dataSourceConfigService.deleteDataSourceConfig(id);
        // Verify data no longer exists
        assertNull(dataSourceConfigMapper.selectById(id));
    }

    @Test
    public void testDeleteDataSourceConfig_notExists() {
        // Prepare parameters
        Long id = randomLongId();

        // Invoke and verify exception
        assertServiceException(() -> dataSourceConfigService.deleteDataSourceConfig(id), DATA_SOURCE_CONFIG_NOT_EXISTS);
    }

    @Test // Test querying by password — record can be queried
    public void testSelectPassword() {
        // mock data
        DataSourceConfigEntity dbDataSourceConfig = randomPojo(DataSourceConfigEntity.class);
        dataSourceConfigMapper.insert(dbDataSourceConfig);// @Sql: first insert an existing record

        // Invoke
        DataSourceConfigEntity result = dataSourceConfigMapper.selectOne(DataSourceConfigEntity::getPassword,
                EncryptTypeHandler.encrypt(dbDataSourceConfig.getPassword()));
        assertPojoEquals(dbDataSourceConfig, result);
    }

    @Test
    public void testGetDataSourceConfig_master() {
        // Prepare parameters
        Long id = 0L;
        // mock the method

        // Invoke
        DataSourceConfigEntity dataSourceConfig = dataSourceConfigService.getDataSourceConfig(id);
        // Assert
        assertEquals(id, dataSourceConfig.getId());
        assertEquals("primary", dataSourceConfig.getName());
        assertEquals("http://localhost:3306", dataSourceConfig.getUrl());
        assertEquals("focela", dataSourceConfig.getUsername());
        assertEquals("password", dataSourceConfig.getPassword());
    }

    @Test
    public void testGetDataSourceConfig_normal() {
        // mock data
        DataSourceConfigEntity dbDataSourceConfig = randomPojo(DataSourceConfigEntity.class);
        dataSourceConfigMapper.insert(dbDataSourceConfig);// @Sql: first insert an existing record
        // Prepare parameters
        Long id = dbDataSourceConfig.getId();

        // Invoke
        DataSourceConfigEntity dataSourceConfig = dataSourceConfigService.getDataSourceConfig(id);
        // Assert
        assertPojoEquals(dbDataSourceConfig, dataSourceConfig);
    }

    @Test
    public void testGetDataSourceConfigList() {
        // mock data
        DataSourceConfigEntity dbDataSourceConfig = randomPojo(DataSourceConfigEntity.class);
        dataSourceConfigMapper.insert(dbDataSourceConfig);// @Sql: first insert an existing record
        // Prepare parameters

        // Invoke
        List<DataSourceConfigEntity> dataSourceConfigList = dataSourceConfigService.getDataSourceConfigList();
        // Assert
        assertEquals(2, dataSourceConfigList.size());
        // master
        assertEquals(0L, dataSourceConfigList.get(0).getId());
        assertEquals("primary", dataSourceConfigList.get(0).getName());
        assertEquals("http://localhost:3306", dataSourceConfigList.get(0).getUrl());
        assertEquals("focela", dataSourceConfigList.get(0).getUsername());
        assertEquals("password", dataSourceConfigList.get(0).getPassword());
        // normal
        assertPojoEquals(dbDataSourceConfig, dataSourceConfigList.get(1));
    }

}
