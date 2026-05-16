package com.focela.platform.system.service.mail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.system.entity.mail.MailAccountEntity;
import com.focela.platform.system.entity.mail.MailTemplateEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.focela.platform.system.mq.message.mail.MailSendMessage;
import com.focela.platform.system.mq.producer.mail.MailProducer;
import com.focela.platform.system.service.member.MemberService;
import com.focela.platform.system.service.user.UserService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.extra.mail.MailAccount;
import org.dromara.hutool.extra.mail.MailUtil;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.ErrorCodeConstants.*;

/**
 * Mail send Service implementation class
 *
 * @since 2022-03-21
 */
@Service
@Validated
@Slf4j
public class DefaultMailSendService implements MailSendService {

    @Resource
    private UserService adminUserService;
    @Resource
    private MemberService memberService;

    @Resource
    private MailAccountService mailAccountService;
    @Resource
    private MailTemplateService mailTemplateService;

    @Resource
    private MailLogService mailLogService;
    @Resource
    private MailProducer mailProducer;

    @Override
    public Long sendSingleMail(Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                               Long userId, Integer userType,
                               String templateCode, Map<String, Object> templateParams,
                               File... attachments) {
        // 1.1 Validate the mail template
        MailTemplateEntity template = validateMailTemplate(templateCode);
        // 1.2 Validate the mail account
        MailAccountEntity account = validateMailAccount(template.getAccountId());
        // 1.3 Validate that mail parameters are not missing
        validateTemplateParams(template, templateParams);

        // 2. Assemble the recipient lists
        String userMail = getUserMail(userId, userType);
        Collection<String> toMailSet = new LinkedHashSet<>();
        Collection<String> ccMailSet = new LinkedHashSet<>();
        Collection<String> bccMailSet = new LinkedHashSet<>();
        if (Validator.isEmail(userMail)) {
            toMailSet.add(userMail);
        }
        if (CollUtil.isNotEmpty(toMails)) {
            toMails.stream().filter(Validator::isEmail).forEach(toMailSet::add);
        }
        if (CollUtil.isNotEmpty(ccMails)) {
            ccMails.stream().filter(Validator::isEmail).forEach(ccMailSet::add);
        }
        if (CollUtil.isNotEmpty(bccMails)) {
            bccMails.stream().filter(Validator::isEmail).forEach(bccMailSet::add);
        }
        if (CollUtil.isEmpty(toMailSet)) {
            throw exception(MAIL_SEND_MAIL_NOT_EXISTS);
        }

        // Create the send log. If the template is disabled, do not send the mail, only record the log
        Boolean isSend = CommonStatusEnum.ENABLE.getStatus().equals(template.getStatus());
        String title = mailTemplateService.formatMailTemplateContent(template.getTitle(), templateParams);
        String content = mailTemplateService.formatMailTemplateContent(template.getContent(), templateParams);
        Long sendLogId = mailLogService.createMailLog(userId, userType, toMailSet, ccMailSet, bccMailSet,
                account, template, content, templateParams, isSend);
        // Send an MQ message to asynchronously send the mail
        if (isSend) {
            mailProducer.sendMailSendMessage(sendLogId, toMailSet, ccMailSet, bccMailSet,
                    account.getId(), template.getNickname(), title, content, attachments);
        }
        return sendLogId;
    }

    private String getUserMail(Long userId, Integer userType) {
        if (userId == null || userType == null) {
            return null;
        }
        if (UserTypeEnum.ADMIN.getValue().equals(userType)) {
            UserEntity user = adminUserService.getUser(userId);
            if (user != null) {
                return user.getEmail();
            }
        }
        if (UserTypeEnum.MEMBER.getValue().equals(userType)) {
            return memberService.getMemberUserEmail(userId);
        }
        return null;
    }

    @Override
    public void doSendMail(MailSendMessage message) {
        // 1. Build the sending account
        MailAccountEntity account = validateMailAccount(message.getAccountId());
        MailAccount mailAccount  = buildMailAccount(account, message.getNickname());
        // 2. Send the mail
        try {
            String messageId = MailUtil.send(mailAccount, message.getToMails(), message.getCcMails(), message.getBccMails(),
                    message.getTitle(), message.getContent(), true, message.getAttachments());
            // 3. Update result (success)
            mailLogService.updateMailSendResult(message.getLogId(), messageId, null);
        } catch (Exception e) {
            // 3. Update result (exception)
            mailLogService.updateMailSendResult(message.getLogId(), null, e);
        }
    }

    private MailAccount buildMailAccount(MailAccountEntity account, String nickname) {
        String from = StrUtil.isNotEmpty(nickname) ? nickname + " <" + account.getMail() + ">" : account.getMail();
        return new MailAccount().setFrom(from).setAuth(true)
                .setUser(account.getUsername()).setPass(account.getPassword().toCharArray())
                .setHost(account.getHost()).setPort(account.getPort())
                .setSslEnable(account.getSslEnable()).setStarttlsEnable(account.getStarttlsEnable());
    }

    @VisibleForTesting
    MailTemplateEntity validateMailTemplate(String templateCode) {
        // Get the mail template. For efficiency, fetch from cache
        MailTemplateEntity template = mailTemplateService.getMailTemplateByCodeFromCache(templateCode);
        // Mail template does not exist
        if (template == null) {
            throw exception(MAIL_TEMPLATE_NOT_EXISTS);
        }
        return template;
    }

    @VisibleForTesting
    MailAccountEntity validateMailAccount(Long accountId) {
        // Get the mail account. For efficiency, fetch from cache
        MailAccountEntity account = mailAccountService.getMailAccountFromCache(accountId);
        // Mail account does not exist
        if (account == null) {
            throw exception(MAIL_ACCOUNT_NOT_EXISTS);
        }
        return account;
    }

    /**
     * Validate that mail parameters are not missing
     *
     * @param template mail template
     * @param templateParams parameter list
     */
    @VisibleForTesting
    void validateTemplateParams(MailTemplateEntity template, Map<String, Object> templateParams) {
        template.getParams().forEach(key -> {
            Object value = templateParams.get(key);
            if (value == null) {
                throw exception(MAIL_SEND_TEMPLATE_PARAM_MISS, key);
            }
        });
    }

}
