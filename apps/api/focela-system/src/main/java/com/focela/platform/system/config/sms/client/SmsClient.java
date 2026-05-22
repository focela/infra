package com.focela.platform.system.config.sms.client;

import com.focela.platform.common.core.KeyValue;
import com.focela.platform.system.config.sms.client.response.SmsReceiveRpcResponse;
import com.focela.platform.system.config.sms.client.response.SmsSendRpcResponse;
import com.focela.platform.system.config.sms.client.response.SmsTemplateRpcResponse;

import java.util.List;

/**
 * SMS client, used to integrate SDKs of various SMS platforms and provide features such as SMS sending
 *
 * @since 2021/1/25 14:14
 */
public interface SmsClient {

    /**
     * Get the channel ID
     *
     * @return channel ID
     */
    Long getId();

    /**
     * Send message
     *
     * @param logId          log ID
     * @param mobile         mobile number
     * @param apiTemplateId  template ID of the SMS API
     * @param templateParams SMS template parameters; using a List to preserve parameter order
     * @return SMS sending result
     */
    SmsSendRpcResponse sendSms(Long logId, String mobile, String apiTemplateId,
                           List<KeyValue<String, Object>> templateParams) throws Throwable;

    /**
     * Parse the SMS receive result
     *
     * @param text result
     * @return result content
     * @throws Throwable thrown when parsing the text fails
     */
    List<SmsReceiveRpcResponse> parseSmsReceiveStatus(String text) throws Throwable;

    /**
     * Query the specified SMS template
     *
     * Returns null if the query fails
     *
     * @param apiTemplateId template ID of the SMS API
     * @return SMS template
     */
    SmsTemplateRpcResponse getSmsTemplate(String apiTemplateId) throws Throwable;

}
