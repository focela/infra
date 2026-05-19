package com.focela.platform.system.mq.producer.sms;

import com.focela.platform.common.core.KeyValue;
import com.focela.platform.system.mq.message.sms.SmsSendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Producer for SMS-related messages
 *
 * @since 2021/3/9 16:35
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsProducer {

    private final ApplicationContext applicationContext;

    /**
     * Send a {@link SmsSendMessage} message
     *
     * @param logId SMS log ID
     * @param mobile mobile number
     * @param channelId channel ID
     * @param apiTemplateId SMS template ID
     * @param templateParams SMS template parameters
     */
    public void sendSmsSendMessage(Long logId, String mobile,
                                   Long channelId, String apiTemplateId, List<KeyValue<String, Object>> templateParams) {
        SmsSendMessage message = new SmsSendMessage().setLogId(logId).setMobile(mobile);
        message.setChannelId(channelId).setApiTemplateId(apiTemplateId).setTemplateParams(templateParams);
        applicationContext.publishEvent(message);
    }

}
