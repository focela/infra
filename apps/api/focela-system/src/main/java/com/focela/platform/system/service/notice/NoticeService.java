package com.focela.platform.system.service.notice;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.notice.request.NoticePageRequest;
import com.focela.platform.system.controller.admin.notice.request.NoticeSaveRequest;
import com.focela.platform.system.domain.entity.notice.NoticeEntity;

import java.util.List;

/**
 * Notice Service interface
 */
public interface NoticeService {

    /**
     * Create a notice
     *
     * @param createRequest notice
     * @return ID
     */
    Long createNotice(NoticeSaveRequest createRequest);

    /**
     * Update a notice
     *
     * @param request notice
     */
    void updateNotice(NoticeSaveRequest request);

    /**
     * Delete a notice
     *
     * @param id ID
     */
    void deleteNotice(Long id);

    /**
     * Batch delete notices
     *
     * @param ids ID list
     */
    void deleteNoticeList(List<Long> ids);

    /**
     * Get paginated list of notices
     *
     * @param request pagination conditions
     * @return paginated notice list
     */
    PageResult<NoticeEntity> getNoticePage(NoticePageRequest request);

    /**
     * Get a notice
     *
     * @param id ID
     * @return notice
     */
    NoticeEntity getNotice(Long id);

}
