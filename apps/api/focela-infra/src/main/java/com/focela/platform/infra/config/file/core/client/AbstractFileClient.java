package com.focela.platform.infra.config.file.core.client;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract file client class, provides template methods to reduce redundant code in subclasses
 */
@Slf4j
public abstract class AbstractFileClient<Config extends FileClientConfig> implements FileClient {

    /**
     * Config ID
     */
    private final Long id;
    /**
     * File config
     */
    protected Config config;
    /**
     * Original file config
     *
     * Reason: {@link #config} may be modified by subclasses, so it cannot be used to determine whether the config has changed
     * @link <a href="https://t.zsxq.com/29wkW">related case</a>
     */
    private Config originalConfig;

    public AbstractFileClient(Long id, Config config) {
        this.id = id;
        this.config = config;
        this.originalConfig = config;
    }

    /**
     * Initialize
     */
    public final void init() {
        doInit();
        log.debug("[init][config ({}) init complete]", config);
    }

    /**
     * Custom initialization
     */
    protected abstract void doInit();

    public final void refresh(Config config) {
        // Check whether updated
        if (config.equals(this.originalConfig)) {
            return;
        }
        log.info("[refresh][config ({})changed, re-init]", config);
        this.config = config;
        this.originalConfig = config;
        // Initialize
        this.init();
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Format the URL access path of a file
     * Use cases: local, ftp, db — get file content via FileController#getFile
     *
     * @param domain custom domain
     * @param path file path
     * @return URL access path
     */
    protected String formatFileUrl(String domain, String path) {
        return StrUtil.format("{}/admin-api/infra/file/{}/get/{}", domain, getId(), path);
    }

}
