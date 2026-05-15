package com.focela.platform.infra.config.file.core.client.local;

import com.focela.platform.infra.config.file.core.client.FileClientConfig;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotEmpty;

/**
 * Local file client config class
 */
@Data
public class LocalFileClientConfig implements FileClientConfig {

    /**
     * Base path
     */
    @NotEmpty(message = "base path must not be blank")
    private String basePath;

    /**
     * Custom domain
     */
    @NotEmpty(message = "domain must not be blank")
    @URL(message = "domain must be URL format")
    private String domain;

}
