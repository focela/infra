package com.focela.platform.system.mq.message.mail;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.File;
import java.util.Collection;

/**
 * Email send message
 */
@Data
public class MailSendMessage {

    /**
     * Email log ID
     */
    @NotNull(message = "email log ID must not be blank")
    private Long logId;
    /**
     * Recipient email addresses
     */
    @NotEmpty(message = "receive email address must not be blank")
    private Collection<String> toMails;
    /**
     * CC email addresses
     */
    private Collection<String> ccMails;
    /**
     * BCC email addresses
     */
    private Collection<String> bccMails;
    /**
     * Email account ID
     */
    @NotNull(message = "email account ID must not be blank")
    private Long accountId;

    /**
     * Email sender nickname
     */
    private String nickname;
    /**
     * Email title
     */
    @NotEmpty(message = "email title must not be blank")
    private String title;
    /**
     * Email content
     */
    @NotEmpty(message = "email content must not be blank")
    private String content;

    /**
     * Attachments
     */
    private File[] attachments;

}
