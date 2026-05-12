package com.focela.platform.module.system.repository.mapper.mail;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.framework.mybatis.core.util.MyBatisUtils;
import com.focela.platform.module.system.controller.admin.mail.dto.log.MailLogPageRequest;
import com.focela.platform.module.system.repository.entity.mail.MailLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailLogMapper extends BaseMapperX<MailLogEntity> {

    default PageResult<MailLogEntity> selectPage(MailLogPageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MailLogEntity>()
                .eqIfPresent(MailLogEntity::getUserId, reqVO.getUserId())
                .eqIfPresent(MailLogEntity::getUserType, reqVO.getUserType())
                .eqIfPresent(MailLogEntity::getAccountId, reqVO.getAccountId())
                .eqIfPresent(MailLogEntity::getTemplateId, reqVO.getTemplateId())
                .eqIfPresent(MailLogEntity::getSendStatus, reqVO.getSendStatus())
                .betweenIfPresent(MailLogEntity::getSendTime, reqVO.getSendTime())
                .apply(StrUtil.isNotBlank(reqVO.getToMail()),
                        MyBatisUtils.findInSet("to_mails", reqVO.getToMail()))
                .orderByDesc(MailLogEntity::getId));
    }

}
