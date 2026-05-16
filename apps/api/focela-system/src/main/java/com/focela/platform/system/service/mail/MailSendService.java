package com.focela.platform.system.service.mail;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.system.mq.message.mail.MailSendMessage;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * Mail send Service interface
 *
 * @since 2022-03-21
 */
public interface MailSendService {

    /**
     * Send a single mail to an admin user
     *
     * @param userId user ID
     * @param toMails recipient emails
     * @param ccMails cc emails
     * @param bccMails bcc emails
     * @param templateCode mail template code
     * @param templateParams mail template parameters
     * @param attachments attachments
     * @return send log ID
     */
    default Long sendSingleMailToAdmin(Long userId,
                                       Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                                       String templateCode, Map<String, Object> templateParams,
                                       File... attachments) {
        return sendSingleMail(toMails, ccMails, bccMails, userId, UserTypeEnum.ADMIN.getValue(),
                templateCode, templateParams, attachments);
    }

    /**
     * Send a single mail to a member (user app)
     *
     * @param userId user ID
     * @param toMails recipient emails
     * @param ccMails cc emails
     * @param bccMails bcc emails
     * @param templateCode mail template code
     * @param templateParams mail template parameters
     * @param attachments attachments
     * @return send log ID
     */
    default Long sendSingleMailToMember(Long userId,
                                        Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                                        String templateCode, Map<String, Object> templateParams,
                                        File... attachments) {
        return sendSingleMail(toMails, ccMails, bccMails, userId, UserTypeEnum.MEMBER.getValue(),
                templateCode, templateParams, attachments);
    }

    /**
     * Send a single mail
     *
     * @param toMails recipient emails
     * @param ccMails cc emails
     * @param bccMails bcc emails
     * @param userId user ID
     * @param userType user type
     * @param templateCode mail template code
     * @param templateParams mail template parameters
     * @param attachments attachments
     * @return send log ID
     */
    Long sendSingleMail(Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                        Long userId, Integer userType,
                        String templateCode, Map<String, Object> templateParams,
                        File... attachments);

    /**
     * Perform the actual mail sending.
     * Note: this method is intended only for MQ Consumer use.
     *
     * @param message mail
     */
    void doSendMail(MailSendMessage message);

}
