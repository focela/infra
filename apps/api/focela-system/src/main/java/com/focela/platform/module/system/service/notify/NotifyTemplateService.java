package com.focela.platform.module.system.service.notify;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.module.system.controller.admin.notify.dto.template.NotifyTemplatePageRequest;
import com.focela.platform.module.system.controller.admin.notify.dto.template.NotifyTemplateSaveRequest;
import com.focela.platform.module.system.entity.notify.NotifyTemplateEntity;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * 站内信模版 Service 接口
 */
public interface NotifyTemplateService {

    /**
     * 创建站内信模版
     *
     * @param createRequest 创建信息
     * @return 编号
     */
    Long createNotifyTemplate(@Valid NotifyTemplateSaveRequest createRequest);

    /**
     * 更新站内信模版
     *
     * @param updateRequest 更新信息
     */
    void updateNotifyTemplate(@Valid NotifyTemplateSaveRequest updateRequest);

    /**
     * 删除站内信模版
     *
     * @param id 编号
     */
    void deleteNotifyTemplate(Long id);

    /**
     * 批量删除站内信模版
     *
     * @param ids 编号列表
     */
    void deleteNotifyTemplateList(List<Long> ids);

    /**
     * 获得站内信模版
     *
     * @param id 编号
     * @return 站内信模版
     */
    NotifyTemplateEntity getNotifyTemplate(Long id);

    /**
     * 获得站内信模板，从缓存中
     *
     * @param code 模板编码
     * @return 站内信模板
     */
    NotifyTemplateEntity getNotifyTemplateByCodeFromCache(String code);

    /**
     * 获得站内信模版分页
     *
     * @param pageRequest 分页查询
     * @return 站内信模版分页
     */
    PageResult<NotifyTemplateEntity> getNotifyTemplatePage(NotifyTemplatePageRequest pageRequest);

    /**
     * 格式化站内信内容
     *
     * @param content 站内信模板的内容
     * @param params 站内信内容的参数
     * @return 格式化后的内容
     */
    String formatNotifyTemplateContent(String content, Map<String, Object> params);

}
