package com.focela.platform.module.infra.config.file.core.client.sftp;

import com.focela.platform.module.infra.config.file.core.client.FileClientConfig;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * Sftp file client config class
 */
@Data
public class SftpFileClientConfig implements FileClientConfig {

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

    /**
     * Host address
     */
    @NotEmpty(message = "host must not be blank")
    private String host;
    /**
     * Host port
     */
    @NotNull(message = "port must not be blank")
    private Integer port;
    /**
     * Username
     */
    @NotEmpty(message = "username must not be blank")
    private String username;
    /**
     * Password
     */
    @NotEmpty(message = "password must not be blank")
    private String password;

}
