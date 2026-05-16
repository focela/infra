package com.focela.platform.system.service.sms;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.ArrayUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.sms.dto.log.SmsLogPageRequest;
import com.focela.platform.system.entity.sms.SmsLogEntity;
import com.focela.platform.system.entity.sms.SmsTemplateEntity;
import com.focela.platform.system.repository.mapper.sms.SmsLogMapper;
import com.focela.platform.system.enums.sms.SmsReceiveStatusEnum;
import com.focela.platform.system.enums.sms.SmsSendStatusEnum;
import com.focela.platform.system.enums.sms.SmsTemplateTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Consumer;

import static cn.hutool.core.util.RandomUtil.randomBoolean;
import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(DefaultSmsLogService.class)
public class DefaultSmsLogServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultSmsLogService smsLogService;

    @Resource
    private SmsLogMapper smsLogMapper;

    @Test
    public void testGetSmsLogPage() {
        // mock data
        SmsLogEntity dbSmsLog = randomSmsLogDO(o -> { // will be queried later
            o.setChannelId(1L);
            o.setTemplateId(10L);
            o.setMobile("15601691300");
            o.setSendStatus(SmsSendStatusEnum.INIT.getStatus());
            o.setSendTime(buildTime(2020, 11, 11));
            o.setReceiveStatus(SmsReceiveStatusEnum.INIT.getStatus());
            o.setReceiveTime(buildTime(2021, 11, 11));
        });
        smsLogMapper.insert(dbSmsLog);
        // test channelId mismatch
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setChannelId(2L)));
        // test templateId mismatch
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setTemplateId(20L)));
        // test mobile mismatch
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setMobile("18818260999")));
        // test sendStatus mismatch
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setSendStatus(SmsSendStatusEnum.IGNORE.getStatus())));
        // test sendTime mismatch
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setSendTime(buildTime(2020, 12, 12))));
        // test receiveStatus mismatch
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setReceiveStatus(SmsReceiveStatusEnum.SUCCESS.getStatus())));
        // test receiveTime mismatch
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setReceiveTime(buildTime(2021, 12, 12))));
        // prepare parameters
        SmsLogPageRequest request = new SmsLogPageRequest();
        request.setChannelId(1L);
        request.setTemplateId(10L);
        request.setMobile("156");
        request.setSendStatus(SmsSendStatusEnum.INIT.getStatus());
        request.setSendTime(buildBetweenTime(2020, 11, 1, 2020, 11, 30));
        request.setReceiveStatus(SmsReceiveStatusEnum.INIT.getStatus());
        request.setReceiveTime(buildBetweenTime(2021, 11, 1, 2021, 11, 30));

        // invoke
        PageResult<SmsLogEntity> pageResult = smsLogService.getSmsLogPage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbSmsLog, pageResult.getList().get(0));
    }

    @Test
    public void testCreateSmsLog() {
        // prepare parameters
        String mobile = randomString();
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        Boolean isSend = randomBoolean();
        SmsTemplateEntity templateDO = randomPojo(SmsTemplateEntity.class,
                o -> o.setType(randomEle(SmsTemplateTypeEnum.values()).getType()));
        String templateContent = randomString();
        Map<String, Object> templateParams = randomTemplateParams();
        // mock the method

        // invoke
        Long logId = smsLogService.createSmsLog(mobile, userId, userType, isSend,
                templateDO, templateContent, templateParams);
        // assert
        SmsLogEntity logEntity = smsLogMapper.selectById(logId);
        assertEquals(isSend ? SmsSendStatusEnum.INIT.getStatus() : SmsSendStatusEnum.IGNORE.getStatus(),
                logEntity.getSendStatus());
        assertEquals(mobile, logEntity.getMobile());
        assertEquals(userType, logEntity.getUserType());
        assertEquals(userId, logEntity.getUserId());
        assertEquals(templateDO.getId(), logEntity.getTemplateId());
        assertEquals(templateDO.getCode(), logEntity.getTemplateCode());
        assertEquals(templateDO.getType(), logEntity.getTemplateType());
        assertEquals(templateDO.getChannelId(), logEntity.getChannelId());
        assertEquals(templateDO.getChannelCode(), logEntity.getChannelCode());
        assertEquals(templateContent, logEntity.getTemplateContent());
        assertEquals(templateParams, logEntity.getTemplateParams());
        assertEquals(SmsReceiveStatusEnum.INIT.getStatus(), logEntity.getReceiveStatus());
    }

    @Test
    public void testUpdateSmsSendResult() {
        // mock data
        SmsLogEntity dbSmsLog = randomSmsLogDO(
                o -> o.setSendStatus(SmsSendStatusEnum.IGNORE.getStatus()));
        smsLogMapper.insert(dbSmsLog);
        // prepare parameters
        Long id = dbSmsLog.getId();
        Boolean success = randomBoolean();
        String apiSendCode = randomString();
        String apiSendMsg = randomString();
        String apiRequestId = randomString();
        String apiSerialNo = randomString();

        // invoke
        smsLogService.updateSmsSendResult(id, success,
                apiSendCode, apiSendMsg, apiRequestId, apiSerialNo);
        // assert
        dbSmsLog = smsLogMapper.selectById(id);
        assertEquals(success ? SmsSendStatusEnum.SUCCESS.getStatus() : SmsSendStatusEnum.FAILURE.getStatus(),
                dbSmsLog.getSendStatus());
        assertNotNull(dbSmsLog.getSendTime());
        assertEquals(apiSendCode, dbSmsLog.getApiSendCode());
        assertEquals(apiSendMsg, dbSmsLog.getApiSendMsg());
        assertEquals(apiRequestId, dbSmsLog.getApiRequestId());
        assertEquals(apiSerialNo, dbSmsLog.getApiSerialNo());
    }

    @Test
    public void testUpdateSmsReceiveResult() {
        // mock data
        SmsLogEntity dbSmsLog = randomSmsLogDO(
                o -> o.setReceiveStatus(SmsReceiveStatusEnum.INIT.getStatus()));
        smsLogMapper.insert(dbSmsLog);
        // prepare parameters
        Long id = dbSmsLog.getId();
        String apiSerialNo = dbSmsLog.getApiSerialNo();
        Boolean success = randomBoolean();
        LocalDateTime receiveTime = randomLocalDateTime();
        String apiReceiveCode = randomString();
        String apiReceiveMsg = randomString();

        // invoke
        smsLogService.updateSmsReceiveResult(id, apiSerialNo, success, receiveTime, apiReceiveCode, apiReceiveMsg);
        // assert
        dbSmsLog = smsLogMapper.selectById(id);
        assertEquals(success ? SmsReceiveStatusEnum.SUCCESS.getStatus()
                : SmsReceiveStatusEnum.FAILURE.getStatus(), dbSmsLog.getReceiveStatus());
        assertEquals(receiveTime, dbSmsLog.getReceiveTime());
        assertEquals(apiReceiveCode, dbSmsLog.getApiReceiveCode());
        assertEquals(apiReceiveMsg, dbSmsLog.getApiReceiveMsg());
    }

    // ========== random object ==========

    @SafeVarargs
    private static SmsLogEntity randomSmsLogDO(Consumer<SmsLogEntity>... consumers) {
        Consumer<SmsLogEntity> consumer = (o) -> {
            o.setTemplateParams(randomTemplateParams());
            o.setTemplateType(randomEle(SmsTemplateTypeEnum.values()).getType()); // ensure templateType range
            o.setUserType(randomEle(UserTypeEnum.values()).getValue()); // ensure userType range
            o.setSendStatus(randomEle(SmsSendStatusEnum.values()).getStatus()); // ensure sendStatus range
            o.setReceiveStatus(randomEle(SmsReceiveStatusEnum.values()).getStatus()); // ensure receiveStatus range
        };
        return randomPojo(SmsLogEntity.class, ArrayUtils.append(consumer, consumers));
    }

    private static Map<String, Object> randomTemplateParams() {
        return MapUtil.<String, Object>builder().put(randomString(), randomString())
                .put(randomString(), randomString()).build();
    }
}
