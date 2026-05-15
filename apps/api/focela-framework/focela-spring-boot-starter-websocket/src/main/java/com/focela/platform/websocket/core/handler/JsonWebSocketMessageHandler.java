package com.focela.platform.websocket.core.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.focela.platform.common.utils.json.JsonUtils;
import com.focela.platform.tenant.core.utils.TenantUtils;
import com.focela.platform.websocket.core.listener.WebSocketMessageListener;
import com.focela.platform.websocket.core.message.JsonWebSocketMessage;
import com.focela.platform.websocket.core.utils.WebSocketFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * JSON-format {@link WebSocketHandler} implementation.
 *
 * Dispatches to the corresponding {@link WebSocketMessageListener} based on the {@link JsonWebSocketMessage#getType()} message type.
 */
@Slf4j
public class JsonWebSocketMessageHandler extends TextWebSocketHandler {

    /**
     * Mapping from type to WebSocketMessageListener
     */
    private final Map<String, WebSocketMessageListener<Object>> listeners = new HashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonWebSocketMessageHandler(List<? extends WebSocketMessageListener> listenersList) {
        listenersList.forEach((Consumer<WebSocketMessageListener>)
                listener -> listeners.put(listener.getType(), listener));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 1.1 empty message, skip
        if (message.getPayloadLength() == 0) {
            return;
        }
        // 1.2 ping heartbeat message; reply with pong directly.
        if (message.getPayloadLength() == 4 && Objects.equals(message.getPayload(), "ping")) {
            session.sendMessage(new TextMessage("pong"));
            return;
        }

        // 2.1 parse the message
        try {
            JsonWebSocketMessage jsonMessage = JsonUtils.parseObject(message.getPayload(), JsonWebSocketMessage.class);
            if (jsonMessage == null) {
                log.error("[handleTextMessage][session({}) message({}) parse is empty]", session.getId(), message.getPayload());
                return;
            }
            if (StrUtil.isEmpty(jsonMessage.getType())) {
                log.error("[handleTextMessage][session({}) message({}) type is empty]", session.getId(), message.getPayload());
                return;
            }
            // 2.2 get the corresponding WebSocketMessageListener
            WebSocketMessageListener<Object> messageListener = listeners.get(jsonMessage.getType());
            if (messageListener == null) {
                log.error("[handleTextMessage][session({}) message({}) listener is empty]", session.getId(), message.getPayload());
                return;
            }
            // 2.3 process the message
            Type type = TypeUtil.getTypeArgument(messageListener.getClass(), 0);
            Object messageObj = JsonUtils.parseObject(jsonMessage.getContent(), type);
            Long tenantId = WebSocketFrameworkUtils.getTenantId(session);
            TenantUtils.execute(tenantId, () -> messageListener.onMessage(session, messageObj));
        } catch (Throwable ex) {
            log.error("[handleTextMessage][session({}) message({}) process exception]", session.getId(), message.getPayload());
        }
    }

}
