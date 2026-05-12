package com.focela.platform.module.infra.api.config;

import com.focela.platform.module.infra.repository.entity.config.ConfigEntity;
import com.focela.platform.module.infra.service.config.ConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 参数配置 API 实现类
 *
 * @author 芋道源码
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
