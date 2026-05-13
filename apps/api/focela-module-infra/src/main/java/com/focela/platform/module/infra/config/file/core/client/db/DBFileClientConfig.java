package com.focela.platform.module.infra.config.file.core.client.db;

import com.focela.platform.module.infra.config.file.core.client.FileClientConfig;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotEmpty;

/**
 * 基于 DB 存储的文件客户端的配置类
 */
@Data
public class DBFileClientConfig implements FileClientConfig {

    /**
     * 自定义域名
     */
    @NotEmpty(message = "domain must not be blank")
    @URL(message = "domain must be URL format")
    private String domain;

}
