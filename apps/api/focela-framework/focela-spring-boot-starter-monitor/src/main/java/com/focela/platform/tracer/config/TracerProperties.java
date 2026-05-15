package com.focela.platform.tracer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BusinessTracer configuration class.
 */
@ConfigurationProperties("focela.tracer")
@Data
public class TracerProperties {
}
