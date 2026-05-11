package com.focela.platform.module.system.repository.mapper.mail;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.mail.vo.account.MailAccountPageReqVO;
import com.focela.platform.module.system.repository.entity.mail.MailAccountEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailAccountMapper extends BaseMapperX<MailAccountEntity> {

    default PageResult<MailAccountEntity> selectPage(MailAccountPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<MailAccountEntity>()
                .likeIfPresent(MailAccountEntity::getMail, pageReqVO.getMail())
                .likeIfPresent(MailAccountEntity::getUsername , pageReqVO.getUsername())
                .orderByDesc(MailAccountEntity::getId));
    }

}
