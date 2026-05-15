package com.focela.platform.infra.api.config;

import com.focela.platform.infra.entity.config.ConfigEntity;
import com.focela.platform.infra.service.config.ConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Implementation class of the param config API
 */
@Service
@Validated
public class LocalConfigApi implements ConfigApi {

    @Resource
    private ConfigService configService;

    @Override
    public String getConfigValueByKey(String key) {
        ConfigEntity config = configService.getConfigByKey(key);
        return config != null ? config.getValue() : null;
    }

}
