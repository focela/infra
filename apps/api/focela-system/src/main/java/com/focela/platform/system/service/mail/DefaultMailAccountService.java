package com.focela.platform.system.service.mail;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.mail.request.account.MailAccountPageRequest;
import com.focela.platform.system.controller.admin.mail.request.account.MailAccountSaveRequest;
import com.focela.platform.system.domain.entity.mail.MailAccountEntity;
import com.focela.platform.system.repository.mapper.mail.MailAccountMapper;
import com.focela.platform.system.constants.RedisKeyConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.MAIL_ACCOUNT_NOT_EXISTS;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS;

/**
 * Mail account Service implementation class
 *
 * @since 2022-03-21
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class DefaultMailAccountService implements MailAccountService {

    private final MailAccountMapper mailAccountMapper;

    private final MailTemplateService mailTemplateService;

    @Override
    public Long createMailAccount(MailAccountSaveRequest createRequest) {
        MailAccountEntity account = BeanUtils.toBean(createRequest, MailAccountEntity.class);
        mailAccountMapper.insert(account);
        return account.getId();
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.MAIL_ACCOUNT, key = "#updateRequest.id")
    public void updateMailAccount(MailAccountSaveRequest updateRequest) {
        // Validate existence
        validateMailAccountExists(updateRequest.getId());

        // Update
        MailAccountEntity updateObj = BeanUtils.toBean(updateRequest, MailAccountEntity.class);
        mailAccountMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.MAIL_ACCOUNT, key = "#id")
    public void deleteMailAccount(Long id) {
        // Validate account existence
        validateMailAccountExists(id);
        // Validate whether associated templates exist
        if (mailTemplateService.getMailTemplateCountByAccountId(id) > 0) {
            throw exception(MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS);
        }

        // Delete
        mailAccountMapper.deleteById(id);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.MAIL_ACCOUNT,
            allEntries = true) // allEntries clears all caches because Spring Cache does not support batch deletion by ids
    public void deleteMailAccountList(List<Long> ids) {
        // 1. Validate whether associated templates exist
        for (Long id : ids) {
            if (mailTemplateService.getMailTemplateCountByAccountId(id) > 0) {
                throw exception(MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS);
            }
        }

        // 2. Batch delete
        mailAccountMapper.deleteByIds(ids);
    }

    private void validateMailAccountExists(Long id) {
        if (mailAccountMapper.selectById(id) == null) {
            throw exception(MAIL_ACCOUNT_NOT_EXISTS);
        }
    }

    @Override
    public MailAccountEntity getMailAccount(Long id) {
        return mailAccountMapper.selectById(id);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.MAIL_ACCOUNT, key = "#id", unless = "#result == null")
    public MailAccountEntity getMailAccountFromCache(Long id) {
        return getMailAccount(id);
    }

    @Override
    public PageResult<MailAccountEntity> getMailAccountPage(MailAccountPageRequest pageRequest) {
        return mailAccountMapper.selectPage(pageRequest);
    }

    @Override
    public List<MailAccountEntity> getMailAccountList() {
        return mailAccountMapper.selectList();
    }

}
