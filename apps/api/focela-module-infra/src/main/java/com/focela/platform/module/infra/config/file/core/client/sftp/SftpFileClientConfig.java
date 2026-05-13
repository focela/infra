package com.focela.platform.module.infra.config.file.core.client.sftp;

import com.focela.platform.module.infra.config.file.core.client.FileClientConfig;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * Sftp 文件客户端的配置类
 */
@Data
public class SftpFileClientConfig implements FileClientConfig {

    /**
     * 基础路径
     */
    @NotEmpty(message = "base path must not be blank")
    private String basePath;

    /**
     * 自定义域名
     */
    @NotEmpty(message = "domain must not be blank")
    @URL(message = "domain must be URL format")
    private String domain;

    /**
     * 主机地址
     */
    @NotEmpty(message = "host must not be blank")
    private String host;
    /**
     * 主机端口
     */
    @NotNull(message = "port must not be blank")
    private Integer port;
    /**
     * 用户名
     */
    @NotEmpty(message = "username must not be blank")
    private String username;
    /**
     * 密码
     */
    @NotEmpty(message = "password must not be blank")
    private String password;

}
