package com.focela.platform.infra.service.database;

import com.focela.platform.infra.controller.admin.database.dto.DataSourceConfigSaveRequest;
import com.focela.platform.infra.domain.entity.database.DataSourceConfigEntity;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Datasource config Service interface
 */
public interface DataSourceConfigService {

    /**
     * Create a datasource config.
     *
     * @param createRequest creation info
     * @return ID
     */
    Long createDataSourceConfig(@Valid DataSourceConfigSaveRequest createRequest);

    /**
     * Update a datasource config.
     *
     * @param updateRequest update info
     */
    void updateDataSourceConfig(@Valid DataSourceConfigSaveRequest updateRequest);

    /**
     * Delete a datasource config.
     *
     * @param id ID
     */
    void deleteDataSourceConfig(Long id);

    /**
     * Batch delete datasource configs.
     *
     * @param ids ID list
     */
    void deleteDataSourceConfigList(List<Long> ids);

    /**
     * Get a datasource config.
     *
     * @param id ID
     * @return datasource config
     */
    DataSourceConfigEntity getDataSourceConfig(Long id);

    /**
     * Get the list of datasource configs.
     *
     * @return list of datasource configs
     */
    List<DataSourceConfigEntity> getDataSourceConfigList();

}
