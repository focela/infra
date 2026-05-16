package com.focela.platform.system.service.mail;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.mail.dto.account.MailAccountPageRequest;
import com.focela.platform.system.controller.admin.mail.dto.account.MailAccountSaveRequest;
import com.focela.platform.system.entity.mail.MailAccountEntity;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Mail account Service interface
 *
 * @since 2022-03-21
 */
public interface MailAccountService {

    /**
     * Create a mail account
     *
     * @param createRequest mail account info
     * @return ID
     */
    Long createMailAccount(@Valid MailAccountSaveRequest createRequest);

    /**
     * Update a mail account
     *
     * @param updateRequest mail account info
     */
    void updateMailAccount(@Valid MailAccountSaveRequest updateRequest);

    /**
     * Delete a mail account
     *
     * @param id ID
     */
    void deleteMailAccount(Long id);

    /**
     * Batch delete mail accounts
     *
     * @param ids ID list
     */
    void deleteMailAccountList(List<Long> ids);

    /**
     * Get mail account info
     *
     * @param id ID
     * @return mail account info
     */
    MailAccountEntity getMailAccount(Long id);

    /**
     * Get mail account from cache
     *
     * @param id ID
     * @return mail account
     */
    MailAccountEntity getMailAccountFromCache(Long id);

    /**
     * Get paginated mail account info
     *
     * @param pageRequest mail account pagination parameters
     * @return paginated mail account info
     */
    PageResult<MailAccountEntity> getMailAccountPage(MailAccountPageRequest pageRequest);

    /**
     * Get list of mail accounts
     *
     * @return mail account list
     */
    List<MailAccountEntity> getMailAccountList();

}
