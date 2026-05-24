package com.focela.platform.infra.service.config;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.infra.controller.admin.config.request.ConfigPageRequest;
import com.focela.platform.infra.controller.admin.config.request.ConfigSaveRequest;
import com.focela.platform.infra.converter.config.ConfigConverter;
import com.focela.platform.infra.domain.entity.config.ConfigEntity;
import com.focela.platform.infra.repository.mapper.config.ConfigMapper;
import com.focela.platform.infra.enums.config.ConfigTypeEnum;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.*;

/**
 * Implementation class of the param config Service
 */
@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class DefaultConfigService implements ConfigService {

    private final ConfigMapper configMapper;

    @Override
    public Long createConfig(ConfigSaveRequest createRequest) {
        // Validate uniqueness of the param config key
        validateConfigKeyUnique(null, createRequest.getKey());

        // Insert param config
        ConfigEntity config = ConfigConverter.INSTANCE.convert(createRequest);
        config.setType(ConfigTypeEnum.CUSTOM.getType());
        configMapper.insert(config);
        return config.getId();
    }

    @Override
    public void updateConfig(ConfigSaveRequest updateRequest) {
        // Verify it exists
        validateConfigExists(updateRequest.getId());
        // Validate uniqueness of the param config key
        validateConfigKeyUnique(updateRequest.getId(), updateRequest.getKey());

        // Update param config
        ConfigEntity updateEntity = ConfigConverter.INSTANCE.convert(updateRequest);
        configMapper.updateById(updateEntity);
    }

    @Override
    public void deleteConfig(Long id) {
        // Verify the config exists
        ConfigEntity config = validateConfigExists(id);
        // Built-in configs cannot be deleted
        if (ConfigTypeEnum.SYSTEM.getType().equals(config.getType())) {
            throw exception(CONFIG_SYSTEM_TYPE_CANNOT_BE_DELETED);
        }
        // Delete
        configMapper.deleteById(id);
    }

    @Override
    public void deleteConfigList(List<Long> ids) {
        // Check whether the list contains a built-in config
        List<ConfigEntity> configs = configMapper.selectByIds(ids);
        configs.forEach(config -> {
            if (ConfigTypeEnum.SYSTEM.getType().equals(config.getType())) {
                throw exception(CONFIG_SYSTEM_TYPE_CANNOT_BE_DELETED);
            }
        });

        // Batch delete
        configMapper.deleteByIds(ids);
    }

    @Override
    public ConfigEntity getConfig(Long id) {
        return configMapper.selectById(id);
    }

    @Override
    public ConfigEntity getConfigByKey(String key) {
        return configMapper.selectByKey(key);
    }

    @Override
    public PageResult<ConfigEntity> getConfigPage(ConfigPageRequest pageRequest) {
        return configMapper.selectPage(pageRequest);
    }

    @VisibleForTesting
    public ConfigEntity validateConfigExists(Long id) {
        if (id == null) {
            return null;
        }
        ConfigEntity config = configMapper.selectById(id);
        if (config == null) {
            throw exception(CONFIG_NOT_FOUND);
        }
        return config;
    }

    @VisibleForTesting
    public void validateConfigKeyUnique(Long id, String key) {
        ConfigEntity config = configMapper.selectByKey(key);
        if (config == null) {
            return;
        }
        // If id is null, we do not need to compare to an existing config with the same id
        if (id == null) {
            throw exception(CONFIG_KEY_DUPLICATE);
        }
        if (!config.getId().equals(id)) {
            throw exception(CONFIG_KEY_DUPLICATE);
        }
    }

}
