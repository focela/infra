package com.focela.platform.module.infra.framework.file.core.client.local;

import com.focela.platform.module.infra.framework.file.core.client.FileClientConfig;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotEmpty;

/**
 * 本地文件客户端的配置类
 */
@Data
public class LocalFileClientConfig implements FileClientConfig {

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

}
