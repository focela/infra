package com.focela.platform.module.system.mq.message.mail;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.File;
import java.util.Collection;

/**
 * 邮箱发送消息
 */
@Data
public class MailSendMessage {

    /**
     * 邮件日志编号
     */
    @NotNull(message = "email log ID must not be blank")
    private Long logId;
    /**
     * 接收邮件地址
     */
    @NotEmpty(message = "receive email address must not be blank")
    private Collection<String> toMails;
    /**
     * 抄送邮件地址
     */
    private Collection<String> ccMails;
    /**
     * 密送邮件地址
     */
    private Collection<String> bccMails;
    /**
     * 邮件账号编号
     */
    @NotNull(message = "email account ID must not be blank")
    private Long accountId;

    /**
     * 邮件发件人
     */
    private String nickname;
    /**
     * 邮件标题
     */
    @NotEmpty(message = "email title must not be blank")
    private String title;
    /**
     * 邮件内容
     */
    @NotEmpty(message = "email content must not be blank")
    private String content;

    /**
     * 附件
     */
    private File[] attachments;

}
