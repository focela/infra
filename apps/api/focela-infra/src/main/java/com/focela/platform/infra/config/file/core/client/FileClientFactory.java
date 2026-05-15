package com.focela.platform.infra.config.file.core.client;

import com.focela.platform.infra.config.file.core.enums.FileStorageEnum;

public interface FileClientFactory {

    /**
     * Get the file client
     *
     * @param configId config ID
     * @return file client
     */
    FileClient getFileClient(Long configId);

    /**
     * Create the file client
     *
     * @param configId config ID
     * @param storage storage enum {@link FileStorageEnum}
     * @param config file config
     */
    <Config extends FileClientConfig> void createOrUpdateFileClient(Long configId, Integer storage, Config config);

}
