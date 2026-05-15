package com.focela.platform.system.config.sms.client;

import com.focela.platform.system.config.sms.property.SmsChannelProperties;

/**
 * SMS client factory interface
 *
 * @since 2021/1/28 14:01
 */
public interface SmsClientFactory {

    /**
     * Get the SMS Client
     *
     * @param channelId channel ID
     * @return SMS Client
     */
    SmsClient getSmsClient(Long channelId);

    /**
     * Get the SMS Client
     *
     * @param channelCode channel code
     * @return SMS Client
     */
    SmsClient getSmsClient(String channelCode);

    /**
     * Create the SMS Client
     *
     * @param properties configuration object
     * @return SMS Client
     */
    SmsClient createOrUpdateSmsClient(SmsChannelProperties properties);

}
