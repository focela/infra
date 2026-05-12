package com.focela.platform.module.system.repository.mapper.sms;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.sms.dto.template.SmsTemplatePageRequest;
import com.focela.platform.module.system.repository.entity.sms.SmsTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsTemplateMapper extends BaseMapperX<SmsTemplateEntity> {

    default SmsTemplateEntity selectByCode(String code) {
        return selectOne(SmsTemplateEntity::getCode, code);
    }

    default PageResult<SmsTemplateEntity> selectPage(SmsTemplatePageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<SmsTemplateEntity>()
                .eqIfPresent(SmsTemplateEntity::getType, request.getType())
                .eqIfPresent(SmsTemplateEntity::getStatus, request.getStatus())
                .likeIfPresent(SmsTemplateEntity::getCode, request.getCode())
                .likeIfPresent(SmsTemplateEntity::getContent, request.getContent())
                .likeIfPresent(SmsTemplateEntity::getApiTemplateId, request.getApiTemplateId())
                .eqIfPresent(SmsTemplateEntity::getChannelId, request.getChannelId())
                .betweenIfPresent(SmsTemplateEntity::getCreateTime, request.getCreateTime())
                .orderByDesc(SmsTemplateEntity::getId));
    }

    default Long selectCountByChannelId(Long channelId) {
        return selectCount(SmsTemplateEntity::getChannelId, channelId);
    }

}
