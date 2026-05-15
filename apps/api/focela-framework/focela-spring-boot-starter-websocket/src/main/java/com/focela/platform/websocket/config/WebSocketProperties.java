package com.focela.platform.websocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * WebSocket configuration properties.
 */
@ConfigurationProperties("focela.websocket")
@Data
@Validated
public class WebSocketProperties {

    /**
     * WebSocket connection path.
     */
    @NotEmpty(message = "WebSocket connection path must not be blank")
    private String path = "/ws";

    /**
     * Message sender type.
     *
     * Allowed values: local, redis, rocketmq, kafka, rabbitmq
     */
    @NotNull(message = "WebSocket message sender must not be blank")
    private String senderType = "local";

}
