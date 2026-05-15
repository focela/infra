package com.focela.platform.module.infra.service.config;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.module.infra.controller.admin.config.dto.ConfigPageRequest;
import com.focela.platform.module.infra.controller.admin.config.dto.ConfigSaveRequest;
import com.focela.platform.module.infra.converter.config.ConfigConverter;
import com.focela.platform.module.infra.entity.config.ConfigEntity;
import com.focela.platform.module.infra.repository.mapper.config.ConfigMapper;
import com.focela.platform.module.infra.enums.config.ConfigTypeEnum;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.focela.platform.framework.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.module.infra.constants.ErrorCodeConstants.*;

/**
 * 参数配置 Service 实现类
 */
@Service
@Slf4j
@Validated
public class DefaultConfigService implements ConfigService {

    @Resource
    private ConfigMapper configMapper;

    @Override
    public Long createConfig(ConfigSaveRequest createRequest) {
        // 校验参数配置 key 的唯一性
        validateConfigKeyUnique(null, createRequest.getKey());

        // 插入参数配置
        ConfigEntity config = ConfigConverter.INSTANCE.convert(createRequest);
        config.setType(ConfigTypeEnum.CUSTOM.getType());
        configMapper.insert(config);
        return config.getId();
    }

    @Override
    public void updateConfig(ConfigSaveRequest updateRequest) {
        // 校验自己存在
        validateConfigExists(updateRequest.getId());
        // 校验参数配置 key 的唯一性
        validateConfigKeyUnique(updateRequest.getId(), updateRequest.getKey());

        // 更新参数配置
        ConfigEntity updateObj = ConfigConverter.INSTANCE.convert(updateRequest);
        configMapper.updateById(updateObj);
    }

    @Override
    public void deleteConfig(Long id) {
        // 校验配置存在
        ConfigEntity config = validateConfigExists(id);
        // 内置配置，不允许删除
        if (ConfigTypeEnum.SYSTEM.getType().equals(config.getType())) {
            throw exception(CONFIG_CAN_NOT_DELETE_SYSTEM_TYPE);
        }
        // 删除
        configMapper.deleteById(id);
    }

    @Override
    public void deleteConfigList(List<Long> ids) {
        // 校验是否有内置配置
        List<ConfigEntity> configs = configMapper.selectByIds(ids);
        configs.forEach(config -> {
            if (ConfigTypeEnum.SYSTEM.getType().equals(config.getType())) {
                throw exception(CONFIG_CAN_NOT_DELETE_SYSTEM_TYPE);
            }
        });

        // 批量删除
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
            throw exception(CONFIG_NOT_EXISTS);
        }
        return config;
    }

    @VisibleForTesting
    public void validateConfigKeyUnique(Long id, String key) {
        ConfigEntity config = configMapper.selectByKey(key);
        if (config == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的参数配置
        if (id == null) {
            throw exception(CONFIG_KEY_DUPLICATE);
        }
        if (!config.getId().equals(id)) {
            throw exception(CONFIG_KEY_DUPLICATE);
        }
    }

}
