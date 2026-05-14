package com.focela.platform.framework.xss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

/**
 * XSS configuration properties
 */
@ConfigurationProperties(prefix = "focela.xss")
@Validated
@Data
public class XssProperties {

    /**
     * Whether enabled; default true
     */
    private boolean enable = true;
    /**
     * URLs to exclude; default empty
     */
    private List<String> excludeUrls = Collections.emptyList();

}
