package com.focela.platform.framework.tracer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BusinessTracer configuration class.
 */
@ConfigurationProperties("focela.tracer")
@Data
public class TracerProperties {
}
