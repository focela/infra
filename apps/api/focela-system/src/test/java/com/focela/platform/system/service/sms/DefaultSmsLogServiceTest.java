package com.focela.platform.system.service.sms;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.collection.ArrayUtils;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
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
import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.framework.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.RandomUtils.*;
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
        // mock 数据
        SmsLogEntity dbSmsLog = randomSmsLogDO(o -> { // 等会查询到
            o.setChannelId(1L);
            o.setTemplateId(10L);
            o.setMobile("15601691300");
            o.setSendStatus(SmsSendStatusEnum.INIT.getStatus());
            o.setSendTime(buildTime(2020, 11, 11));
            o.setReceiveStatus(SmsReceiveStatusEnum.INIT.getStatus());
            o.setReceiveTime(buildTime(2021, 11, 11));
        });
        smsLogMapper.insert(dbSmsLog);
        // 测试 channelId 不匹配
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setChannelId(2L)));
        // 测试 templateId 不匹配
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setTemplateId(20L)));
        // 测试 mobile 不匹配
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setMobile("18818260999")));
        // 测试 sendStatus 不匹配
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setSendStatus(SmsSendStatusEnum.IGNORE.getStatus())));
        // 测试 sendTime 不匹配
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setSendTime(buildTime(2020, 12, 12))));
        // 测试 receiveStatus 不匹配
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setReceiveStatus(SmsReceiveStatusEnum.SUCCESS.getStatus())));
        // 测试 receiveTime 不匹配
        smsLogMapper.insert(cloneIgnoreId(dbSmsLog, o -> o.setReceiveTime(buildTime(2021, 12, 12))));
        // 准备参数
        SmsLogPageRequest request = new SmsLogPageRequest();
        request.setChannelId(1L);
        request.setTemplateId(10L);
        request.setMobile("156");
        request.setSendStatus(SmsSendStatusEnum.INIT.getStatus());
        request.setSendTime(buildBetweenTime(2020, 11, 1, 2020, 11, 30));
        request.setReceiveStatus(SmsReceiveStatusEnum.INIT.getStatus());
        request.setReceiveTime(buildBetweenTime(2021, 11, 1, 2021, 11, 30));

        // 调用
        PageResult<SmsLogEntity> pageResult = smsLogService.getSmsLogPage(request);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbSmsLog, pageResult.getList().get(0));
    }

    @Test
    public void testCreateSmsLog() {
        // 准备参数
        String mobile = randomString();
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        Boolean isSend = randomBoolean();
        SmsTemplateEntity templateDO = randomPojo(SmsTemplateEntity.class,
                o -> o.setType(randomEle(SmsTemplateTypeEnum.values()).getType()));
        String templateContent = randomString();
        Map<String, Object> templateParams = randomTemplateParams();
        // mock 方法

        // 调用
        Long logId = smsLogService.createSmsLog(mobile, userId, userType, isSend,
                templateDO, templateContent, templateParams);
        // 断言
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
        // mock 数据
        SmsLogEntity dbSmsLog = randomSmsLogDO(
                o -> o.setSendStatus(SmsSendStatusEnum.IGNORE.getStatus()));
        smsLogMapper.insert(dbSmsLog);
        // 准备参数
        Long id = dbSmsLog.getId();
        Boolean success = randomBoolean();
        String apiSendCode = randomString();
        String apiSendMsg = randomString();
        String apiRequestId = randomString();
        String apiSerialNo = randomString();

        // 调用
        smsLogService.updateSmsSendResult(id, success,
                apiSendCode, apiSendMsg, apiRequestId, apiSerialNo);
        // 断言
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
        // mock 数据
        SmsLogEntity dbSmsLog = randomSmsLogDO(
                o -> o.setReceiveStatus(SmsReceiveStatusEnum.INIT.getStatus()));
        smsLogMapper.insert(dbSmsLog);
        // 准备参数
        Long id = dbSmsLog.getId();
        String apiSerialNo = dbSmsLog.getApiSerialNo();
        Boolean success = randomBoolean();
        LocalDateTime receiveTime = randomLocalDateTime();
        String apiReceiveCode = randomString();
        String apiReceiveMsg = randomString();

        // 调用
        smsLogService.updateSmsReceiveResult(id, apiSerialNo, success, receiveTime, apiReceiveCode, apiReceiveMsg);
        // 断言
        dbSmsLog = smsLogMapper.selectById(id);
        assertEquals(success ? SmsReceiveStatusEnum.SUCCESS.getStatus()
                : SmsReceiveStatusEnum.FAILURE.getStatus(), dbSmsLog.getReceiveStatus());
        assertEquals(receiveTime, dbSmsLog.getReceiveTime());
        assertEquals(apiReceiveCode, dbSmsLog.getApiReceiveCode());
        assertEquals(apiReceiveMsg, dbSmsLog.getApiReceiveMsg());
    }

    // ========== 随机对象 ==========

    @SafeVarargs
    private static SmsLogEntity randomSmsLogDO(Consumer<SmsLogEntity>... consumers) {
        Consumer<SmsLogEntity> consumer = (o) -> {
            o.setTemplateParams(randomTemplateParams());
            o.setTemplateType(randomEle(SmsTemplateTypeEnum.values()).getType()); // 保证 templateType 的范围
            o.setUserType(randomEle(UserTypeEnum.values()).getValue()); // 保证 userType 的范围
            o.setSendStatus(randomEle(SmsSendStatusEnum.values()).getStatus()); // 保证 sendStatus 的范围
            o.setReceiveStatus(randomEle(SmsReceiveStatusEnum.values()).getStatus()); // 保证 receiveStatus 的范围
        };
        return randomPojo(SmsLogEntity.class, ArrayUtils.append(consumer, consumers));
    }

    private static Map<String, Object> randomTemplateParams() {
        return MapUtil.<String, Object>builder().put(randomString(), randomString())
                .put(randomString(), randomString()).build();
    }
}
