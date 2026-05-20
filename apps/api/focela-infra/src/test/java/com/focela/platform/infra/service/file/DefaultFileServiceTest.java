package com.focela.platform.infra.service.file;

import cn.hutool.core.io.resource.ResourceUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.ObjectUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.test.core.utils.AssertUtils;
import com.focela.platform.infra.controller.admin.file.request.FilePageRequest;
import com.focela.platform.infra.domain.entity.file.FileEntity;
import com.focela.platform.infra.repository.mapper.file.FileMapper;
import com.focela.platform.infra.config.file.client.FileClient;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.FILE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@Import({DefaultFileService.class})
public class DefaultFileServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultFileService fileService;

    @Resource
    private FileMapper fileMapper;

    @MockitoBean
    private FileConfigService fileConfigService;

    @BeforeEach
    public void setUp() {
        DefaultFileService.PATH_PREFIX_DATE_ENABLE = true;
        DefaultFileService.PATH_SUFFIX_TIMESTAMP_ENABLE = true;
    }

    @Test
    public void testGetFilePage() {
        // mock data
        FileEntity dbFile = randomPojo(FileEntity.class, o -> { // will be queried later
            o.setPath("focela");
            o.setType("image/jpg");
            o.setCreateTime(buildTime(2021, 1, 15));
        });
        fileMapper.insert(dbFile);
        // Test path mismatch
        fileMapper.insert(ObjectUtils.cloneIgnoreId(dbFile, o -> o.setPath("potato")));
        // Test type mismatch
        fileMapper.insert(ObjectUtils.cloneIgnoreId(dbFile, o -> {
            o.setType("image/png");
        }));
        // Test createTime mismatch
        fileMapper.insert(ObjectUtils.cloneIgnoreId(dbFile, o -> {
            o.setCreateTime(buildTime(2020, 1, 15));
        }));
        // Prepare parameters
        FilePageRequest request = new FilePageRequest();
        request.setPath("focela");
        request.setType("jp");
        request.setCreateTime((new LocalDateTime[]{buildTime(2021, 1, 10), buildTime(2021, 1, 20)}));

        // Invoke
        PageResult<FileEntity> pageResult = fileService.getFilePage(request);
        // Assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        AssertUtils.assertPojoEquals(dbFile, pageResult.getList().get(0));
    }

    /**
     * content, name, directory and type are all non-null
     */
    @Test
    public void testCreateFile_successWhenAllFieldsProvided() throws Exception {
        // Prepare parameters
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        String name = "test file name";
        String directory = randomString();
        String type = "image/jpeg";
        // mock Master file client
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getMasterFileClient()).thenReturn(client);
        String url = randomString();
        AtomicReference<String> pathRef = new AtomicReference<>();
        when(client.upload(same(content), argThat(path -> {
            assertTrue(path.matches(directory + "/\\d{8}/" + name + "_\\d+.jpg"));
            pathRef.set(path);
            return true;
        }), eq(type))).thenReturn(url);
        when(client.getId()).thenReturn(10L);
        // Invoke
        String result = fileService.createFile(content, name, directory, type);
        // Assert
        assertEquals(result, url);
        // Verify data
        FileEntity file = fileMapper.selectOne(FileEntity::getUrl, url);
        assertEquals(10L, file.getConfigId());
        assertEquals(pathRef.get(), file.getPath());
        assertEquals(url, file.getUrl());
        assertEquals(type, file.getType());
        assertEquals(content.length, file.getSize());
    }

    /**
     * content is non-null, all others are null
     */
    @Test
    public void testCreateFile_successWhenOnlyContentProvided() throws Exception {
        // Prepare parameters
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        // mock Master file client
        String type = "image/jpeg";
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getMasterFileClient()).thenReturn(client);
        String url = randomString();
        AtomicReference<String> pathRef = new AtomicReference<>();
        when(client.upload(same(content), argThat(path -> {
            assertTrue(path.matches("\\d{8}/6318848e882d8a7e7e82789d87608f684ee52d41966bfc8cad3ce15aad2b970e_\\d+\\.jpg"));
            pathRef.set(path);
            return true;
        }), eq(type))).thenReturn(url);
        when(client.getId()).thenReturn(10L);
        // Invoke
        String result = fileService.createFile(content, null, null, null);
        // Assert
        assertEquals(result, url);
        // Verify data
        FileEntity file = fileMapper.selectOne(FileEntity::getUrl, url);
        assertEquals(10L, file.getConfigId());
        assertEquals(pathRef.get(), file.getPath());
        assertEquals(url, file.getUrl());
        assertEquals(type, file.getType());
        assertEquals(content.length, file.getSize());
    }

    @Test
    public void testDeleteFile_success() throws Exception {
        // mock data
        FileEntity dbFile = randomPojo(FileEntity.class, o -> o.setConfigId(10L).setPath("potato.jpg"));
        fileMapper.insert(dbFile);// @Sql: first insert an existing record
        // mock Master file client
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getFileClient(eq(10L))).thenReturn(client);
        // Prepare parameters
        Long id = dbFile.getId();

        // Invoke
        fileService.deleteFile(id);
        // Verify data no longer exists
        assertNull(fileMapper.selectById(id));
        // Verify the call
        verify(client).delete(eq("potato.jpg"));
    }

    @Test
    public void testDeleteFile_notExists() {
        // Prepare parameters
        Long id = randomLongId();

        // Invoke and verify exception
        assertServiceException(() -> fileService.deleteFile(id), FILE_NOT_FOUND);
    }

    @Test
    public void testGetFileContent() throws Exception {
        // Prepare parameters
        Long configId = 10L;
        String path = "potato.jpg";
        // mock the method
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getFileClient(eq(10L))).thenReturn(client);
        byte[] content = new byte[]{};
        when(client.getContent(eq("potato.jpg"))).thenReturn(content);

        // Invoke
        byte[] result = fileService.getFileContent(configId, path);
        // Assert
        assertSame(result, content);
    }

    @Test
    public void testGenerateUploadPath_AllEnabled() {
        // Prepare parameters
        String name = "test.jpg";
        String directory = "avatar";
        DefaultFileService.PATH_PREFIX_DATE_ENABLE = true;
        DefaultFileService.PATH_SUFFIX_TIMESTAMP_ENABLE = true;

        // Invoke
        String path = fileService.generateUploadPath(name, directory);

        // Assert
        // Format: avatar/yyyyMMdd/test_timestamp.jpg
        assertTrue(path.startsWith(directory + "/"));
        // Contains date format: 8-digit number, e.g. 20240517
        assertTrue(path.matches(directory + "/\\d{8}/test_\\d+\\.jpg"));
    }

    @Test
    public void testGenerateUploadPath_PrefixEnabled_SuffixDisabled() {
        // Prepare parameters
        String name = "test.jpg";
        String directory = "avatar";
        DefaultFileService.PATH_PREFIX_DATE_ENABLE = true;
        DefaultFileService.PATH_SUFFIX_TIMESTAMP_ENABLE = false;

        // Invoke
        String path = fileService.generateUploadPath(name, directory);

        // Assert
        // Format: avatar/yyyyMMdd/test.jpg
        assertTrue(path.startsWith(directory + "/"));
        // Contains date format: 8-digit number, e.g. 20240517
        assertTrue(path.matches(directory + "/\\d{8}/test\\.jpg"));
    }

    @Test
    public void testGenerateUploadPath_PrefixDisabled_SuffixEnabled() {
        // Prepare parameters
        String name = "test.jpg";
        String directory = "avatar";
        DefaultFileService.PATH_PREFIX_DATE_ENABLE = false;
        DefaultFileService.PATH_SUFFIX_TIMESTAMP_ENABLE = true;

        // Invoke
        String path = fileService.generateUploadPath(name, directory);

        // Assert
        // Format: avatar/test_timestamp.jpg
        assertTrue(path.startsWith(directory + "/"));
        assertTrue(path.matches(directory + "/test_\\d+\\.jpg"));
    }

    @Test
    public void testGenerateUploadPath_AllDisabled() {
        // Prepare parameters
        String name = "test.jpg";
        String directory = "avatar";
        DefaultFileService.PATH_PREFIX_DATE_ENABLE = false;
        DefaultFileService.PATH_SUFFIX_TIMESTAMP_ENABLE = false;

        // Invoke
        String path = fileService.generateUploadPath(name, directory);

        // Assert
        // Format: avatar/test.jpg
        assertEquals(directory + "/" + name, path);
    }

    @Test
    public void testGenerateUploadPath_NoExtension() {
        // Prepare parameters
        String name = "test";
        String directory = "avatar";
        DefaultFileService.PATH_PREFIX_DATE_ENABLE = true;
        DefaultFileService.PATH_SUFFIX_TIMESTAMP_ENABLE = true;

        // Invoke
        String path = fileService.generateUploadPath(name, directory);

        // Assert
        // Format: avatar/yyyyMMdd/test_timestamp
        assertTrue(path.startsWith(directory + "/"));
        assertTrue(path.matches(directory + "/\\d{8}/test_\\d+"));
    }

    @Test
    public void testGenerateUploadPath_DirectoryNull() {
        // Prepare parameters
        String name = "test.jpg";
        String directory = null;
        DefaultFileService.PATH_PREFIX_DATE_ENABLE = true;
        DefaultFileService.PATH_SUFFIX_TIMESTAMP_ENABLE = true;

        // Invoke
        String path = fileService.generateUploadPath(name, directory);

        // Assert
        // Format: yyyyMMdd/test_timestamp.jpg
        assertTrue(path.matches("\\d{8}/test_\\d+\\.jpg"));
    }

    @Test
    public void testGenerateUploadPath_DirectoryEmpty() {
        // Prepare parameters
        String name = "test.jpg";
        String directory = "";
        DefaultFileService.PATH_PREFIX_DATE_ENABLE = true;
        DefaultFileService.PATH_SUFFIX_TIMESTAMP_ENABLE = true;

        // Invoke
        String path = fileService.generateUploadPath(name, directory);

        // Assert
        // Format: yyyyMMdd/test_timestamp.jpg
        assertTrue(path.matches("\\d{8}/test_\\d+\\.jpg"));
    }

}
