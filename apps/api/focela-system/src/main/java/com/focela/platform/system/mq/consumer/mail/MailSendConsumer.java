package com.focela.platform.system.mq.consumer.mail;

import com.focela.platform.system.mq.message.mail.MailSendMessage;
import com.focela.platform.system.service.mail.MailSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * Consumer for {@link MailSendMessage}
 */
@Component
@Slf4j
public class MailSendConsumer {

    @Resource
    private MailSendService mailSendService;

    @EventListener
    @Async // Spring Event runs by default on the Producer's sending thread; @Async makes it asynchronous
    public void onMessage(MailSendMessage message) {
        log.info("[onMessage][message content ({})]", message);
        mailSendService.doSendMail(message);
    }

}
