package com.focela.platform.module.system.service.mail;

import cn.hutool.core.collection.ListUtil;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.module.system.controller.admin.mail.dto.log.MailLogPageRequest;
import com.focela.platform.module.system.repository.entity.mail.MailAccountEntity;
import com.focela.platform.module.system.repository.entity.mail.MailLogEntity;
import com.focela.platform.module.system.repository.entity.mail.MailTemplateEntity;
import com.focela.platform.module.system.repository.mapper.mail.MailLogMapper;
import com.focela.platform.module.system.enums.mail.MailSendStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

import static cn.hutool.core.exceptions.ExceptionUtil.getRootCauseMessage;

/**
 * 邮件日志 Service 实现类
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Service
@Validated
public class DefaultMailLogService implements MailLogService {

    @Resource
    private MailLogMapper mailLogMapper;

    @Override
    public PageResult<MailLogEntity> getMailLogPage(MailLogPageRequest pageVO) {
        return mailLogMapper.selectPage(pageVO);
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
        // 根据是否要发送，设置状态
        logEntityBuilder.sendStatus(Objects.equals(isSend, true) ? MailSendStatusEnum.INIT.getStatus()
                : MailSendStatusEnum.IGNORE.getStatus())
                // 用户信息
                .userId(userId).userType(userType)
                .toMails(ListUtil.toList(toMails)).ccMails(ListUtil.toList(ccMails)).bccMails(ListUtil.toList(bccMails))
                .accountId(account.getId()).fromMail(account.getMail())
                // 模板相关字段
                .templateId(template.getId()).templateCode(template.getCode()).templateNickname(template.getNickname())
                .templateTitle(template.getTitle()).templateContent(templateContent).templateParams(templateParams);

        // 插入数据库
        MailLogEntity logEntity = logEntityBuilder.build();
        mailLogMapper.insert(logEntity);
        return logEntity.getId();
    }

    @Override
    public void updateMailSendResult(Long logId, String messageId, Exception exception) {
        // 1. 成功
        if (exception == null) {
            mailLogMapper.updateById(new MailLogEntity().setId(logId).setSendTime(LocalDateTime.now())
                    .setSendStatus(MailSendStatusEnum.SUCCESS.getStatus()).setSendMessageId(messageId));
            return;
        }
        // 2. 失败
        mailLogMapper.updateById(new MailLogEntity().setId(logId).setSendTime(LocalDateTime.now())
                .setSendStatus(MailSendStatusEnum.FAILURE.getStatus()).setSendException(getRootCauseMessage(exception)));

    }

}
