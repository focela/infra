package com.focela.platform.system.service.sms;

import com.focela.platform.system.mq.message.sms.SmsSendMessage;

import java.util.List;
import java.util.Map;

/**
 * SMS send Service interface
 */
public interface SmsSendService {

    /**
     * Send a single SMS to a backoffice admin user.
     *
     * When mobile is empty, use userId to load the admin's mobile number.
     *
     * @param mobile mobile number
     * @param userId user ID
     * @param templateCode SMS template code
     * @param templateParams SMS template parameters
     * @return send log ID
     */
    Long sendSingleSmsToAdmin(String mobile, Long userId,
                              String templateCode, Map<String, Object> templateParams);

    /**
     * Send a single SMS to a user APP user.
     *
     * When mobile is empty, use userId to load the member's mobile number.
     *
     * @param mobile mobile number
     * @param userId user ID
     * @param templateCode SMS template code
     * @param templateParams SMS template parameters
     * @return send log ID
     */
    Long sendSingleSmsToMember(String mobile, Long userId,
                               String templateCode, Map<String, Object> templateParams);

    /**
     * Send a single SMS to a user
     *
     * @param mobile mobile number
     * @param userId user ID
     * @param userType user type
     * @param templateCode SMS template code
     * @param templateParams SMS template parameters
     * @return send log ID
     */
    Long sendSingleSms(String mobile, Long userId, Integer userType,
                       String templateCode, Map<String, Object> templateParams);

    default void sendBatchSms(List<String> mobiles, List<Long> userIds, Integer userType,
                              String templateCode, Map<String, Object> templateParams) {
        throw new UnsupportedOperationException("temporarily not supported this operation, if interested can implement this feature!");
    }

    /**
     * Execute the actual SMS sending.
     * Note: this method is intended for use only by the MQ Consumer.
     *
     * @param message SMS
     */
    void doSendSms(SmsSendMessage message);

    /**
     * Receive the SMS delivery result
     *
     * @param channelCode channel code
     * @param text result content
     * @throws Throwable thrown when processing fails
     */
    void receiveSmsStatus(String channelCode, String text) throws Throwable;

}
