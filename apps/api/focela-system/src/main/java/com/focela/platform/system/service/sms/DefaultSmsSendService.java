package com.focela.platform.system.service.sms;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.core.KeyValue;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.datapermission.core.annotation.DataPermission;
import com.focela.platform.system.config.sms.client.SmsClient;
import com.focela.platform.system.config.sms.client.dto.SmsReceiveRpcResponse;
import com.focela.platform.system.config.sms.client.dto.SmsSendRpcResponse;
import com.focela.platform.system.domain.entity.sms.SmsChannelEntity;
import com.focela.platform.system.domain.entity.sms.SmsTemplateEntity;
import com.focela.platform.system.domain.entity.user.UserEntity;
import com.focela.platform.system.mq.message.sms.SmsSendMessage;
import com.focela.platform.system.mq.producer.sms.SmsProducer;
import com.focela.platform.system.service.member.MemberService;
import com.focela.platform.system.service.user.UserService;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;

/**
 * SMS send Service implementation
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultSmsSendService implements SmsSendService {

    private final UserService adminUserService;
    private final MemberService memberService;
    private final SmsChannelService smsChannelService;
    private final SmsTemplateService smsTemplateService;
    private final SmsLogService smsLogService;

    private final SmsProducer smsProducer;

    @Override
    @DataPermission(enable = false) // when sending SMS, data permission need not be considered
    public Long sendSingleSmsToAdmin(String mobile, Long userId, String templateCode, Map<String, Object> templateParams) {
        // if mobile is empty, load the mobile number corresponding to the user ID
        if (StrUtil.isEmpty(mobile)) {
            UserEntity user = adminUserService.getUser(userId);
            if (user != null) {
                mobile = user.getMobile();
            }
        }
        // perform sending
        return sendSingleSms(mobile, userId, UserTypeEnum.ADMIN.getValue(), templateCode, templateParams);
    }

    @Override
    public Long sendSingleSmsToMember(String mobile, Long userId, String templateCode, Map<String, Object> templateParams) {
        // if mobile is empty, load the mobile number corresponding to the user ID
        if (StrUtil.isEmpty(mobile)) {
            mobile = memberService.getMemberUserMobile(userId);
        }
        // perform sending
        return sendSingleSms(mobile, userId, UserTypeEnum.MEMBER.getValue(), templateCode, templateParams);
    }

    @Override
    public Long sendSingleSms(String mobile, Long userId, Integer userType,
                              String templateCode, Map<String, Object> templateParams) {
        // validate the SMS template
        SmsTemplateEntity template = validateSmsTemplate(templateCode);
        // validate the SMS channel
        SmsChannelEntity smsChannel = validateSmsChannel(template.getChannelId());

        // validate that the mobile number exists
        mobile = validateMobile(mobile);
        // build ordered template parameters. The reason this is placed here is to ensure template parameter correctness
        // up front, rather than at the point of inserting the send log.
        List<KeyValue<String, Object>> newTemplateParams = buildTemplateParams(template, templateParams);

        // create the send log. If the template is disabled, do not send the SMS; only record the log.
        Boolean isSend = CommonStatusEnum.ENABLE.getStatus().equals(template.getStatus())
                && CommonStatusEnum.ENABLE.getStatus().equals(smsChannel.getStatus());
        String content = smsTemplateService.formatSmsTemplateContent(template.getContent(), templateParams);
        Long sendLogId = smsLogService.createSmsLog(mobile, userId, userType, isSend, template, content, templateParams);

        // send MQ message; SMS is sent asynchronously
        if (isSend) {
            smsProducer.sendSmsSendMessage(sendLogId, mobile, template.getChannelId(),
                    template.getApiTemplateId(), newTemplateParams);
        }
        return sendLogId;
    }

    @VisibleForTesting
    SmsChannelEntity validateSmsChannel(Long channelId) {
        // get the SMS channel; for efficiency, fetch from cache
        SmsChannelEntity channel = smsChannelService.getSmsChannel(channelId);
        // SMS channel does not exist
        if (channel == null) {
            throw exception(SMS_CHANNEL_NOT_EXISTS);
        }
        return channel;
    }

    @VisibleForTesting
    SmsTemplateEntity validateSmsTemplate(String templateCode) {
        // get the SMS template; for efficiency, fetch from cache
        SmsTemplateEntity template = smsTemplateService.getSmsTemplateByCodeFromCache(templateCode);
        // SMS template does not exist
        if (template == null) {
            throw exception(SMS_SEND_TEMPLATE_NOT_EXISTS);
        }
        return template;
    }

    /**
     * Convert the template parameters into an ordered KeyValue array.
     * <p>
     * The reason is that some SMS platforms use array indexes rather than keys for parameters,
     * for example <a href="https://cloud.tencent.com/document/product/382/39023">Tencent Cloud</a>.
     *
     * @param template       SMS template
     * @param templateParams raw parameters
     * @return processed parameters
     */
    @VisibleForTesting
    List<KeyValue<String, Object>> buildTemplateParams(SmsTemplateEntity template, Map<String, Object> templateParams) {
        return template.getParams().stream().map(key -> {
            Object value = templateParams.get(key);
            if (value == null) {
                throw exception(SMS_SEND_MOBILE_TEMPLATE_PARAM_MISS, key);
            }
            return new KeyValue<>(key, value);
        }).collect(Collectors.toList());
    }

    @VisibleForTesting
    public String validateMobile(String mobile) {
        if (StrUtil.isEmpty(mobile)) {
            throw exception(SMS_SEND_MOBILE_NOT_EXISTS);
        }
        return mobile;
    }

    @Override
    public void doSendSms(SmsSendMessage message) {
        // get the SmsClient for the channel
        SmsClient smsClient = smsChannelService.getSmsClient(message.getChannelId());
        Assert.notNull(smsClient, "SMS client ({}) does not exist", message.getChannelId());
        // send SMS
        try {
            SmsSendRpcResponse sendResponse = smsClient.sendSms(message.getLogId(), message.getMobile(),
                    message.getApiTemplateId(), message.getTemplateParams());
            smsLogService.updateSmsSendResult(message.getLogId(), sendResponse.getSuccess(),
                    sendResponse.getApiCode(), sendResponse.getApiMsg(),
                    sendResponse.getApiRequestId(), sendResponse.getSerialNo());
        } catch (Throwable ex) {
            log.error("[doSendSms][send SMS exception, log ID ({})]", message.getLogId(), ex);
            smsLogService.updateSmsSendResult(message.getLogId(), false,
                    "EXCEPTION", ExceptionUtil.getRootCauseMessage(ex), null, null);
        }
    }

    @Override
    public void receiveSmsStatus(String channelCode, String text) throws Throwable {
        // get the SmsClient for the channel
        SmsClient smsClient = smsChannelService.getSmsClient(channelCode);
        Assert.notNull(smsClient, "SMS client ({}) does not exist", channelCode);
        // parse content
        List<SmsReceiveRpcResponse> receiveResults = smsClient.parseSmsReceiveStatus(text);
        if (CollUtil.isEmpty(receiveResults)) {
            return;
        }
        // update the receive result of the SMS log. Because volume is usually small, a for-loop update is sufficient.
        receiveResults.forEach(result -> smsLogService.updateSmsReceiveResult(result.getLogId(), result.getSerialNo(),
                result.getSuccess(), result.getReceiveTime(), result.getErrorCode(), result.getErrorMsg()));
    }

}
