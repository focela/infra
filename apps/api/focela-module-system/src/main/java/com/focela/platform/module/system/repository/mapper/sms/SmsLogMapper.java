package com.focela.platform.module.system.repository.mapper.sms;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.sms.dto.log.SmsLogPageRequest;
import com.focela.platform.module.system.repository.entity.sms.SmsLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsLogMapper extends BaseMapperX<SmsLogEntity> {

    default PageResult<SmsLogEntity> selectPage(SmsLogPageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SmsLogEntity>()
                .eqIfPresent(SmsLogEntity::getChannelId, reqVO.getChannelId())
                .eqIfPresent(SmsLogEntity::getTemplateId, reqVO.getTemplateId())
                .likeIfPresent(SmsLogEntity::getMobile, reqVO.getMobile())
                .eqIfPresent(SmsLogEntity::getSendStatus, reqVO.getSendStatus())
                .betweenIfPresent(SmsLogEntity::getSendTime, reqVO.getSendTime())
                .eqIfPresent(SmsLogEntity::getReceiveStatus, reqVO.getReceiveStatus())
                .betweenIfPresent(SmsLogEntity::getReceiveTime, reqVO.getReceiveTime())
                .orderByDesc(SmsLogEntity::getId));
    }

    default SmsLogEntity selectByApiSerialNo(String apiSerialNo) {
        return selectOne(SmsLogEntity::getApiSerialNo, apiSerialNo);
    }

}
