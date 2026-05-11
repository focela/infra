package com.focela.platform.module.system.service.sms;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.module.system.controller.admin.sms.vo.log.SmsLogPageReqVO;
import com.focela.platform.module.system.repository.entity.sms.SmsLogEntity;
import com.focela.platform.module.system.repository.entity.sms.SmsTemplateEntity;
import com.focela.platform.module.system.repository.mapper.sms.SmsLogMapper;
import com.focela.platform.module.system.enums.sms.SmsReceiveStatusEnum;
import com.focela.platform.module.system.enums.sms.SmsSendStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * 短信日志 Service 实现类
 *
 * @author zzf
 */
@Slf4j
@Service
public class SmsLogServiceImpl implements SmsLogService {

    @Resource
    private SmsLogMapper smsLogMapper;

    @Override
    public Long createSmsLog(String mobile, Long userId, Integer userType, Boolean isSend,
                             SmsTemplateEntity template, String templateContent, Map<String, Object> templateParams) {
        SmsLogEntity.SmsLogEntityBuilder logBuilder = SmsLogEntity.builder();
        // 根据是否要发送，设置状态
        logBuilder.sendStatus(Objects.equals(isSend, true) ? SmsSendStatusEnum.INIT.getStatus()
                : SmsSendStatusEnum.IGNORE.getStatus());
        // 设置手机相关字段
        logBuilder.mobile(mobile).userId(userId).userType(userType);
        // 设置模板相关字段
        logBuilder.templateId(template.getId()).templateCode(template.getCode()).templateType(template.getType());
        logBuilder.templateContent(templateContent).templateParams(templateParams)
                .apiTemplateId(template.getApiTemplateId());
        // 设置渠道相关字段
        logBuilder.channelId(template.getChannelId()).channelCode(template.getChannelCode());
        // 设置接收相关字段
        logBuilder.receiveStatus(SmsReceiveStatusEnum.INIT.getStatus());

        // 插入数据库
        SmsLogEntity logDO = logBuilder.build();
        smsLogMapper.insert(logDO);
        return logDO.getId();
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
    public PageResult<SmsLogEntity> getSmsLogPage(SmsLogPageReqVO pageReqVO) {
        return smsLogMapper.selectPage(pageReqVO);
    }

}
