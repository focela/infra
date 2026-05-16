package com.focela.platform.system.mq.consumer.sms;

import com.focela.platform.system.mq.message.sms.SmsSendMessage;
import com.focela.platform.system.service.sms.SmsSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * Consumer for {@link SmsSendMessage}
 */
@Component
@Slf4j
public class SmsSendConsumer {

    @Resource
    private SmsSendService smsSendService;

    @EventListener
    @Async // Spring Event runs on the producer's sending thread by default; @Async makes it asynchronous
    public void onMessage(SmsSendMessage message) {
        log.info("[onMessage][message content ({})]", message);
        smsSendService.doSendSms(message);
    }

}
