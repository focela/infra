package com.focela.platform.system.service.mail;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.mail.dto.template.MailTemplatePageRequest;
import com.focela.platform.system.controller.admin.mail.dto.template.MailTemplateSaveRequest;
import com.focela.platform.system.entity.mail.MailTemplateEntity;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * Mail Template Service interface
 *
 * @since 2022-03-21
 */
public interface MailTemplateService {

    /**
     * Create a mail template
     *
     * @param createRequest mail information
     * @return ID
     */
    Long createMailTemplate(@Valid MailTemplateSaveRequest createRequest);

    /**
     * Update a mail template
     *
     * @param updateRequest mail information
     */
    void updateMailTemplate(@Valid MailTemplateSaveRequest updateRequest);

    /**
     * Delete a mail template
     *
     * @param id ID
     */
    void deleteMailTemplate(Long id);

    /**
     * Batch delete mail templates
     *
     * @param ids ID list
     */
    void deleteMailTemplateList(List<Long> ids);

    /**
     * Get a mail template
     *
     * @param id ID
     * @return mail template
     */
    MailTemplateEntity getMailTemplate(Long id);

    /**
     * Get the paginated mail templates
     *
     * @param pageRequest template information
     * @return paginated mail templates
     */
    PageResult<MailTemplateEntity> getMailTemplatePage(MailTemplatePageRequest pageRequest);

    /**
     * Get the mail template list
     *
     * @return template list
     */
    List<MailTemplateEntity> getMailTemplateList();

    /**
     * Get a mail template from cache
     *
     * @param code template code
     * @return mail template
     */
    MailTemplateEntity getMailTemplateByCodeFromCache(String code);

    /**
     * Format the mail template content
     *
     * @param content mail template
     * @param params parameters to merge
     * @return formatted content
     */
    String formatMailTemplateContent(String content, Map<String, Object> params);

    /**
     * Get the number of mail templates under the specified mail account
     *
     * @param accountId account ID
     * @return count
     */
    long getMailTemplateCountByAccountId(Long accountId);

}
