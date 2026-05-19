package com.focela.platform.system.service.sms;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.sms.dto.log.SmsLogPageRequest;
import com.focela.platform.system.domain.entity.sms.SmsLogEntity;
import com.focela.platform.system.domain.entity.sms.SmsTemplateEntity;
import com.focela.platform.system.repository.mapper.sms.SmsLogMapper;
import com.focela.platform.system.enums.sms.SmsReceiveStatusEnum;
import com.focela.platform.system.enums.sms.SmsSendStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * SMS log Service implementation class
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultSmsLogService implements SmsLogService {

    private final SmsLogMapper smsLogMapper;

    @Override
    public Long createSmsLog(String mobile, Long userId, Integer userType, Boolean isSend,
                             SmsTemplateEntity template, String templateContent, Map<String, Object> templateParams) {
        SmsLogEntity.SmsLogEntityBuilder logBuilder = SmsLogEntity.builder();
        // set status based on whether we are sending
        logBuilder.sendStatus(Objects.equals(isSend, true) ? SmsSendStatusEnum.INIT.getStatus()
                : SmsSendStatusEnum.IGNORE.getStatus());
        // set mobile-related fields
        logBuilder.mobile(mobile).userId(userId).userType(userType);
        // set template-related fields
        logBuilder.templateId(template.getId()).templateCode(template.getCode()).templateType(template.getType());
        logBuilder.templateContent(templateContent).templateParams(templateParams)
                .apiTemplateId(template.getApiTemplateId());
        // set channel-related fields
        logBuilder.channelId(template.getChannelId()).channelCode(template.getChannelCode());
        // set receive-related fields
        logBuilder.receiveStatus(SmsReceiveStatusEnum.INIT.getStatus());

        // insert into database
        SmsLogEntity logEntity = logBuilder.build();
        smsLogMapper.insert(logEntity);
        return logEntity.getId();
    }

    @Override
    public void updateSmsSendResult(Long id, Boolean success,
                                    String apiSendCode, String apiSendMsg,
                                    String apiRequestId, String apiSerialNo) {
        SmsSendStatusEnum sendStatus = success ? SmsSendStatusEnum.SUCCESS : SmsSendStatusEnum.FAILURE;
        smsLogMapper.updateById(SmsLogEntity.builder().id(id)
                .sendStatus(sendStatus.getStatus()).sendTime(LocalDateTime.now())
                .apiSendCode(apiSendCode).apiSendMsg(apiSendMsg)
                .apiRequestId(apiRequestId).apiSerialNo(apiSerialNo).build());
    }

    @Override
    public void updateSmsReceiveResult(Long id, String apiSerialNo, Boolean success, LocalDateTime receiveTime,
                                       String apiReceiveCode, String apiReceiveMsg) {
        SmsReceiveStatusEnum receiveStatus = Objects.equals(success, true) ?
                SmsReceiveStatusEnum.SUCCESS : SmsReceiveStatusEnum.FAILURE;
        if (id == null || id == 0) {
            SmsLogEntity log = smsLogMapper.selectByApiSerialNo(apiSerialNo);
            if (log == null) {
                return;
            }
            id = log.getId();
        }
        smsLogMapper.updateById(SmsLogEntity.builder().id(id).receiveStatus(receiveStatus.getStatus())
                .receiveTime(receiveTime).apiReceiveCode(apiReceiveCode).apiReceiveMsg(apiReceiveMsg).build());
    }

    @Override
    public SmsLogEntity getSmsLog(Long id) {
        return smsLogMapper.selectById(id);
    }

    @Override
    public PageResult<SmsLogEntity> getSmsLogPage(SmsLogPageRequest pageRequest) {
        return smsLogMapper.selectPage(pageRequest);
    }

}
