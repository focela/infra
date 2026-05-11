package com.focela.platform.module.system.repository.mapper.sms;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.sms.vo.template.SmsTemplatePageReqVO;
import com.focela.platform.module.system.repository.entity.sms.SmsTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsTemplateMapper extends BaseMapperX<SmsTemplateEntity> {

    default SmsTemplateEntity selectByCode(String code) {
        return selectOne(SmsTemplateEntity::getCode, code);
    }

    default PageResult<SmsTemplateEntity> selectPage(SmsTemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SmsTemplateEntity>()
                .eqIfPresent(SmsTemplateEntity::getType, reqVO.getType())
                .eqIfPresent(SmsTemplateEntity::getStatus, reqVO.getStatus())
                .likeIfPresent(SmsTemplateEntity::getCode, reqVO.getCode())
                .likeIfPresent(SmsTemplateEntity::getContent, reqVO.getContent())
                .likeIfPresent(SmsTemplateEntity::getApiTemplateId, reqVO.getApiTemplateId())
                .eqIfPresent(SmsTemplateEntity::getChannelId, reqVO.getChannelId())
                .betweenIfPresent(SmsTemplateEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SmsTemplateEntity::getId));
    }

    default Long selectCountByChannelId(Long channelId) {
        return selectCount(SmsTemplateEntity::getChannelId, channelId);
    }

}
