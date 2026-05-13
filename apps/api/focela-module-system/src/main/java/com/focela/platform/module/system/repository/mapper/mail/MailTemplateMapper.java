package com.focela.platform.module.system.repository.mapper.mail;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.mail.dto.template.MailTemplatePageRequest;
import com.focela.platform.module.system.entity.mail.MailTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailTemplateMapper extends BaseMapperX<MailTemplateEntity> {

    default PageResult<MailTemplateEntity> selectPage(MailTemplatePageRequest pageRequest){
        return selectPage(pageRequest , new LambdaQueryWrapperX<MailTemplateEntity>()
                .eqIfPresent(MailTemplateEntity::getStatus, pageRequest.getStatus())
                .likeIfPresent(MailTemplateEntity::getCode, pageRequest.getCode())
                .likeIfPresent(MailTemplateEntity::getName, pageRequest.getName())
                .eqIfPresent(MailTemplateEntity::getAccountId, pageRequest.getAccountId())
                .betweenIfPresent(MailTemplateEntity::getCreateTime, pageRequest.getCreateTime())
                .orderByDesc(MailTemplateEntity::getId));
    }

    default Long selectCountByAccountId(Long accountId) {
        return selectCount(MailTemplateEntity::getAccountId, accountId);
    }

    default MailTemplateEntity selectByCode(String code) {
        return selectOne(MailTemplateEntity::getCode, code);
    }

}
