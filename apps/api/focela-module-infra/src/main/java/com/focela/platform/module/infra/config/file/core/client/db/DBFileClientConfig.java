package com.focela.platform.module.infra.config.file.core.client.db;

import com.focela.platform.module.infra.config.file.core.client.FileClientConfig;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotEmpty;

/**
 * File client config class based on DB storage
 */
@Data
public class DBFileClientConfig implements FileClientConfig {

    /**
     * Custom domain
     */
    @NotEmpty(message = "domain must not be blank")
    @URL(message = "domain must be URL format")
    private String domain;

}
