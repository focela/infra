package com.focela.platform.framework.websocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * WebSocket 配置项
 */
@ConfigurationProperties("focela.websocket")
@Data
@Validated
public class WebSocketProperties {

    /**
     * WebSocket 的连接路径
     */
    @NotEmpty(message = "WebSocket 连接path must not be blank")
    private String path = "/ws";

    /**
     * 消息发送器的类型
     *
     * 可选值：local、redis、rocketmq、kafka、rabbitmq
     */
    @NotNull(message = "WebSocket message send 者must not be blank")
    private String senderType = "local";

}
