package com.focela.platform.system.service.mail;

import cn.hutool.core.collection.ListUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.mail.dto.log.MailLogPageRequest;
import com.focela.platform.system.entity.mail.MailAccountEntity;
import com.focela.platform.system.entity.mail.MailLogEntity;
import com.focela.platform.system.entity.mail.MailTemplateEntity;
import com.focela.platform.system.repository.mapper.mail.MailLogMapper;
import com.focela.platform.system.enums.mail.MailSendStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;

import static cn.hutool.core.exceptions.ExceptionUtil.getRootCauseMessage;

/**
 * Mail Log Service implementation class
 *
 * @since 2022-03-21
 */
@Service
@Validated
@RequiredArgsConstructor
public class DefaultMailLogService implements MailLogService {

    private final MailLogMapper mailLogMapper;

    @Override
    public PageResult<MailLogEntity> getMailLogPage(MailLogPageRequest pageRequest) {
        return mailLogMapper.selectPage(pageRequest);
    }

    @Override
    public MailLogEntity getMailLog(Long id) {
        return mailLogMapper.selectById(id);
    }

    @Override
    public Long createMailLog(Long userId, Integer userType,
                              Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                              MailAccountEntity account, MailTemplateEntity template,
                              String templateContent, Map<String, Object> templateParams, Boolean isSend) {
        MailLogEntity.MailLogEntityBuilder logEntityBuilder = MailLogEntity.builder();
        // Set status based on whether to send
        logEntityBuilder.sendStatus(Objects.equals(isSend, true) ? MailSendStatusEnum.INIT.getStatus()
                : MailSendStatusEnum.IGNORE.getStatus())
                // User information
                .userId(userId).userType(userType)
                .toMails(ListUtil.toList(toMails)).ccMails(ListUtil.toList(ccMails)).bccMails(ListUtil.toList(bccMails))
                .accountId(account.getId()).fromMail(account.getMail())
                // Template-related fields
                .templateId(template.getId()).templateCode(template.getCode()).templateNickname(template.getNickname())
                .templateTitle(template.getTitle()).templateContent(templateContent).templateParams(templateParams);

        // Insert into database
        MailLogEntity logEntity = logEntityBuilder.build();
        mailLogMapper.insert(logEntity);
        return logEntity.getId();
    }

    @Override
    public void updateMailSendResult(Long logId, String messageId, Exception exception) {
        // 1. Success
        if (exception == null) {
            mailLogMapper.updateById(new MailLogEntity().setId(logId).setSendTime(LocalDateTime.now())
                    .setSendStatus(MailSendStatusEnum.SUCCESS.getStatus()).setSendMessageId(messageId));
            return;
        }
        // 2. Failure
        mailLogMapper.updateById(new MailLogEntity().setId(logId).setSendTime(LocalDateTime.now())
                .setSendStatus(MailSendStatusEnum.FAILURE.getStatus()).setSendException(getRootCauseMessage(exception)));

    }

}
