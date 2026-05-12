package com.focela.platform.module.system.service.mail;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.module.system.controller.admin.mail.dto.template.MailTemplatePageRequest;
import com.focela.platform.module.system.controller.admin.mail.dto.template.MailTemplateSaveRequest;
import com.focela.platform.module.system.repository.entity.mail.MailTemplateEntity;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * 邮件模版 Service 接口
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
public interface MailTemplateService {

    /**
     * 邮件模版创建
     *
     * @param createRequest 邮件信息
     * @return 编号
     */
    Long createMailTemplate(@Valid MailTemplateSaveRequest createRequest);

    /**
     * 邮件模版修改
     *
     * @param updateRequest 邮件信息
     */
    void updateMailTemplate(@Valid MailTemplateSaveRequest updateRequest);

    /**
     * 邮件模版删除
     *
     * @param id 编号
     */
    void deleteMailTemplate(Long id);

    /**
     * 批量删除邮件模版
     *
     * @param ids 编号列表
     */
    void deleteMailTemplateList(List<Long> ids);

    /**
     * 获取邮件模版
     *
     * @param id 编号
     * @return 邮件模版
     */
    MailTemplateEntity getMailTemplate(Long id);

    /**
     * 获取邮件模版分页
     *
     * @param pageRequest 模版信息
     * @return 邮件模版分页信息
     */
    PageResult<MailTemplateEntity> getMailTemplatePage(MailTemplatePageRequest pageRequest);

    /**
     * 获取邮件模板数组
     *
     * @return 模版数组
     */
    List<MailTemplateEntity> getMailTemplateList();

    /**
     * 从缓存中获取邮件模版
     *
     * @param code 模板编码
     * @return 邮件模板
     */
    MailTemplateEntity getMailTemplateByCodeFromCache(String code);

    /**
     * 邮件模版内容合成
     *
     * @param content 邮件模版
     * @param params 合成参数
     * @return 格式化后的内容
     */
    String formatMailTemplateContent(String content, Map<String, Object> params);

    /**
     * 获得指定邮件账号下的邮件模板数量
     *
     * @param accountId 账号编号
     * @return 数量
     */
    long getMailTemplateCountByAccountId(Long accountId);

}
