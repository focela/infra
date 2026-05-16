package com.focela.platform.system.mq.producer.mail;

import com.focela.platform.system.mq.message.mail.MailSendMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collection;

/**
 * Producer for Mail-related messages
 *
 * @since 2021/4/19 13:33
 */
@Slf4j
@Component
public class MailProducer {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * Send a {@link MailSendMessage} message
     *
     * @param sendLogId   send log ID
     * @param toMails     recipient email addresses
     * @param ccMails     cc email addresses
     * @param bccMails    bcc email addresses
     * @param accountId   email account ID
     * @param nickname    email sender
     * @param title       email title
     * @param content     email content
     * @param attachments attachments
     */
    public void sendMailSendMessage(Long sendLogId,
                                    Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                                    Long accountId, String nickname, String title, String content,
                                    File[] attachments) {
        MailSendMessage message = new MailSendMessage()
                .setLogId(sendLogId)
                .setToMails(toMails).setCcMails(ccMails).setBccMails(bccMails)
                .setAccountId(accountId).setNickname(nickname)
                .setTitle(title).setContent(content).setAttachments(attachments);
        applicationContext.publishEvent(message);
    }

}
