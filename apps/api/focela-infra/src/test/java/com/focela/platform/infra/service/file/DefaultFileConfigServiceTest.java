package com.focela.platform.infra.service.file;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.infra.controller.admin.file.dto.config.FileConfigPageRequest;
import com.focela.platform.infra.controller.admin.file.dto.config.FileConfigSaveRequest;
import com.focela.platform.infra.entity.file.FileConfigEntity;
import com.focela.platform.infra.repository.mapper.file.FileConfigMapper;
import com.focela.platform.infra.config.file.core.client.FileClient;
import com.focela.platform.infra.config.file.core.client.FileClientConfig;
import com.focela.platform.infra.config.file.core.client.FileClientFactory;
import com.focela.platform.infra.config.file.core.client.local.LocalFileClient;
import com.focela.platform.infra.config.file.core.client.local.LocalFileClientConfig;
import com.focela.platform.infra.config.file.core.enums.FileStorageEnum;
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
import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.framework.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.utils.RandomUtils.randomLongId;
import static com.focela.platform.framework.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.infra.constants.ErrorCodeConstants.FILE_CONFIG_DELETE_FAIL_MASTER;
import static com.focela.platform.infra.constants.ErrorCodeConstants.FILE_CONFIG_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link DefaultFileConfigService} 的单元测试类
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
        // 准备参数
        Map<String, Object> config = MapUtil.<String, Object>builder().put("basePath", "/yunai")
                .put("domain", "https://www.example.com").build();
        FileConfigSaveRequest request = randomPojo(FileConfigSaveRequest.class,
                o -> o.setStorage(FileStorageEnum.LOCAL.getStorage()).setConfig(config))
                .setId(null); // 避免 id 被赋值

        // 调用
        Long fileConfigId = fileConfigService.createFileConfig(request);
        // 断言
        assertNotNull(fileConfigId);
        // 校验记录的属性是否正确
        FileConfigEntity fileConfig = fileConfigMapper.selectById(fileConfigId);
        assertPojoEquals(request, fileConfig, "id", "config");
        assertFalse(fileConfig.getMaster());
        assertEquals("/yunai", ((LocalFileClientConfig) fileConfig.getConfig()).getBasePath());
        assertEquals("https://www.example.com", ((LocalFileClientConfig) fileConfig.getConfig()).getDomain());
        // 验证 cache
        assertNull(fileConfigService.getClientCache().getIfPresent(fileConfigId));
    }

    @Test
    public void testUpdateFileConfig_success() {
        // mock 数据
        FileConfigEntity dbFileConfig = randomPojo(FileConfigEntity.class, o -> o.setStorage(FileStorageEnum.LOCAL.getStorage())
                .setConfig(new LocalFileClientConfig().setBasePath("/yunai").setDomain("https://www.example.com")));
        fileConfigMapper.insert(dbFileConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数
        FileConfigSaveRequest request = randomPojo(FileConfigSaveRequest.class, o -> {
            o.setId(dbFileConfig.getId()); // 设置更新的 ID
            o.setStorage(FileStorageEnum.LOCAL.getStorage());
            Map<String, Object> config = MapUtil.<String, Object>builder().put("basePath", "/yunai2")
                    .put("domain", "https://www.example.com").build();
            o.setConfig(config);
        });

        // 调用
        fileConfigService.updateFileConfig(request);
        // 校验是否更新正确
        FileConfigEntity fileConfig = fileConfigMapper.selectById(request.getId()); // 获取最新的
        assertPojoEquals(request, fileConfig, "config");
        assertEquals("/yunai2", ((LocalFileClientConfig) fileConfig.getConfig()).getBasePath());
        assertEquals("https://www.example.com", ((LocalFileClientConfig) fileConfig.getConfig()).getDomain());
        // 验证 cache
        assertNull(fileConfigService.getClientCache().getIfPresent(fileConfig.getId()));
    }

    @Test
    public void testUpdateFileConfig_notExists() {
        // 准备参数
        FileConfigSaveRequest request = randomPojo(FileConfigSaveRequest.class);

        // 调用, 并断言异常
        assertServiceException(() -> fileConfigService.updateFileConfig(request), FILE_CONFIG_NOT_EXISTS);
    }

    @Test
    public void testUpdateFileConfigMaster_success() {
        // mock 数据
        FileConfigEntity dbFileConfig = randomFileConfigDO().setMaster(false);
        fileConfigMapper.insert(dbFileConfig);// @Sql: 先插入出一条存在的数据
        FileConfigEntity masterFileConfig = randomFileConfigDO().setMaster(true);
        fileConfigMapper.insert(masterFileConfig);// @Sql: 先插入出一条存在的数据

        // 调用
        fileConfigService.updateFileConfigMaster(dbFileConfig.getId());
        // 断言数据
        assertTrue(fileConfigMapper.selectById(dbFileConfig.getId()).getMaster());
        assertFalse(fileConfigMapper.selectById(masterFileConfig.getId()).getMaster());
        // 验证 cache
        assertNull(fileConfigService.getClientCache().getIfPresent(0L));
    }

    @Test
    public void testUpdateFileConfigMaster_notExists() {
        // 调用, 并断言异常
        assertServiceException(() -> fileConfigService.updateFileConfigMaster(randomLongId()), FILE_CONFIG_NOT_EXISTS);
    }

    @Test
    public void testDeleteFileConfig_success() {
        // mock 数据
        FileConfigEntity dbFileConfig = randomFileConfigDO().setMaster(false);
        fileConfigMapper.insert(dbFileConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbFileConfig.getId();

        // 调用
        fileConfigService.deleteFileConfig(id);
        // 校验数据不存在了
        assertNull(fileConfigMapper.selectById(id));
        // 验证 cache
        assertNull(fileConfigService.getClientCache().getIfPresent(id));
    }

    @Test
    public void testDeleteFileConfig_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> fileConfigService.deleteFileConfig(id), FILE_CONFIG_NOT_EXISTS);
    }

    @Test
    public void testDeleteFileConfig_master() {
        // mock 数据
        FileConfigEntity dbFileConfig = randomFileConfigDO().setMaster(true);
        fileConfigMapper.insert(dbFileConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbFileConfig.getId();

        // 调用, 并断言异常
        assertServiceException(() -> fileConfigService.deleteFileConfig(id), FILE_CONFIG_DELETE_FAIL_MASTER);
    }

    @Test
    public void testGetFileConfigPage() {
        // mock 数据
        FileConfigEntity dbFileConfig = randomFileConfigDO().setName("芋道源码")
                .setStorage(FileStorageEnum.LOCAL.getStorage());
        dbFileConfig.setCreateTime(LocalDateTimeUtil.parse("2020-01-23", DatePattern.NORM_DATE_PATTERN));// 等会查询到
        fileConfigMapper.insert(dbFileConfig);
        // 测试 name 不匹配
        fileConfigMapper.insert(cloneIgnoreId(dbFileConfig, o -> o.setName("源码")));
        // 测试 storage 不匹配
        fileConfigMapper.insert(cloneIgnoreId(dbFileConfig, o -> o.setStorage(FileStorageEnum.DB.getStorage())));
        // 测试 createTime 不匹配
        fileConfigMapper.insert(cloneIgnoreId(dbFileConfig, o -> o.setCreateTime(LocalDateTimeUtil.parse("2020-11-23", DatePattern.NORM_DATE_PATTERN))));
        // 准备参数
        FileConfigPageRequest request = new FileConfigPageRequest();
        request.setName("芋道");
        request.setStorage(FileStorageEnum.LOCAL.getStorage());
        request.setCreateTime((new LocalDateTime[]{buildTime(2020, 1, 1),
                buildTime(2020, 1, 24)}));

        // 调用
        PageResult<FileConfigEntity> pageResult = fileConfigService.getFileConfigPage(request);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbFileConfig, pageResult.getList().get(0));
    }

    @Test
    public void testFileConfig() throws Exception {
        // mock 数据
        FileConfigEntity dbFileConfig = randomFileConfigDO().setMaster(false);
        fileConfigMapper.insert(dbFileConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbFileConfig.getId();
        // mock 获得 Client
        FileClient fileClient = mock(FileClient.class);
        when(fileClientFactory.getFileClient(eq(id))).thenReturn(fileClient);
        when(fileClient.upload(any(), any(), any())).thenReturn("https://www.example.com");

        // 调用，并断言
        assertEquals("https://www.example.com", fileConfigService.testFileConfig(id));
    }

    @Test
    public void testGetFileConfig() {
        // mock 数据
        FileConfigEntity dbFileConfig = randomFileConfigDO().setMaster(false);
        fileConfigMapper.insert(dbFileConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbFileConfig.getId();

        // 调用，并断言
        assertPojoEquals(dbFileConfig, fileConfigService.getFileConfig(id));
    }

    @Test
    public void testGetFileClient() {
        // mock 数据
        FileConfigEntity fileConfig = randomFileConfigDO().setMaster(false);
        fileConfigMapper.insert(fileConfig);
        // 准备参数
        Long id = fileConfig.getId();
        // mock 获得 Client
        FileClient fileClient = new LocalFileClient(id, new LocalFileClientConfig());
        when(fileClientFactory.getFileClient(eq(id))).thenReturn(fileClient);

        // 调用，并断言
        assertSame(fileClient, fileConfigService.getFileClient(id));
        // 断言缓存
        verify(fileClientFactory).createOrUpdateFileClient(eq(id), eq(fileConfig.getStorage()),
                eq(fileConfig.getConfig()));
    }

    @Test
    public void testGetMasterFileClient() {
        // mock 数据
        FileConfigEntity fileConfig = randomFileConfigDO().setMaster(true);
        fileConfigMapper.insert(fileConfig);
        // 准备参数
        Long id = fileConfig.getId();
        // mock 获得 Client
        FileClient fileClient = new LocalFileClient(id, new LocalFileClientConfig());
        when(fileClientFactory.getFileClient(eq(fileConfig.getId()))).thenReturn(fileClient);

        // 调用，并断言
        assertSame(fileClient, fileConfigService.getMasterFileClient());
        // 断言缓存
        verify(fileClientFactory).createOrUpdateFileClient(eq(fileConfig.getId()), eq(fileConfig.getStorage()),
                eq(fileConfig.getConfig()));
    }

    private FileConfigEntity randomFileConfigDO() {
        return randomPojo(FileConfigEntity.class).setStorage(randomEle(FileStorageEnum.values()).getStorage())
                .setConfig(new EmptyFileClientConfig());
    }

    @Data
    public static class EmptyFileClientConfig implements FileClientConfig, Serializable {

    }

}
