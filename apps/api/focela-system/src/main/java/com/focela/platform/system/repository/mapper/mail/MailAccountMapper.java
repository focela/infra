package com.focela.platform.system.repository.mapper.mail;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.system.controller.admin.mail.dto.account.MailAccountPageRequest;
import com.focela.platform.system.domain.entity.mail.MailAccountEntity;
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
