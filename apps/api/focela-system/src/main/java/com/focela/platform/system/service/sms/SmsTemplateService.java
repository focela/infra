package com.focela.platform.system.service.sms;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.sms.dto.template.SmsTemplatePageRequest;
import com.focela.platform.system.controller.admin.sms.dto.template.SmsTemplateSaveRequest;
import com.focela.platform.system.domain.entity.sms.SmsTemplateEntity;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * SMS template Service interface
 *
 * @since 2021/1/25 9:24
 */
public interface SmsTemplateService {

    /**
     * Create SMS template
     *
     * @param createRequest create info
     * @return ID
     */
    Long createSmsTemplate(@Valid SmsTemplateSaveRequest createRequest);

    /**
     * Update SMS template
     *
     * @param updateRequest update info
     */
    void updateSmsTemplate(@Valid SmsTemplateSaveRequest updateRequest);

    /**
     * Delete SMS template
     *
     * @param id ID
     */
    void deleteSmsTemplate(Long id);

    /**
     * Batch delete SMS templates
     *
     * @param ids ID array
     */
    void deleteSmsTemplateList(List<Long> ids);

    /**
     * Get SMS template
     *
     * @param id ID
     * @return SMS template
     */
    SmsTemplateEntity getSmsTemplate(Long id);

    /**
     * Get SMS template from cache
     *
     * @param code template code
     * @return SMS template
     */
    SmsTemplateEntity getSmsTemplateByCodeFromCache(String code);

    /**
     * Get SMS template page
     *
     * @param pageRequest page query
     * @return SMS template page
     */
    PageResult<SmsTemplateEntity> getSmsTemplatePage(SmsTemplatePageRequest pageRequest);

    /**
     * Get the SMS template count of the specified SMS channel
     *
     * @param channelId SMS channel ID
     * @return count
     */
    Long getSmsTemplateCountByChannelId(Long channelId);

    /**
     * Format SMS content
     *
     * @param content SMS template content
     * @param params content parameters
     * @return formatted content
     */
    String formatSmsTemplateContent(String content, Map<String, Object> params);

}
