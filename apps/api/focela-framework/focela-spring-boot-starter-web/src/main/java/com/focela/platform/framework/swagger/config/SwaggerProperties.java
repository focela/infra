package com.focela.platform.framework.swagger.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotEmpty;

/**
 * Swagger configuration properties
 */
@ConfigurationProperties("focela.swagger")
@Data
public class SwaggerProperties {

    /**
     * Title
     */
    @NotEmpty(message = "title must not be blank")
    private String title;
    /**
     * Description
     */
    @NotEmpty(message = "description must not be blank")
    private String description;
    /**
     * Author
     */
    @NotEmpty(message = "author must not be blank")
    private String author;
    /**
     * Version
     */
    @NotEmpty(message = "version must not be blank")
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
