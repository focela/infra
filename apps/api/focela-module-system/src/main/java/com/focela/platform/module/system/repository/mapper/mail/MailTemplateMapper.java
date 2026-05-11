package com.focela.platform.module.system.repository.mapper.mail;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.mail.vo.template.MailTemplatePageReqVO;
import com.focela.platform.module.system.repository.entity.mail.MailTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailTemplateMapper extends BaseMapperX<MailTemplateEntity> {

    default PageResult<MailTemplateEntity> selectPage(MailTemplatePageReqVO pageReqVO){
        return selectPage(pageReqVO , new LambdaQueryWrapperX<MailTemplateEntity>()
                .eqIfPresent(MailTemplateEntity::getStatus, pageReqVO.getStatus())
                .likeIfPresent(MailTemplateEntity::getCode, pageReqVO.getCode())
                .likeIfPresent(MailTemplateEntity::getName, pageReqVO.getName())
                .eqIfPresent(MailTemplateEntity::getAccountId, pageReqVO.getAccountId())
                .betweenIfPresent(MailTemplateEntity::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(MailTemplateEntity::getId));
    }

    default Long selectCountByAccountId(Long accountId) {
        return selectCount(MailTemplateEntity::getAccountId, accountId);
    }

    default MailTemplateEntity selectByCode(String code) {
        return selectOne(MailTemplateEntity::getCode, code);
    }

}
