package com.focela.platform.module.system.repository.mapper.mail;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.framework.mybatis.core.utils.MyBatisUtils;
import com.focela.platform.module.system.controller.admin.mail.dto.log.MailLogPageRequest;
import com.focela.platform.module.system.repository.entity.mail.MailLogEntity;
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
