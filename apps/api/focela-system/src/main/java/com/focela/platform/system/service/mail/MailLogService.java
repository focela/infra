package com.focela.platform.system.service.mail;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.mail.request.log.MailLogPageRequest;
import com.focela.platform.system.domain.entity.mail.MailAccountEntity;
import com.focela.platform.system.domain.entity.mail.MailLogEntity;
import com.focela.platform.system.domain.entity.mail.MailTemplateEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Mail log Service interface
 *
 * @since 2022-03-21
 */
public interface MailLogService {

    /**
     * Get paginated mail logs
     *
     * @param pageRequest pagination parameters
     * @return paginated result
     */
    PageResult<MailLogEntity> getMailLogPage(MailLogPageRequest pageRequest);

    /**
     * Get the mail log with the specified ID
     *
     * @param id log ID
     * @return mail log
     */
    MailLogEntity getMailLog(Long id);

    /**
     * Create a mail log
     *
     * @param userId          user ID
     * @param userType        user type
     * @param toMails         recipient emails
     * @param ccMails         cc recipient emails
     * @param bccMails        bcc recipient emails
     * @param account         mail account info
     * @param template        template info
     * @param templateContent template content
     * @param templateParams  template parameters
     * @param isSend          whether send succeeded
     * @return log ID
     */
    Long createMailLog(Long userId, Integer userType,
                       Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                       MailAccountEntity account, MailTemplateEntity template,
                       String templateContent, Map<String, Object> templateParams, Boolean isSend);

    /**
     * Update mail send result
     *
     * @param logId  log ID
     * @param messageId message ID after sending
     * @param exception send exception
     */
    void updateMailSendResult(Long logId, String messageId, Exception exception);

}
