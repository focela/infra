package com.focela.platform.system.repository.mapper.sms;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.system.controller.admin.sms.dto.log.SmsLogPageRequest;
import com.focela.platform.system.domain.entity.sms.SmsLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsLogMapper extends BaseMapperX<SmsLogEntity> {

    default PageResult<SmsLogEntity> selectPage(SmsLogPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<SmsLogEntity>()
                .eqIfPresent(SmsLogEntity::getChannelId, request.getChannelId())
                .eqIfPresent(SmsLogEntity::getTemplateId, request.getTemplateId())
                .likeIfPresent(SmsLogEntity::getMobile, request.getMobile())
                .eqIfPresent(SmsLogEntity::getSendStatus, request.getSendStatus())
                .betweenIfPresent(SmsLogEntity::getSendTime, request.getSendTime())
                .eqIfPresent(SmsLogEntity::getReceiveStatus, request.getReceiveStatus())
                .betweenIfPresent(SmsLogEntity::getReceiveTime, request.getReceiveTime())
                .orderByDesc(SmsLogEntity::getId));
    }

    default SmsLogEntity selectByApiSerialNo(String apiSerialNo) {
        return selectOne(SmsLogEntity::getApiSerialNo, apiSerialNo);
    }

}
