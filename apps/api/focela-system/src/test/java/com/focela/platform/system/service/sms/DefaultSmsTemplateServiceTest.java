package com.focela.platform.system.service.sms;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.ArrayUtils;
import com.focela.platform.common.utils.object.ObjectUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.sms.request.template.SmsTemplatePageRequest;
import com.focela.platform.system.controller.admin.sms.request.template.SmsTemplateSaveRequest;
import com.focela.platform.system.domain.entity.sms.SmsChannelEntity;
import com.focela.platform.system.domain.entity.sms.SmsTemplateEntity;
import com.focela.platform.system.repository.mapper.sms.SmsTemplateMapper;
import com.focela.platform.system.enums.sms.SmsTemplateTypeEnum;
import com.focela.platform.system.config.sms.client.SmsClient;
import com.focela.platform.system.config.sms.client.response.SmsTemplateRpcResponse;
import com.focela.platform.system.config.sms.enums.SmsTemplateAuditStatusEnum;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Import(DefaultSmsTemplateService.class)
public class DefaultSmsTemplateServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultSmsTemplateService smsTemplateService;

    @Resource
    private SmsTemplateMapper smsTemplateMapper;

    @MockitoBean
    private SmsChannelService smsChannelService;
    @MockitoBean
    private SmsClient smsClient;

    @Test
    public void testFormatSmsTemplateContent() {
        // prepare parameters
        String content = "You are performing the {operation} operation, your verification code is {code}";
        Map<String, Object> params = MapUtil.<String, Object>builder("operation", "login")
                .put("code", "1234").build();

        // invoke
        String result = smsTemplateService.formatSmsTemplateContent(content, params);
        // assert
        assertEquals("You are performing the login operation, your verification code is 1234", result);
    }

    @Test
    public void testParseTemplateContentParams() {
        // prepare parameters
        String content = "You are performing the {operation} operation, your verification code is {code}";
        // mock the method

        // invoke
        List<String> params = smsTemplateService.parseTemplateContentParams(content);
        // assert
        assertEquals(Lists.newArrayList("operation", "code"), params);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateSmsTemplate_success() throws Throwable {
        // prepare parameters
        SmsTemplateSaveRequest request = randomPojo(SmsTemplateSaveRequest.class, o -> {
            o.setContent("You are performing the {operation} operation, your verification code is {code}");
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // ensure status range
            o.setType(randomEle(SmsTemplateTypeEnum.values()).getType()); // ensure type range
        }).setId(null); // prevent id from being assigned
        // mock Channel  method
        SmsChannelEntity channel = randomPojo(SmsChannelEntity.class, o -> {
            o.setId(request.getChannelId());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus()); // ensure status is enabled, creation must be in this state
        });
        when(smsChannelService.getSmsChannel(eq(channel.getId()))).thenReturn(channel);
        // mock get API SMS template succeeded
        when(smsChannelService.getSmsClient(eq(request.getChannelId()))).thenReturn(smsClient);
        when(smsClient.getSmsTemplate(eq(request.getApiTemplateId()))).thenReturn(
                randomPojo(SmsTemplateRpcResponse.class, o -> o.setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus())));

        // invoke
        Long smsTemplateId = smsTemplateService.createSmsTemplate(request);
        // assert
        assertNotNull(smsTemplateId);
        // verify record properties are correct
        SmsTemplateEntity smsTemplate = smsTemplateMapper.selectById(smsTemplateId);
        assertPojoEquals(request, smsTemplate, "id");
        assertEquals(Lists.newArrayList("operation", "code"), smsTemplate.getParams());
        assertEquals(channel.getCode(), smsTemplate.getChannelCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateSmsTemplate_success() throws Throwable {
        // mock data
        SmsTemplateEntity dbSmsTemplate = randomSmsTemplateEntity();
        smsTemplateMapper.insert(dbSmsTemplate);// @Sql: first insert an existing record
        // prepare parameters
        SmsTemplateSaveRequest request = randomPojo(SmsTemplateSaveRequest.class, o -> {
            o.setId(dbSmsTemplate.getId()); // set updated ID
            o.setContent("You are performing the {operation} operation, your verification code is {code}");
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // ensure status range
            o.setType(randomEle(SmsTemplateTypeEnum.values()).getType()); // ensure type range
        });
        // mock the method
        SmsChannelEntity channel = randomPojo(SmsChannelEntity.class, o -> {
            o.setId(request.getChannelId());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus()); // ensure status is enabled, creation must be in this state
        });
        when(smsChannelService.getSmsChannel(eq(channel.getId()))).thenReturn(channel);
        // mock get API SMS template succeeded
        when(smsChannelService.getSmsClient(eq(request.getChannelId()))).thenReturn(smsClient);
        when(smsClient.getSmsTemplate(eq(request.getApiTemplateId()))).thenReturn(
                randomPojo(SmsTemplateRpcResponse.class, o -> o.setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus())));

        // invoke
        smsTemplateService.updateSmsTemplate(request);
        // verify update is correct
        SmsTemplateEntity smsTemplate = smsTemplateMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, smsTemplate);
        assertEquals(Lists.newArrayList("operation", "code"), smsTemplate.getParams());
        assertEquals(channel.getCode(), smsTemplate.getChannelCode());
    }

    @Test
    public void testUpdateSmsTemplate_notExists() {
        // prepare parameters
        SmsTemplateSaveRequest request = randomPojo(SmsTemplateSaveRequest.class);

        // invoke and assert exception
        assertServiceException(() -> smsTemplateService.updateSmsTemplate(request), SMS_TEMPLATE_NOT_FOUND);
    }

    @Test
    public void testDeleteSmsTemplate_success() {
        // mock data
        SmsTemplateEntity dbSmsTemplate = randomSmsTemplateEntity();
        smsTemplateMapper.insert(dbSmsTemplate);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbSmsTemplate.getId();

        // invoke
        smsTemplateService.deleteSmsTemplate(id);
        // verify data no longer exists
        assertNull(smsTemplateMapper.selectById(id));
    }

    @Test
    public void testDeleteSmsTemplate_notExists() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> smsTemplateService.deleteSmsTemplate(id), SMS_TEMPLATE_NOT_FOUND);
    }

    @Test
    public void testGetSmsTemplate() {
        // mock data
        SmsTemplateEntity dbSmsTemplate = randomSmsTemplateEntity();
        smsTemplateMapper.insert(dbSmsTemplate);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbSmsTemplate.getId();

        // invoke
        SmsTemplateEntity smsTemplate = smsTemplateService.getSmsTemplate(id);
        // verify
        assertPojoEquals(dbSmsTemplate, smsTemplate);
    }

    @Test
    public void testGetSmsTemplateByCodeFromCache() {
        // mock data
        SmsTemplateEntity dbSmsTemplate = randomSmsTemplateEntity();
        smsTemplateMapper.insert(dbSmsTemplate);// @Sql: first insert an existing record
        // prepare parameters
        String code = dbSmsTemplate.getCode();

        // invoke
        SmsTemplateEntity smsTemplate = smsTemplateService.getSmsTemplateByCodeFromCache(code);
        // verify
        assertPojoEquals(dbSmsTemplate, smsTemplate);
    }

    @Test
    public void testGetSmsTemplatePage() {
        // mock data
        SmsTemplateEntity dbSmsTemplate = randomPojo(SmsTemplateEntity.class, o -> { // will be queried later
            o.setType(SmsTemplateTypeEnum.PROMOTION.getType());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCode("focela_alternate");
            o.setContent("Focelasource");
            o.setApiTemplateId("focela_sample");
            o.setChannelId(1L);
            o.setCreateTime(buildTime(2021, 11, 11));
        });
        smsTemplateMapper.insert(dbSmsTemplate);
        // test type mismatch
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setType(SmsTemplateTypeEnum.VERIFICATION_CODE.getType())));
        // test status mismatch
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // test code mismatch
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setCode("focela_secret")));
        // test content mismatch
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setContent("source")));
        // test apiTemplateId mismatch
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setApiTemplateId("nai")));
        // test channelId mismatch
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setChannelId(2L)));
        // test createTime mismatch
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setCreateTime(buildTime(2021, 12, 12))));
        // prepare parameters
        SmsTemplatePageRequest request = new SmsTemplatePageRequest();
        request.setType(SmsTemplateTypeEnum.PROMOTION.getType());
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());
        request.setCode("focela_alternate");
        request.setContent("Focela");
        request.setApiTemplateId("focela_sample");
        request.setChannelId(1L);
        request.setCreateTime(buildBetweenTime(2021, 11, 1, 2021, 12, 1));

        // invoke
        PageResult<SmsTemplateEntity> pageResult = smsTemplateService.getSmsTemplatePage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbSmsTemplate, pageResult.getList().get(0));
    }

    @Test
    public void testGetSmsTemplateCountByChannelId() {
        // mock data
        SmsTemplateEntity dbSmsTemplate = randomPojo(SmsTemplateEntity.class, o -> o.setChannelId(1L));
        smsTemplateMapper.insert(dbSmsTemplate);
        // test channelId mismatch
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setChannelId(2L)));
        // prepare parameters
        Long channelId = 1L;

        // invoke
        Long count = smsTemplateService.getSmsTemplateCountByChannelId(channelId);
        // assert
        assertEquals(1, count);
    }

    @Test
    public void testValidateSmsChannel_success() {
        // prepare parameters
        Long channelId = randomLongId();
        // mock the method
        SmsChannelEntity channel = randomPojo(SmsChannelEntity.class, o -> {
            o.setId(channelId);
            o.setStatus(CommonStatusEnum.ENABLE.getStatus()); // ensure status is enabled, creation must be in this state
        });
        when(smsChannelService.getSmsChannel(eq(channelId))).thenReturn(channel);

        // invoke
        SmsChannelEntity returnChannel = smsTemplateService.validateSmsChannel(channelId);
        // assert
        assertPojoEquals(returnChannel, channel);
    }

    @Test
    public void testValidateSmsChannel_notExists() {
        // prepare parameters
        Long channelId = randomLongId();

        // invoke, verify exception
        assertServiceException(() -> smsTemplateService.validateSmsChannel(channelId),
                SMS_CHANNEL_NOT_FOUND);
    }

    @Test
    public void testValidateSmsChannel_disable() {
        // prepare parameters
        Long channelId = randomLongId();
        // mock the method
        SmsChannelEntity channel = randomPojo(SmsChannelEntity.class, o -> {
            o.setId(channelId);
            o.setStatus(CommonStatusEnum.DISABLE.getStatus()); // ensure status is disabled, triggers failure
        });
        when(smsChannelService.getSmsChannel(eq(channelId))).thenReturn(channel);

        // invoke, verify exception
        assertServiceException(() -> smsTemplateService.validateSmsChannel(channelId),
                SMS_CHANNEL_DISABLED);
    }

    @Test
    public void testValidateDictDataValueUnique_success() {
        // invoke, succeeded
        smsTemplateService.validateSmsTemplateCodeDuplicate(randomLongId(), randomString());
    }

    @Test
    public void testValidateSmsTemplateCodeDuplicate_valueDuplicateForCreate() {
        // prepare parameters
        String code = randomString();
        // mock data
        smsTemplateMapper.insert(randomSmsTemplateEntity(o -> o.setCode(code)));

        // invoke, verify exception
        assertServiceException(() -> smsTemplateService.validateSmsTemplateCodeDuplicate(null, code),
                SMS_TEMPLATE_CODE_DUPLICATE, code);
    }

    @Test
    public void testValidateDictDataValueUnique_valueDuplicateForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String code = randomString();
        // mock data
        smsTemplateMapper.insert(randomSmsTemplateEntity(o -> o.setCode(code)));

        // invoke, verify exception
        assertServiceException(() -> smsTemplateService.validateSmsTemplateCodeDuplicate(id, code),
                SMS_TEMPLATE_CODE_DUPLICATE, code);
    }

    // ========== random object ==========

    @SafeVarargs
    private static SmsTemplateEntity randomSmsTemplateEntity(Consumer<SmsTemplateEntity>... consumers) {
        Consumer<SmsTemplateEntity> consumer = (o) -> {
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // ensure status range
            o.setType(randomEle(SmsTemplateTypeEnum.values()).getType()); // ensure type range
        };
        return randomPojo(SmsTemplateEntity.class, ArrayUtils.append(consumer, consumers));
    }

}
