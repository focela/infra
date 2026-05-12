package com.focela.platform.module.system.repository.mapper.sms;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.sms.dto.channel.SmsChannelPageRequest;
import com.focela.platform.module.system.repository.entity.sms.SmsChannelEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsChannelMapper extends BaseMapperX<SmsChannelEntity> {

    default PageResult<SmsChannelEntity> selectPage(SmsChannelPageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SmsChannelEntity>()
                .likeIfPresent(SmsChannelEntity::getSignature, reqVO.getSignature())
                .eqIfPresent(SmsChannelEntity::getStatus, reqVO.getStatus())
                .betweenIfPresent(SmsChannelEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SmsChannelEntity::getId));
    }

    default SmsChannelEntity selectByCode(String code) {
        return selectOne(SmsChannelEntity::getCode, code);
    }

}
