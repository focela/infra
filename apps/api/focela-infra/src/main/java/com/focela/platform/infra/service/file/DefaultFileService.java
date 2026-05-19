package com.focela.platform.infra.service.file;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.http.HttpUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.infra.controller.admin.file.request.FileCreateRequest;
import com.focela.platform.infra.controller.admin.file.request.FilePageRequest;
import com.focela.platform.infra.controller.admin.file.response.FilePresignedUrlResponse;
import com.focela.platform.infra.domain.entity.file.FileEntity;
import com.focela.platform.infra.repository.mapper.file.FileMapper;
import com.focela.platform.infra.config.file.client.FileClient;
import com.focela.platform.infra.config.file.utils.FileTypeUtils;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.hutool.core.date.DatePattern.PURE_DATE_PATTERN;
import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.FILE_NOT_EXISTS;

/**
 * Implementation class of the file Service
 */
@Service
@RequiredArgsConstructor
public class DefaultFileService implements FileService {

    /**
     * Whether the upload path prefix includes the date (yyyyMMdd).
     *
     * Purpose: partition stored files by date.
     */
    static boolean PATH_PREFIX_DATE_ENABLE = true;
    /**
     * Whether the upload path suffix includes a timestamp.
     *
     * Purpose: guarantee file-name uniqueness and avoid overwrites.
     * Customization: can be replaced with UUID or another scheme.
     */
    static boolean PATH_SUFFIX_TIMESTAMP_ENABLE = true;

    private final FileConfigService fileConfigService;

    private final FileMapper fileMapper;

    @Override
    public PageResult<FileEntity> getFilePage(FilePageRequest pageRequest) {
        return fileMapper.selectPage(pageRequest);
    }

    @Override
    @SneakyThrows
    public String createFile(byte[] content, String name, String directory, String type) {
        // 1.1 Handle empty type
        if (StrUtil.isEmpty(type)) {
            type = FileTypeUtils.getMimeType(content, name);
        }
        // 1.2 Handle empty name
        if (StrUtil.isEmpty(name)) {
            name = DigestUtil.sha256Hex(content);
        }
        if (StrUtil.isEmpty(FileUtil.extName(name))) {
            // If the name has no extension that matches type, append one
            String extension = FileTypeUtils.getExtension(type);
            if (StrUtil.isNotEmpty(extension)) {
                name = name + extension;
            }
        }

        // 2.1 Generate the upload path; must be unique
        String path = generateUploadPath(name, directory);
        // 2.2 Upload to the file storage
        FileClient client = fileConfigService.getMasterFileClient();
        Assert.notNull(client, "client (master) must not be null");
        String url = client.upload(content, path, type);

        // 3. Save to database
        fileMapper.insert(new FileEntity().setConfigId(client.getId())
                .setName(name).setPath(path).setUrl(url)
                .setType(type).setSize((long) content.length));
        return url;
    }

    @VisibleForTesting
    String generateUploadPath(String name, String directory) {
        // 1. Generate prefix and suffix
        String prefix = null;
        if (PATH_PREFIX_DATE_ENABLE) {
            prefix = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PURE_DATE_PATTERN);
        }
        String suffix = null;
        if (PATH_SUFFIX_TIMESTAMP_ENABLE) {
            suffix = String.valueOf(System.currentTimeMillis());
        }

        // 2.1 Append the suffix
        if (StrUtil.isNotEmpty(suffix)) {
            String ext = FileUtil.extName(name);
            if (StrUtil.isNotEmpty(ext)) {
                name = FileUtil.mainName(name) + StrUtil.C_UNDERLINE + suffix + StrUtil.DOT + ext;
            } else {
                name = name + StrUtil.C_UNDERLINE + suffix;
            }
        }
        // 2.2 Prepend the prefix
        if (StrUtil.isNotEmpty(prefix)) {
            name = prefix + StrUtil.SLASH + name;
        }
        // 2.3 Finally prepend the directory
        if (StrUtil.isNotEmpty(directory)) {
            name = directory + StrUtil.SLASH + name;
        }
        return name;
    }

    @Override
    @SneakyThrows
    public FilePresignedUrlResponse presignPutUrl(String name, String directory) {
        // 1. Generate the upload path; must be unique
        String path = generateUploadPath(name, directory);

        // 2. Get presigned URL
        FileClient fileClient = fileConfigService.getMasterFileClient();
        String uploadUrl = fileClient.presignPutUrl(path);
        String visitUrl = fileClient.presignGetUrl(path, null);
        return new FilePresignedUrlResponse().setConfigId(fileClient.getId())
                .setPath(path).setUploadUrl(uploadUrl).setUrl(visitUrl);
    }

    @Override
    public String presignGetUrl(String url, Integer expirationSeconds) {
        FileClient fileClient = fileConfigService.getMasterFileClient();
        return fileClient.presignGetUrl(url, expirationSeconds);
    }

    @Override
    public Long createFile(FileCreateRequest createRequest) {
        createRequest.setUrl(HttpUtils.removeUrlQuery(createRequest.getUrl())); // Purpose: strip signature params from URLs of private buckets
        FileEntity file = BeanUtils.toBean(createRequest, FileEntity.class);
        fileMapper.insert(file);
        return file.getId();
    }

    @Override
    public FileEntity getFile(Long id) {
        return validateFileExists(id);
    }

    @Override
    public void deleteFile(Long id) throws Exception {
        // Verify it exists
        FileEntity file = validateFileExists(id);

        // Delete from file storage
        FileClient client = fileConfigService.getFileClient(file.getConfigId());
        Assert.notNull(client, "client ({}) must not be null", file.getConfigId());
        client.delete(file.getPath());

        // Delete record
        fileMapper.deleteById(id);
    }

    @Override
    @SneakyThrows
    public void deleteFileList(List<Long> ids) {
        // Delete files
        List<FileEntity> files = fileMapper.selectByIds(ids);
        for (FileEntity file : files) {
            // Get client
            FileClient client = fileConfigService.getFileClient(file.getConfigId());
            Assert.notNull(client, "client ({}) must not be null", file.getPath());
            // Delete file
            client.delete(file.getPath());
        }

        // Delete records
        fileMapper.deleteByIds(ids);
    }

    private FileEntity validateFileExists(Long id) {
        FileEntity fileEntity = fileMapper.selectById(id);
        if (fileEntity == null) {
            throw exception(FILE_NOT_EXISTS);
        }
        return fileEntity;
    }

    @Override
    public byte[] getFileContent(Long configId, String path) throws Exception {
        FileClient client = fileConfigService.getFileClient(configId);
        Assert.notNull(client, "client ({}) must not be null", configId);
        return client.getContent(path);
    }

}
