package com.focela.platform.infra.service.file;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.infra.controller.admin.file.request.config.FileConfigPageRequest;
import com.focela.platform.infra.controller.admin.file.request.config.FileConfigSaveRequest;
import com.focela.platform.infra.domain.entity.file.FileConfigEntity;
import com.focela.platform.infra.repository.mapper.file.FileConfigMapper;
import com.focela.platform.infra.config.file.client.FileClient;
import com.focela.platform.infra.config.file.client.FileClientConfig;
import com.focela.platform.infra.config.file.client.FileClientFactory;
import com.focela.platform.infra.config.file.client.local.LocalFileClient;
import com.focela.platform.infra.config.file.client.local.LocalFileClientConfig;
import com.focela.platform.infra.config.file.enums.FileStorageEnum;
import jakarta.annotation.Resource;
import jakarta.validation.Validator;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.randomLongId;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.FILE_CONFIG_DELETE_FAIL_MASTER;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.FILE_CONFIG_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test class for {@link DefaultFileConfigService}
 */
@Import(DefaultFileConfigService.class)
public class DefaultFileConfigServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultFileConfigService fileConfigService;

    @Resource
    private FileConfigMapper fileConfigMapper;

    @MockitoBean
    private Validator validator;
    @MockitoBean
    private FileClientFactory fileClientFactory;

    @Test
    public void testCreateFileConfig_success() {
        // Prepare parameters
        Map<String, Object> config = MapUtil.<String, Object>builder().put("basePath", "/focela")
                .put("domain", "https://www.example.com").build();
        FileConfigSaveRequest request = randomPojo(FileConfigSaveRequest.class,
                o -> o.setStorage(FileStorageEnum.LOCAL.getStorage()).setConfig(config))
                .setId(null); // avoid id being set

        // Invoke
        Long fileConfigId = fileConfigService.createFileConfig(request);
        // Assert
        assertNotNull(fileConfigId);
        // Verify record properties are correct
        FileConfigEntity fileConfig = fileConfigMapper.selectById(fileConfigId);
        assertPojoEquals(request, fileConfig, "id", "config");
        assertFalse(fileConfig.getMaster());
        assertEquals("/focela", ((LocalFileClientConfig) fileConfig.getConfig()).getBasePath());
        assertEquals("https://www.example.com", ((LocalFileClientConfig) fileConfig.getConfig()).getDomain());
        // Verify cache
        assertNull(fileConfigService.getClientCache().getIfPresent(fileConfigId));
    }

    @Test
    public void testUpdateFileConfig_success() {
        // mock data
        FileConfigEntity dbFileConfig = randomPojo(FileConfigEntity.class, o -> o.setStorage(FileStorageEnum.LOCAL.getStorage())
                .setConfig(new LocalFileClientConfig().setBasePath("/focela").setDomain("https://www.example.com")));
        fileConfigMapper.insert(dbFileConfig);// @Sql: first insert an existing record
        // Prepare parameters
        FileConfigSaveRequest request = randomPojo(FileConfigSaveRequest.class, o -> {
            o.setId(dbFileConfig.getId()); // set the ID to update
            o.setStorage(FileStorageEnum.LOCAL.getStorage());
            Map<String, Object> config = MapUtil.<String, Object>builder().put("basePath", "/focela2")
                    .put("domain", "https://www.example.com").build();
            o.setConfig(config);
        });

        // Invoke
        fileConfigService.updateFileConfig(request);
        // Verify update is correct
        FileConfigEntity fileConfig = fileConfigMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, fileConfig, "config");
        assertEquals("/focela2", ((LocalFileClientConfig) fileConfig.getConfig()).getBasePath());
        assertEquals("https://www.example.com", ((LocalFileClientConfig) fileConfig.getConfig()).getDomain());
        // Verify cache
        assertNull(fileConfigService.getClientCache().getIfPresent(fileConfig.getId()));
    }

    @Test
    public void testUpdateFileConfig_notExists() {
        // Prepare parameters
        FileConfigSaveRequest request = randomPojo(FileConfigSaveRequest.class);

        // Invoke and verify exception
        assertServiceException(() -> fileConfigService.updateFileConfig(request), FILE_CONFIG_NOT_EXISTS);
    }

    @Test
    public void testUpdateFileConfigMaster_success() {
        // mock data
        FileConfigEntity dbFileConfig = randomFileConfigEntity().setMaster(false);
        fileConfigMapper.insert(dbFileConfig);// @Sql: first insert an existing record
        FileConfigEntity masterFileConfig = randomFileConfigEntity().setMaster(true);
        fileConfigMapper.insert(masterFileConfig);// @Sql: first insert an existing record

        // Invoke
        fileConfigService.updateFileConfigMaster(dbFileConfig.getId());
        // Assert data
        assertTrue(fileConfigMapper.selectById(dbFileConfig.getId()).getMaster());
        assertFalse(fileConfigMapper.selectById(masterFileConfig.getId()).getMaster());
        // Verify cache
        assertNull(fileConfigService.getClientCache().getIfPresent(0L));
    }

    @Test
    public void testUpdateFileConfigMaster_notExists() {
        // Invoke and verify exception
        assertServiceException(() -> fileConfigService.updateFileConfigMaster(randomLongId()), FILE_CONFIG_NOT_EXISTS);
    }

    @Test
    public void testDeleteFileConfig_success() {
        // mock data
        FileConfigEntity dbFileConfig = randomFileConfigEntity().setMaster(false);
        fileConfigMapper.insert(dbFileConfig);// @Sql: first insert an existing record
        // Prepare parameters
        Long id = dbFileConfig.getId();

        // Invoke
        fileConfigService.deleteFileConfig(id);
        // Verify data no longer exists
        assertNull(fileConfigMapper.selectById(id));
        // Verify cache
        assertNull(fileConfigService.getClientCache().getIfPresent(id));
    }

    @Test
    public void testDeleteFileConfig_notExists() {
        // Prepare parameters
        Long id = randomLongId();

        // Invoke and verify exception
        assertServiceException(() -> fileConfigService.deleteFileConfig(id), FILE_CONFIG_NOT_EXISTS);
    }

    @Test
    public void testDeleteFileConfig_master() {
        // mock data
        FileConfigEntity dbFileConfig = randomFileConfigEntity().setMaster(true);
        fileConfigMapper.insert(dbFileConfig);// @Sql: first insert an existing record
        // Prepare parameters
        Long id = dbFileConfig.getId();

        // Invoke and verify exception
        assertServiceException(() -> fileConfigService.deleteFileConfig(id), FILE_CONFIG_DELETE_FAIL_MASTER);
    }

    @Test
    public void testGetFileConfigPage() {
        // mock data
        FileConfigEntity dbFileConfig = randomFileConfigEntity().setName("focela-source")
                .setStorage(FileStorageEnum.LOCAL.getStorage());
        dbFileConfig.setCreateTime(LocalDateTimeUtil.parse("2020-01-23", DatePattern.NORM_DATE_PATTERN));// will be queried later
        fileConfigMapper.insert(dbFileConfig);
        // Test name mismatch
        fileConfigMapper.insert(cloneIgnoreId(dbFileConfig, o -> o.setName("other")));
        // Test storage mismatch
        fileConfigMapper.insert(cloneIgnoreId(dbFileConfig, o -> o.setStorage(FileStorageEnum.DB.getStorage())));
        // Test createTime mismatch
        fileConfigMapper.insert(cloneIgnoreId(dbFileConfig, o -> o.setCreateTime(LocalDateTimeUtil.parse("2020-11-23", DatePattern.NORM_DATE_PATTERN))));
        // Prepare parameters
        FileConfigPageRequest request = new FileConfigPageRequest();
        request.setName("focela");
        request.setStorage(FileStorageEnum.LOCAL.getStorage());
        request.setCreateTime((new LocalDateTime[]{buildTime(2020, 1, 1),
                buildTime(2020, 1, 24)}));

        // Invoke
        PageResult<FileConfigEntity> pageResult = fileConfigService.getFileConfigPage(request);
        // Assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbFileConfig, pageResult.getList().get(0));
    }

    @Test
    public void testFileConfig() throws Exception {
        // mock data
        FileConfigEntity dbFileConfig = randomFileConfigEntity().setMaster(false);
        fileConfigMapper.insert(dbFileConfig);// @Sql: first insert an existing record
        // Prepare parameters
        Long id = dbFileConfig.getId();
        // mock the Client
        FileClient fileClient = mock(FileClient.class);
        when(fileClientFactory.getFileClient(eq(id))).thenReturn(fileClient);
        when(fileClient.upload(any(), any(), any())).thenReturn("https://www.example.com");

        // Invoke and verify
        assertEquals("https://www.example.com", fileConfigService.testFileConfig(id));
    }

    @Test
    public void testGetFileConfig() {
        // mock data
        FileConfigEntity dbFileConfig = randomFileConfigEntity().setMaster(false);
        fileConfigMapper.insert(dbFileConfig);// @Sql: first insert an existing record
        // Prepare parameters
        Long id = dbFileConfig.getId();

        // Invoke and verify
        assertPojoEquals(dbFileConfig, fileConfigService.getFileConfig(id));
    }

    @Test
    public void testGetFileClient() {
        // mock data
        FileConfigEntity fileConfig = randomFileConfigEntity().setMaster(false);
        fileConfigMapper.insert(fileConfig);
        // Prepare parameters
        Long id = fileConfig.getId();
        // mock the Client
        FileClient fileClient = new LocalFileClient(id, new LocalFileClientConfig());
        when(fileClientFactory.getFileClient(eq(id))).thenReturn(fileClient);

        // Invoke and verify
        assertSame(fileClient, fileConfigService.getFileClient(id));
        // Assert cache
        verify(fileClientFactory).createOrUpdateFileClient(eq(id), eq(fileConfig.getStorage()),
                eq(fileConfig.getConfig()));
    }

    @Test
    public void testGetMasterFileClient() {
        // mock data
        FileConfigEntity fileConfig = randomFileConfigEntity().setMaster(true);
        fileConfigMapper.insert(fileConfig);
        // Prepare parameters
        Long id = fileConfig.getId();
        // mock the Client
        FileClient fileClient = new LocalFileClient(id, new LocalFileClientConfig());
        when(fileClientFactory.getFileClient(eq(fileConfig.getId()))).thenReturn(fileClient);

        // Invoke and verify
        assertSame(fileClient, fileConfigService.getMasterFileClient());
        // Assert cache
        verify(fileClientFactory).createOrUpdateFileClient(eq(fileConfig.getId()), eq(fileConfig.getStorage()),
                eq(fileConfig.getConfig()));
    }

    private FileConfigEntity randomFileConfigEntity() {
        return randomPojo(FileConfigEntity.class).setStorage(randomEle(FileStorageEnum.values()).getStorage())
                .setConfig(new EmptyFileClientConfig());
    }

    @Data
    public static class EmptyFileClientConfig implements FileClientConfig, Serializable {

    }

}
