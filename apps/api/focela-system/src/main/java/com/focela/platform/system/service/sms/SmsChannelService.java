package com.focela.platform.system.service.sms;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.sms.request.channel.SmsChannelPageRequest;
import com.focela.platform.system.controller.admin.sms.request.channel.SmsChannelSaveRequest;
import com.focela.platform.system.domain.entity.sms.SmsChannelEntity;
import com.focela.platform.system.config.sms.client.SmsClient;
import jakarta.validation.Valid;

import java.util.List;

/**
 * SMS channel Service interface
 *
 * @since 2021/1/25 9:24
 */
public interface SmsChannelService {

    /**
     * Create SMS channel
     *
     * @param createRequest create info
     * @return ID
     */
    Long createSmsChannel(@Valid SmsChannelSaveRequest createRequest);

    /**
     * Update SMS channel
     *
     * @param updateRequest update info
     */
    void updateSmsChannel(@Valid SmsChannelSaveRequest updateRequest);

    /**
     * Delete SMS channel
     *
     * @param id ID
     */
    void deleteSmsChannel(Long id);

    /**
     * Batch delete SMS channels
     *
     * @param ids ID array
     */
    void deleteSmsChannelList(List<Long> ids);

    /**
     * Get SMS channel
     *
     * @param id ID
     * @return SMS channel
     */
    SmsChannelEntity getSmsChannel(Long id);

    /**
     * Get the list of all SMS channels
     *
     * @return SMS channel list
     */
    List<SmsChannelEntity> getSmsChannelList();

    /**
     * Get SMS channel page
     *
     * @param pageRequest page query
     * @return SMS channel page
     */
    PageResult<SmsChannelEntity> getSmsChannelPage(SmsChannelPageRequest pageRequest);

    /**
     * Get SMS client
     *
     * @param id ID
     * @return SMS client
     */
    SmsClient getSmsClient(Long id);

    /**
     * Get SMS client
     *
     * @param code code
     * @return SMS client
     */
    SmsClient getSmsClient(String code);

}
