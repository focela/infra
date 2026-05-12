package com.focela.platform.framework.swagger.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotEmpty;

/**
 * Swagger 配置属性
 */
@ConfigurationProperties("focela.swagger")
@Data
public class SwaggerProperties {

    /**
     * 标题
     */
    @NotEmpty(message = "title must not be blank")
    private String title;
    /**
     * 描述
     */
    @NotEmpty(message = "description must not be blank")
    private String description;
    /**
     * 作者
     */
    @NotEmpty(message = "author must not be blank")
    private String author;
    /**
     * 版本
     */
    @NotEmpty(message = "版本must not be blank")
    private String version;
    /**
     * url
     */
    @NotEmpty(message = "scan package must not be blank")
    private String url;
    /**
     * email
     */
    @NotEmpty(message = "scan email must not be blank")
    private String email;

    /**
     * license
     */
    @NotEmpty(message = "scan license must not be blank")
    private String license;

    /**
     * license-url
     */
    @NotEmpty(message = "scan license-url must not be blank")
    private String licenseUrl;

}
