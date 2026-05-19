package com.focela.platform.system.mq.consumer.mail;

import com.focela.platform.system.mq.message.mail.MailSendMessage;
import com.focela.platform.system.service.mail.MailSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Consumer for {@link MailSendMessage}
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MailSendConsumer {

    private final MailSendService mailSendService;

    @EventListener
    @Async // Spring Event runs by default on the Producer's sending thread; @Async makes it asynchronous
    public void onMessage(MailSendMessage message) {
        log.info("[onMessage][message content ({})]", message);
        mailSendService.doSendMail(message);
    }

}
