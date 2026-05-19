package com.focela.platform.system.service.sms;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.sms.request.log.SmsLogPageRequest;
import com.focela.platform.system.domain.entity.sms.SmsLogEntity;
import com.focela.platform.system.domain.entity.sms.SmsTemplateEntity;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * SMS log Service interface
 *
 * @since 13:48 2021/3/2
 */
public interface SmsLogService {

    /**
     * Create SMS log
     *
     * @param mobile mobile number
     * @param userId user ID
     * @param userType user type
     * @param isSend whether to send
     * @param template SMS template
     * @param templateContent SMS content
     * @param templateParams SMS parameters
     * @return send log ID
     */
    Long createSmsLog(String mobile, Long userId, Integer userType, Boolean isSend,
                      SmsTemplateEntity template, String templateContent, Map<String, Object> templateParams);

    /**
     * Update the send result of the log
     *
     * @param id log ID
     * @param success whether the send succeeded
     * @param apiSendCode SMS API send result code
     * @param apiSendMsg SMS API send failure message
     * @param apiRequestId unique request ID returned by the SMS API
     * @param apiSerialNo serial number returned by the SMS API
     */
    void updateSmsSendResult(Long id, Boolean success,
                             String apiSendCode, String apiSendMsg,
                             String apiRequestId, String apiSerialNo);

    /**
     * Update the receive result of the log
     *
     * @param id log ID
     * @param apiSerialNo send serial number
     * @param success whether the receipt succeeded
     * @param receiveTime user receive time
     * @param apiReceiveCode API receive result code
     * @param apiReceiveMsg API receive result description
     */
    void updateSmsReceiveResult(Long id, String apiSerialNo, Boolean success,
                                LocalDateTime receiveTime, String apiReceiveCode, String apiReceiveMsg);

    /**
     * Get SMS log
     *
     * @param id log ID
     * @return SMS log
     */
    SmsLogEntity getSmsLog(Long id);

    /**
     * Get SMS log page
     *
     * @param pageRequest page query
     * @return SMS log page
     */
    PageResult<SmsLogEntity> getSmsLogPage(SmsLogPageRequest pageRequest);

}
