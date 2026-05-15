package com.focela.platform.infra.config.file.client.db;

import com.focela.platform.infra.config.file.client.FileClientConfig;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotEmpty;

/**
 * File client config class based on DB storage
 */
@Data
public class DbFileClientConfig implements FileClientConfig {

    /**
     * Custom domain
     */
    @NotEmpty(message = "domain must not be blank")
    @URL(message = "domain must be URL format")
    private String domain;

}
