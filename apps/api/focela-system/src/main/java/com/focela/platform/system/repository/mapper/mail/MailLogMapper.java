package com.focela.platform.system.repository.mapper.mail;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.mybatis.core.utils.MyBatisUtils;
import com.focela.platform.system.controller.admin.mail.request.log.MailLogPageRequest;
import com.focela.platform.system.domain.entity.mail.MailLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailLogMapper extends BaseMapperX<MailLogEntity> {

    default PageResult<MailLogEntity> selectPage(MailLogPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<MailLogEntity>()
                .eqIfPresent(MailLogEntity::getUserId, request.getUserId())
                .eqIfPresent(MailLogEntity::getUserType, request.getUserType())
                .eqIfPresent(MailLogEntity::getAccountId, request.getAccountId())
                .eqIfPresent(MailLogEntity::getTemplateId, request.getTemplateId())
                .eqIfPresent(MailLogEntity::getSendStatus, request.getSendStatus())
                .betweenIfPresent(MailLogEntity::getSendTime, request.getSendTime())
                .apply(StrUtil.isNotBlank(request.getToMail()),
                        MyBatisUtils.findInSet("to_mails", request.getToMail()))
                .orderByDesc(MailLogEntity::getId));
    }

}
