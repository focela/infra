package com.focela.platform.system.service.sms;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.sms.dto.channel.SmsChannelPageRequest;
import com.focela.platform.system.controller.admin.sms.dto.channel.SmsChannelSaveRequest;
import com.focela.platform.system.entity.sms.SmsChannelEntity;
import com.focela.platform.system.repository.mapper.sms.SmsChannelMapper;
import com.focela.platform.system.config.sms.client.SmsClient;
import com.focela.platform.system.config.sms.client.SmsClientFactory;
import com.focela.platform.system.config.sms.property.SmsChannelProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.ErrorCodeConstants.SMS_CHANNEL_HAS_CHILDREN;
import static com.focela.platform.system.constants.ErrorCodeConstants.SMS_CHANNEL_NOT_EXISTS;

/**
 * 短信渠道 Service 实现类
 */
@Service
@Slf4j
public class DefaultSmsChannelService implements SmsChannelService {

    @Resource
    private SmsClientFactory smsClientFactory;

    @Resource
    private SmsChannelMapper smsChannelMapper;

    @Resource
    private SmsTemplateService smsTemplateService;

    @Override
    public Long createSmsChannel(SmsChannelSaveRequest createRequest) {
        SmsChannelEntity channel = BeanUtils.toBean(createRequest, SmsChannelEntity.class);
        smsChannelMapper.insert(channel);
        return channel.getId();
    }

    @Override
    public void updateSmsChannel(SmsChannelSaveRequest updateRequest) {
        // 校验存在
        validateSmsChannelExists(updateRequest.getId());
        // 更新
        SmsChannelEntity updateObj = BeanUtils.toBean(updateRequest, SmsChannelEntity.class);
        smsChannelMapper.updateById(updateObj);
    }

    @Override
    public void deleteSmsChannel(Long id) {
        // 校验存在
        validateSmsChannelExists(id);
        // 校验是否有在使用该账号的模版
        if (smsTemplateService.getSmsTemplateCountByChannelId(id) > 0) {
            throw exception(SMS_CHANNEL_HAS_CHILDREN);
        }
        // 删除
        smsChannelMapper.deleteById(id);
    }

    @Override
    public void deleteSmsChannelList(List<Long> ids) {
        // 1. 校验是否有在使用该账号的模版
        ids.forEach(id -> {
            if (smsTemplateService.getSmsTemplateCountByChannelId(id) > 0) {
                throw exception(SMS_CHANNEL_HAS_CHILDREN);
            }
        });

        // 2. 批量删除
        smsChannelMapper.deleteByIds(ids);
    }

    private SmsChannelEntity validateSmsChannelExists(Long id) {
        SmsChannelEntity channel = smsChannelMapper.selectById(id);
        if (channel == null) {
            throw exception(SMS_CHANNEL_NOT_EXISTS);
        }
        return channel;
    }

    @Override
    public SmsChannelEntity getSmsChannel(Long id) {
        return smsChannelMapper.selectById(id);
    }

    @Override
    public List<SmsChannelEntity> getSmsChannelList() {
        return smsChannelMapper.selectList();
    }

    @Override
    public PageResult<SmsChannelEntity> getSmsChannelPage(SmsChannelPageRequest pageRequest) {
        return smsChannelMapper.selectPage(pageRequest);
    }

    @Override
    public SmsClient getSmsClient(Long id) {
        SmsChannelEntity channel = smsChannelMapper.selectById(id);
        SmsChannelProperties properties = BeanUtils.toBean(channel, SmsChannelProperties.class);
        return smsClientFactory.createOrUpdateSmsClient(properties);
    }

    @Override
    public SmsClient getSmsClient(String code) {
        return smsClientFactory.getSmsClient(code);
    }

}
