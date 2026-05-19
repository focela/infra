package com.focela.platform.infra.service.file;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.json.JsonUtils;
import com.focela.platform.common.utils.validation.ValidationUtils;
import com.focela.platform.infra.controller.admin.file.request.config.FileConfigPageRequest;
import com.focela.platform.infra.controller.admin.file.request.config.FileConfigSaveRequest;
import com.focela.platform.infra.converter.file.FileConfigConverter;
import com.focela.platform.infra.domain.entity.file.FileConfigEntity;
import com.focela.platform.infra.repository.mapper.file.FileConfigMapper;
import com.focela.platform.infra.config.file.client.FileClient;
import com.focela.platform.infra.config.file.client.FileClientConfig;
import com.focela.platform.infra.config.file.client.FileClientFactory;
import com.focela.platform.infra.config.file.enums.FileStorageEnum;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.common.utils.cache.CacheUtils.buildAsyncReloadingCache;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.FILE_CONFIG_DELETE_FAIL_MASTER;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.FILE_CONFIG_NOT_EXISTS;

/**
 * Implementation class of the file config Service
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class DefaultFileConfigService implements FileConfigService {

    private static final Long CACHE_MASTER_ID = 0L;

    /**
     * {@link FileClient} cache. Used to asynchronously refresh fileClientFactory.
     */
    @Getter
    private final LoadingCache<Long, FileClient> clientCache = buildAsyncReloadingCache(Duration.ofSeconds(10L),
            new CacheLoader<Long, FileClient>() {

                @Override
                public FileClient load(Long id) {
                    FileConfigEntity config = Objects.equals(CACHE_MASTER_ID, id) ?
                            fileConfigMapper.selectByMaster() : fileConfigMapper.selectById(id);
                    if (config != null) {
                        fileClientFactory.createOrUpdateFileClient(config.getId(), config.getStorage(), config.getConfig());
                    }
                    return fileClientFactory.getFileClient(null == config ? id : config.getId());
                }

            });

    private final FileClientFactory fileClientFactory;

    private final FileConfigMapper fileConfigMapper;

    private final Validator validator;

    @Override
    public Long createFileConfig(FileConfigSaveRequest createRequest) {
        FileConfigEntity fileConfig = FileConfigConverter.INSTANCE.convert(createRequest)
                .setConfig(parseClientConfig(createRequest.getStorage(), createRequest.getConfig()))
                .setMaster(false); // Defaults to non-master
        fileConfigMapper.insert(fileConfig);
        return fileConfig.getId();
    }

    @Override
    public void updateFileConfig(FileConfigSaveRequest updateRequest) {
        // Verify it exists
        FileConfigEntity config = validateFileConfigExists(updateRequest.getId());
        // Update
        FileConfigEntity updateObj = FileConfigConverter.INSTANCE.convert(updateRequest)
                .setConfig(parseClientConfig(config.getStorage(), updateRequest.getConfig()));
        fileConfigMapper.updateById(updateObj);

        // Clear cache
        clearCache(config.getId(), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFileConfigMaster(Long id) {
        // Verify it exists
        validateFileConfigExists(id);
        // Mark all others as non-master
        fileConfigMapper.updateBatch(new FileConfigEntity().setMaster(false));
        // Update
        fileConfigMapper.updateById(new FileConfigEntity().setId(id).setMaster(true));

        // Clear cache
        clearCache(null, true);
    }

    private FileClientConfig parseClientConfig(Integer storage, Map<String, Object> config) {
        // Get the config class
        Class<? extends FileClientConfig> configClass = FileStorageEnum.getByStorage(storage)
                .getConfigClass();
        FileClientConfig clientConfig = JsonUtils.parseObject2(JsonUtils.toJsonString(config), configClass);
        // Validate params
        ValidationUtils.validate(validator, clientConfig);
        // Set params
        return clientConfig;
    }

    @Override
    public void deleteFileConfig(Long id) {
        // Verify it exists
        FileConfigEntity config = validateFileConfigExists(id);
        if (Boolean.TRUE.equals(config.getMaster())) {
            throw exception(FILE_CONFIG_DELETE_FAIL_MASTER);
        }
        // Delete
        fileConfigMapper.deleteById(id);

        // Clear cache
        clearCache(id, null);
    }

    @Override
    public void deleteFileConfigList(List<Long> ids) {
        // Check whether the list contains the master config
        List<FileConfigEntity> configs = fileConfigMapper.selectByIds(ids);
        for (FileConfigEntity config : configs) {
            if (Boolean.TRUE.equals(config.getMaster())) {
                throw exception(FILE_CONFIG_DELETE_FAIL_MASTER);
            }
        }

        // Batch delete
        fileConfigMapper.deleteByIds(ids);

        // Clear cache
        ids.forEach(id -> clearCache(id, null));
    }

    /**
     * Clear the cache for the specified file config.
     *
     * @param id     config ID
     * @param master whether master config
     */
    private void clearCache(Long id, Boolean master) {
        if (id != null) {
            clientCache.invalidate(id);
        }
        if (Boolean.TRUE.equals(master)) {
            clientCache.invalidate(CACHE_MASTER_ID);
        }
    }

    private FileConfigEntity validateFileConfigExists(Long id) {
        FileConfigEntity config = fileConfigMapper.selectById(id);
        if (config == null) {
            throw exception(FILE_CONFIG_NOT_EXISTS);
        }
        return config;
    }

    @Override
    public FileConfigEntity getFileConfig(Long id) {
        return fileConfigMapper.selectById(id);
    }

    @Override
    public PageResult<FileConfigEntity> getFileConfigPage(FileConfigPageRequest pageRequest) {
        return fileConfigMapper.selectPage(pageRequest);
    }

    @Override
    public String testFileConfig(Long id) throws Exception {
        // Verify it exists
        validateFileConfigExists(id);
        // Upload file
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        return getFileClient(id).upload(content, IdUtil.fastSimpleUUID() + ".jpg", "image/jpeg");
    }

    @Override
    public FileClient getFileClient(Long id) {
        return clientCache.getUnchecked(id);
    }

    @Override
    public FileClient getMasterFileClient() {
        return clientCache.getUnchecked(CACHE_MASTER_ID);
    }

}
