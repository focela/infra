package com.focela.platform.module.system.service.sms;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.collection.ArrayUtils;
import com.focela.platform.framework.common.utils.object.ObjectUtils;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.module.system.controller.admin.sms.dto.template.SmsTemplatePageRequest;
import com.focela.platform.module.system.controller.admin.sms.dto.template.SmsTemplateSaveRequest;
import com.focela.platform.module.system.entity.sms.SmsChannelEntity;
import com.focela.platform.module.system.entity.sms.SmsTemplateEntity;
import com.focela.platform.module.system.repository.mapper.sms.SmsTemplateMapper;
import com.focela.platform.module.system.enums.sms.SmsTemplateTypeEnum;
import com.focela.platform.module.system.config.sms.core.client.SmsClient;
import com.focela.platform.module.system.config.sms.core.client.dto.SmsTemplateRespDTO;
import com.focela.platform.module.system.config.sms.core.enums.SmsTemplateAuditStatusEnum;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.utils.RandomUtils.*;
import static com.focela.platform.module.system.constants.ErrorCodeConstants.*;
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
        // 准备参数
        String content = "正在进行登录操作{operation}，您的验证码是{code}";
        Map<String, Object> params = MapUtil.<String, Object>builder("operation", "登录")
                .put("code", "1234").build();

        // 调用
        String result = smsTemplateService.formatSmsTemplateContent(content, params);
        // 断言
        assertEquals("正在进行登录操作登录，您的验证码是1234", result);
    }

    @Test
    public void testParseTemplateContentParams() {
        // 准备参数
        String content = "正在进行登录操作{operation}，您的验证码是{code}";
        // mock 方法

        // 调用
        List<String> params = smsTemplateService.parseTemplateContentParams(content);
        // 断言
        assertEquals(Lists.newArrayList("operation", "code"), params);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateSmsTemplate_success() throws Throwable {
        // 准备参数
        SmsTemplateSaveRequest request = randomPojo(SmsTemplateSaveRequest.class, o -> {
            o.setContent("正在进行登录操作{operation}，您的验证码是{code}");
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // 保证 status 的范围
            o.setType(randomEle(SmsTemplateTypeEnum.values()).getType()); // 保证 type 的 范围
        }).setId(null); // 防止 id 被赋值
        // mock Channel 的方法
        SmsChannelEntity channelDO = randomPojo(SmsChannelEntity.class, o -> {
            o.setId(request.getChannelId());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 保证 status 开启，创建必须处于这个状态
        });
        when(smsChannelService.getSmsChannel(eq(channelDO.getId()))).thenReturn(channelDO);
        // mock 获得 API 短信模板成功
        when(smsChannelService.getSmsClient(eq(request.getChannelId()))).thenReturn(smsClient);
        when(smsClient.getSmsTemplate(eq(request.getApiTemplateId()))).thenReturn(
                randomPojo(SmsTemplateRespDTO.class, o -> o.setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus())));

        // 调用
        Long smsTemplateId = smsTemplateService.createSmsTemplate(request);
        // 断言
        assertNotNull(smsTemplateId);
        // 校验记录的属性是否正确
        SmsTemplateEntity smsTemplate = smsTemplateMapper.selectById(smsTemplateId);
        assertPojoEquals(request, smsTemplate, "id");
        assertEquals(Lists.newArrayList("operation", "code"), smsTemplate.getParams());
        assertEquals(channelDO.getCode(), smsTemplate.getChannelCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateSmsTemplate_success() throws Throwable {
        // mock 数据
        SmsTemplateEntity dbSmsTemplate = randomSmsTemplateDO();
        smsTemplateMapper.insert(dbSmsTemplate);// @Sql: 先插入出一条存在的数据
        // 准备参数
        SmsTemplateSaveRequest request = randomPojo(SmsTemplateSaveRequest.class, o -> {
            o.setId(dbSmsTemplate.getId()); // 设置更新的 ID
            o.setContent("正在进行登录操作{operation}，您的验证码是{code}");
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // 保证 status 的范围
            o.setType(randomEle(SmsTemplateTypeEnum.values()).getType()); // 保证 type 的 范围
        });
        // mock 方法
        SmsChannelEntity channelDO = randomPojo(SmsChannelEntity.class, o -> {
            o.setId(request.getChannelId());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 保证 status 开启，创建必须处于这个状态
        });
        when(smsChannelService.getSmsChannel(eq(channelDO.getId()))).thenReturn(channelDO);
        // mock 获得 API 短信模板成功
        when(smsChannelService.getSmsClient(eq(request.getChannelId()))).thenReturn(smsClient);
        when(smsClient.getSmsTemplate(eq(request.getApiTemplateId()))).thenReturn(
                randomPojo(SmsTemplateRespDTO.class, o -> o.setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus())));

        // 调用
        smsTemplateService.updateSmsTemplate(request);
        // 校验是否更新正确
        SmsTemplateEntity smsTemplate = smsTemplateMapper.selectById(request.getId()); // 获取最新的
        assertPojoEquals(request, smsTemplate);
        assertEquals(Lists.newArrayList("operation", "code"), smsTemplate.getParams());
        assertEquals(channelDO.getCode(), smsTemplate.getChannelCode());
    }

    @Test
    public void testUpdateSmsTemplate_notExists() {
        // 准备参数
        SmsTemplateSaveRequest request = randomPojo(SmsTemplateSaveRequest.class);

        // 调用, 并断言异常
        assertServiceException(() -> smsTemplateService.updateSmsTemplate(request), SMS_TEMPLATE_NOT_EXISTS);
    }

    @Test
    public void testDeleteSmsTemplate_success() {
        // mock 数据
        SmsTemplateEntity dbSmsTemplate = randomSmsTemplateDO();
        smsTemplateMapper.insert(dbSmsTemplate);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbSmsTemplate.getId();

        // 调用
        smsTemplateService.deleteSmsTemplate(id);
        // 校验数据不存在了
        assertNull(smsTemplateMapper.selectById(id));
    }

    @Test
    public void testDeleteSmsTemplate_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> smsTemplateService.deleteSmsTemplate(id), SMS_TEMPLATE_NOT_EXISTS);
    }

    @Test
    public void testGetSmsTemplate() {
        // mock 数据
        SmsTemplateEntity dbSmsTemplate = randomSmsTemplateDO();
        smsTemplateMapper.insert(dbSmsTemplate);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbSmsTemplate.getId();

        // 调用
        SmsTemplateEntity smsTemplate = smsTemplateService.getSmsTemplate(id);
        // 校验
        assertPojoEquals(dbSmsTemplate, smsTemplate);
    }

    @Test
    public void testGetSmsTemplateByCodeFromCache() {
        // mock 数据
        SmsTemplateEntity dbSmsTemplate = randomSmsTemplateDO();
        smsTemplateMapper.insert(dbSmsTemplate);// @Sql: 先插入出一条存在的数据
        // 准备参数
        String code = dbSmsTemplate.getCode();

        // 调用
        SmsTemplateEntity smsTemplate = smsTemplateService.getSmsTemplateByCodeFromCache(code);
        // 校验
        assertPojoEquals(dbSmsTemplate, smsTemplate);
    }

    @Test
    public void testGetSmsTemplatePage() {
        // mock 数据
        SmsTemplateEntity dbSmsTemplate = randomPojo(SmsTemplateEntity.class, o -> { // 等会查询到
            o.setType(SmsTemplateTypeEnum.PROMOTION.getType());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCode("tudou");
            o.setContent("芋道源码");
            o.setApiTemplateId("yunai");
            o.setChannelId(1L);
            o.setCreateTime(buildTime(2021, 11, 11));
        });
        smsTemplateMapper.insert(dbSmsTemplate);
        // 测试 type 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setType(SmsTemplateTypeEnum.VERIFICATION_CODE.getType())));
        // 测试 status 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // 测试 code 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setCode("yuanma")));
        // 测试 content 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setContent("源码")));
        // 测试 apiTemplateId 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setApiTemplateId("nai")));
        // 测试 channelId 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setChannelId(2L)));
        // 测试 createTime 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setCreateTime(buildTime(2021, 12, 12))));
        // 准备参数
        SmsTemplatePageRequest request = new SmsTemplatePageRequest();
        request.setType(SmsTemplateTypeEnum.PROMOTION.getType());
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());
        request.setCode("tu");
        request.setContent("芋道");
        request.setApiTemplateId("yu");
        request.setChannelId(1L);
        request.setCreateTime(buildBetweenTime(2021, 11, 1, 2021, 12, 1));

        // 调用
        PageResult<SmsTemplateEntity> pageResult = smsTemplateService.getSmsTemplatePage(request);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbSmsTemplate, pageResult.getList().get(0));
    }

    @Test
    public void testGetSmsTemplateCountByChannelId() {
        // mock 数据
        SmsTemplateEntity dbSmsTemplate = randomPojo(SmsTemplateEntity.class, o -> o.setChannelId(1L));
        smsTemplateMapper.insert(dbSmsTemplate);
        // 测试 channelId 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setChannelId(2L)));
        // 准备参数
        Long channelId = 1L;

        // 调用
        Long count = smsTemplateService.getSmsTemplateCountByChannelId(channelId);
        // 断言
        assertEquals(1, count);
    }

    @Test
    public void testValidateSmsChannel_success() {
        // 准备参数
        Long channelId = randomLongId();
        // mock 方法
        SmsChannelEntity channelDO = randomPojo(SmsChannelEntity.class, o -> {
            o.setId(channelId);
            o.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 保证 status 开启，创建必须处于这个状态
        });
        when(smsChannelService.getSmsChannel(eq(channelId))).thenReturn(channelDO);

        // 调用
        SmsChannelEntity returnChannelDO = smsTemplateService.validateSmsChannel(channelId);
        // 断言
        assertPojoEquals(returnChannelDO, channelDO);
    }

    @Test
    public void testValidateSmsChannel_notExists() {
        // 准备参数
        Long channelId = randomLongId();

        // 调用，校验异常
        assertServiceException(() -> smsTemplateService.validateSmsChannel(channelId),
                SMS_CHANNEL_NOT_EXISTS);
    }

    @Test
    public void testValidateSmsChannel_disable() {
        // 准备参数
        Long channelId = randomLongId();
        // mock 方法
        SmsChannelEntity channelDO = randomPojo(SmsChannelEntity.class, o -> {
            o.setId(channelId);
            o.setStatus(CommonStatusEnum.DISABLE.getStatus()); // 保证 status 禁用，触发失败
        });
        when(smsChannelService.getSmsChannel(eq(channelId))).thenReturn(channelDO);

        // 调用，校验异常
        assertServiceException(() -> smsTemplateService.validateSmsChannel(channelId),
                SMS_CHANNEL_DISABLE);
    }

    @Test
    public void testValidateDictDataValueUnique_success() {
        // 调用，成功
        smsTemplateService.validateSmsTemplateCodeDuplicate(randomLongId(), randomString());
    }

    @Test
    public void testValidateSmsTemplateCodeDuplicate_valueDuplicateForCreate() {
        // 准备参数
        String code = randomString();
        // mock 数据
        smsTemplateMapper.insert(randomSmsTemplateDO(o -> o.setCode(code)));

        // 调用，校验异常
        assertServiceException(() -> smsTemplateService.validateSmsTemplateCodeDuplicate(null, code),
                SMS_TEMPLATE_CODE_DUPLICATE, code);
    }

    @Test
    public void testValidateDictDataValueUnique_valueDuplicateForUpdate() {
        // 准备参数
        Long id = randomLongId();
        String code = randomString();
        // mock 数据
        smsTemplateMapper.insert(randomSmsTemplateDO(o -> o.setCode(code)));

        // 调用，校验异常
        assertServiceException(() -> smsTemplateService.validateSmsTemplateCodeDuplicate(id, code),
                SMS_TEMPLATE_CODE_DUPLICATE, code);
    }

    // ========== 随机对象 ==========

    @SafeVarargs
    private static SmsTemplateEntity randomSmsTemplateDO(Consumer<SmsTemplateEntity>... consumers) {
        Consumer<SmsTemplateEntity> consumer = (o) -> {
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // 保证 status 的范围
            o.setType(randomEle(SmsTemplateTypeEnum.values()).getType()); // 保证 type 的 范围
        };
        return randomPojo(SmsTemplateEntity.class, ArrayUtils.append(consumer, consumers));
    }

}
