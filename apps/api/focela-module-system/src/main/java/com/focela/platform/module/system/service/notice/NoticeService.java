package com.focela.platform.module.system.service.notice;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.module.system.controller.admin.notice.dto.NoticePageRequest;
import com.focela.platform.module.system.controller.admin.notice.dto.NoticeSaveRequest;
import com.focela.platform.module.system.entity.notice.NoticeEntity;

import java.util.List;

/**
 * 通知公告 Service 接口
 */
public interface NoticeService {

    /**
     * 创建通知公告
     *
     * @param createRequest 通知公告
     * @return 编号
     */
    Long createNotice(NoticeSaveRequest createRequest);

    /**
     * 更新通知公告
     *
     * @param request 通知公告
     */
    void updateNotice(NoticeSaveRequest request);

    /**
     * 删除通知公告
     *
     * @param id 编号
     */
    void deleteNotice(Long id);

    /**
     * 批量删除通知公告
     *
     * @param ids 编号列表
     */
    void deleteNoticeList(List<Long> ids);

    /**
     * 获得通知公告分页列表
     *
     * @param request 分页条件
     * @return 部门分页列表
     */
    PageResult<NoticeEntity> getNoticePage(NoticePageRequest request);

    /**
     * 获得通知公告
     *
     * @param id 编号
     * @return 通知公告
     */
    NoticeEntity getNotice(Long id);

}
