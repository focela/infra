package com.focela.platform.system.service.notify;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.notify.request.template.NotifyTemplatePageRequest;
import com.focela.platform.system.controller.admin.notify.request.template.NotifyTemplateSaveRequest;
import com.focela.platform.system.domain.entity.notify.NotifyTemplateEntity;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * In-site notification template Service interface
 */
public interface NotifyTemplateService {

    /**
     * Create in-site notification template
     *
     * @param createRequest create info
     * @return ID
     */
    Long createNotifyTemplate(@Valid NotifyTemplateSaveRequest createRequest);

    /**
     * Update in-site notification template
     *
     * @param updateRequest update info
     */
    void updateNotifyTemplate(@Valid NotifyTemplateSaveRequest updateRequest);

    /**
     * Delete in-site notification template
     *
     * @param id ID
     */
    void deleteNotifyTemplate(Long id);

    /**
     * Batch delete in-site notification templates
     *
     * @param ids ID list
     */
    void deleteNotifyTemplateList(List<Long> ids);

    /**
     * Get in-site notification template
     *
     * @param id ID
     * @return in-site notification template
     */
    NotifyTemplateEntity getNotifyTemplate(Long id);

    /**
     * Get in-site notification template from cache
     *
     * @param code template code
     * @return in-site notification template
     */
    NotifyTemplateEntity getNotifyTemplateByCodeFromCache(String code);

    /**
     * Get in-site notification template page
     *
     * @param pageRequest page query
     * @return in-site notification template page
     */
    PageResult<NotifyTemplateEntity> getNotifyTemplatePage(NotifyTemplatePageRequest pageRequest);

    /**
     * Format in-site notification content
     *
     * @param content template content
     * @param params content parameters
     * @return formatted content
     */
    String formatNotifyTemplateContent(String content, Map<String, Object> params);

}
