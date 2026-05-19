package com.focela.platform.infra.service.config;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.infra.controller.admin.config.request.ConfigPageRequest;
import com.focela.platform.infra.controller.admin.config.request.ConfigSaveRequest;
import com.focela.platform.infra.domain.entity.config.ConfigEntity;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Param config Service interface
 */
public interface ConfigService {

    /**
     * Create a param config.
     *
     * @param createRequest creation info
     * @return config ID
     */
    Long createConfig(@Valid ConfigSaveRequest createRequest);

    /**
     * Update a param config.
     *
     * @param updateRequest update info
     */
    void updateConfig(@Valid ConfigSaveRequest updateRequest);

    /**
     * Delete a param config.
     *
     * @param id config ID
     */
    void deleteConfig(Long id);

    /**
     * Batch delete param configs.
     *
     * @param ids config ID list
     */
    void deleteConfigList(List<Long> ids);

    /**
     * Get a param config.
     *
     * @param id config ID
     * @return param config
     */
    ConfigEntity getConfig(Long id);

    /**
     * Get a param config by key.
     *
     * @param key config key
     * @return param config
     */
    ConfigEntity getConfigByKey(String key);

    /**
     * Get a paged list of param configs.
     *
     * @param request paging conditions
     * @return paged list
     */
    PageResult<ConfigEntity> getConfigPage(ConfigPageRequest request);

}
