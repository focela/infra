package com.focela.platform.system.service.sms;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.sms.dto.template.SmsTemplatePageRequest;
import com.focela.platform.system.controller.admin.sms.dto.template.SmsTemplateSaveRequest;
import com.focela.platform.system.domain.entity.sms.SmsChannelEntity;
import com.focela.platform.system.domain.entity.sms.SmsTemplateEntity;
import com.focela.platform.system.repository.mapper.sms.SmsTemplateMapper;
import com.focela.platform.system.constants.RedisKeyConstants;
import com.focela.platform.system.config.sms.client.SmsClient;
import com.focela.platform.system.config.sms.client.dto.SmsTemplateRpcResponse;
import com.focela.platform.system.config.sms.enums.SmsTemplateAuditStatusEnum;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;

/**
 * SMS template Service implementation class
 *
 * @since 2021/1/25 9:25
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultSmsTemplateService implements SmsTemplateService {

    /**
     * Regular expression matching variables inside {}
     */
    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    private final SmsTemplateMapper smsTemplateMapper;

    private final SmsChannelService smsChannelService;

    @Override
    public Long createSmsTemplate(SmsTemplateSaveRequest createRequest) {
        // validate SMS channel
        SmsChannelEntity channel = validateSmsChannel(createRequest.getChannelId());
        // validate SMS code is not duplicated
        validateSmsTemplateCodeDuplicate(null, createRequest.getCode());
        // validate SMS template
        validateApiTemplate(createRequest.getChannelId(), createRequest.getApiTemplateId());

        // insert
        SmsTemplateEntity template = BeanUtils.toBean(createRequest, SmsTemplateEntity.class);
        template.setParams(parseTemplateContentParams(template.getContent()));
        template.setChannelCode(channel.getCode());
        smsTemplateMapper.insert(template);
        // return
        return template.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.SMS_TEMPLATE,
            allEntries = true) // allEntries clears all caches because the code field may have changed, which is hard to clear
    public void updateSmsTemplate(SmsTemplateSaveRequest updateRequest) {
        // validate existence
        validateSmsTemplateExists(updateRequest.getId());
        // validate SMS channel
        SmsChannelEntity channel = validateSmsChannel(updateRequest.getChannelId());
        // validate SMS code is not duplicated
        validateSmsTemplateCodeDuplicate(updateRequest.getId(), updateRequest.getCode());
        // validate SMS template
        validateApiTemplate(updateRequest.getChannelId(), updateRequest.getApiTemplateId());

        // update
        SmsTemplateEntity updateObj = BeanUtils.toBean(updateRequest, SmsTemplateEntity.class);
        updateObj.setParams(parseTemplateContentParams(updateObj.getContent()));
        updateObj.setChannelCode(channel.getCode());
        smsTemplateMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.SMS_TEMPLATE,
            allEntries = true) // allEntries clears all caches because id is not directly the cached code, hard to clear individually
    public void deleteSmsTemplate(Long id) {
        // validate existence
        validateSmsTemplateExists(id);
        // update
        smsTemplateMapper.deleteById(id);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.SMS_TEMPLATE,
            allEntries = true) // allEntries clears all caches because id is not directly the cached code, hard to clear individually
    public void deleteSmsTemplateList(List<Long> ids) {
        smsTemplateMapper.deleteByIds(ids);
    }

    private void validateSmsTemplateExists(Long id) {
        if (smsTemplateMapper.selectById(id) == null) {
            throw exception(SMS_TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public SmsTemplateEntity getSmsTemplate(Long id) {
        return smsTemplateMapper.selectById(id);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.SMS_TEMPLATE, key = "#code",
            unless = "#result == null")
    public SmsTemplateEntity getSmsTemplateByCodeFromCache(String code) {
        return smsTemplateMapper.selectByCode(code);
    }

    @Override
    public PageResult<SmsTemplateEntity> getSmsTemplatePage(SmsTemplatePageRequest pageRequest) {
        return smsTemplateMapper.selectPage(pageRequest);
    }

    @Override
    public Long getSmsTemplateCountByChannelId(Long channelId) {
        return smsTemplateMapper.selectCountByChannelId(channelId);
    }

    @VisibleForTesting
    public SmsChannelEntity validateSmsChannel(Long channelId) {
        SmsChannelEntity channel = smsChannelService.getSmsChannel(channelId);
        if (channel == null) {
            throw exception(SMS_CHANNEL_NOT_EXISTS);
        }
        if (CommonStatusEnum.isDisable(channel.getStatus())) {
            throw exception(SMS_CHANNEL_DISABLE);
        }
        return channel;
    }

    @VisibleForTesting
    public void validateSmsTemplateCodeDuplicate(Long id, String code) {
        SmsTemplateEntity template = smsTemplateMapper.selectByCode(code);
        if (template == null) {
            return;
        }
        // if id is null, no need to compare against a dictionary type with the same id
        if (id == null) {
            throw exception(SMS_TEMPLATE_CODE_DUPLICATE, code);
        }
        if (!template.getId().equals(id)) {
            throw exception(SMS_TEMPLATE_CODE_DUPLICATE, code);
        }
    }

    /**
     * Validate whether the API SMS platform template is valid
     *
     * @param channelId channel ID
     * @param apiTemplateId API template ID
     */
    @VisibleForTesting
    void validateApiTemplate(Long channelId, String apiTemplateId) {
        // get the SMS template
        SmsClient smsClient = smsChannelService.getSmsClient(channelId);
        Assert.notNull(smsClient, String.format("SMS client (%d) does not exist", channelId));
        SmsTemplateRpcResponse template;
        try {
            template = smsClient.getSmsTemplate(apiTemplateId);
        } catch (Throwable ex) {
            throw exception(SMS_TEMPLATE_API_ERROR, ExceptionUtil.getRootCauseMessage(ex));
        }
        // validate the SMS template
        if (template == null) {
            throw exception(SMS_TEMPLATE_API_NOT_FOUND);
        }
        if (Objects.equals(template.getAuditStatus(), SmsTemplateAuditStatusEnum.CHECKING.getStatus())) {
            throw exception(SMS_TEMPLATE_API_AUDIT_CHECKING);
        }
        if (Objects.equals(template.getAuditStatus(), SmsTemplateAuditStatusEnum.FAIL.getStatus())) {
            throw exception(SMS_TEMPLATE_API_AUDIT_FAIL, template.getAuditReason());
        }
        Assert.equals(template.getAuditStatus(), SmsTemplateAuditStatusEnum.SUCCESS.getStatus(),
                String.format("SMS template (%s) approval status (%d) is invalid", apiTemplateId, template.getAuditStatus()));
    }

    @Override
    public String formatSmsTemplateContent(String content, Map<String, Object> params) {
        return StrUtil.format(content, params);
    }

    @VisibleForTesting
    List<String> parseTemplateContentParams(String content) {
        return ReUtil.findAllGroup1(PATTERN_PARAMS, content);
    }

}
