package com.focela.platform.module.system.service.mail;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.module.system.controller.admin.mail.dto.account.MailAccountPageRequest;
import com.focela.platform.module.system.controller.admin.mail.dto.account.MailAccountSaveRequest;
import com.focela.platform.module.system.repository.entity.mail.MailAccountEntity;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 邮箱账号 Service 接口
 *
 * @since 2022-03-21
 */
public interface MailAccountService {

    /**
     * 创建邮箱账号
     *
     * @param createRequest 邮箱账号信息
     * @return 编号
     */
    Long createMailAccount(@Valid MailAccountSaveRequest createRequest);

    /**
     * 修改邮箱账号
     *
     * @param updateRequest 邮箱账号信息
     */
    void updateMailAccount(@Valid MailAccountSaveRequest updateRequest);

    /**
     * 删除邮箱账号
     *
     * @param id 编号
     */
    void deleteMailAccount(Long id);

    /**
     * 批量删除邮箱账号
     *
     * @param ids 编号列表
     */
    void deleteMailAccountList(List<Long> ids);

    /**
     * 获取邮箱账号信息
     *
     * @param id 编号
     * @return 邮箱账号信息
     */
    MailAccountEntity getMailAccount(Long id);

    /**
     * 从缓存中获取邮箱账号
     *
     * @param id 编号
     * @return 邮箱账号
     */
    MailAccountEntity getMailAccountFromCache(Long id);

    /**
     * 获取邮箱账号分页信息
     *
     * @param pageRequest 邮箱账号分页参数
     * @return 邮箱账号分页信息
     */
    PageResult<MailAccountEntity> getMailAccountPage(MailAccountPageRequest pageRequest);

    /**
     * 获取邮箱数组信息
     *
     * @return 邮箱账号信息数组
     */
    List<MailAccountEntity> getMailAccountList();

}
