package com.focela.platform.infra.service.file;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.infra.controller.admin.file.request.config.FileConfigPageRequest;
import com.focela.platform.infra.controller.admin.file.request.config.FileConfigSaveRequest;
import com.focela.platform.infra.domain.entity.file.FileConfigEntity;
import com.focela.platform.infra.config.file.client.FileClient;
import jakarta.validation.Valid;

import java.util.List;

/**
 * File config Service interface
 */
public interface FileConfigService {

    /**
     * Create a file config.
     *
     * @param createRequest creation info
     * @return ID
     */
    Long createFileConfig(@Valid FileConfigSaveRequest createRequest);

    /**
     * Update a file config.
     *
     * @param updateRequest update info
     */
    void updateFileConfig(@Valid FileConfigSaveRequest updateRequest);

    /**
     * Mark a file config as Master.
     *
     * @param id ID
     */
    void updateFileConfigMaster(Long id);

    /**
     * Delete a file config.
     *
     * @param id ID
     */
    void deleteFileConfig(Long id);

    /**
     * Batch delete file configs.
     *
     * @param ids ID list
     */
    void deleteFileConfigList(List<Long> ids);

    /**
     * Get a file config.
     *
     * @param id ID
     * @return file config
     */
    FileConfigEntity getFileConfig(Long id);

    /**
     * Get a paged list of file configs.
     *
     * @param pageRequest paged query
     * @return paged file configs
     */
    PageResult<FileConfigEntity> getFileConfigPage(FileConfigPageRequest pageRequest);

    /**
     * Test the file config by uploading a file.
     *
     * @param id ID
     * @return file URL
     */
    String testFileConfig(Long id) throws Exception;

    /**
     * Get the file client for the specified config ID.
     *
     * @param id config ID
     * @return file client
     */
    FileClient getFileClient(Long id);

    /**
     * Get the Master file client.
     *
     * @return file client
     */
    FileClient getMasterFileClient();

}
