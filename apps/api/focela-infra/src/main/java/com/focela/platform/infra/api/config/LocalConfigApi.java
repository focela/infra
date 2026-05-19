package com.focela.platform.infra.api.config;

import com.focela.platform.infra.domain.entity.config.ConfigEntity;
import com.focela.platform.infra.service.config.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Implementation class of the param config API
 */
@Service
@Validated
@RequiredArgsConstructor
public class LocalConfigApi implements ConfigApi {

    private final ConfigService configService;

    @Override
    public String getConfigValueByKey(String key) {
        ConfigEntity config = configService.getConfigByKey(key);
        return config != null ? config.getValue() : null;
    }

}
