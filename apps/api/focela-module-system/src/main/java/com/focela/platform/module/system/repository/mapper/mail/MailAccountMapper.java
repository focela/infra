package com.focela.platform.module.system.repository.mapper.mail;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.mail.dto.account.MailAccountPageRequest;
import com.focela.platform.module.system.repository.entity.mail.MailAccountEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailAccountMapper extends BaseMapperX<MailAccountEntity> {

    default PageResult<MailAccountEntity> selectPage(MailAccountPageRequest pageRequest) {
        return selectPage(pageRequest, new LambdaQueryWrapperX<MailAccountEntity>()
                .likeIfPresent(MailAccountEntity::getMail, pageRequest.getMail())
                .likeIfPresent(MailAccountEntity::getUsername , pageRequest.getUsername())
                .orderByDesc(MailAccountEntity::getId));
    }

}
