package com.focela.platform.system.service.notice;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.notice.dto.NoticePageRequest;
import com.focela.platform.system.controller.admin.notice.dto.NoticeSaveRequest;
import com.focela.platform.system.entity.notice.NoticeEntity;
import com.focela.platform.system.repository.mapper.notice.NoticeMapper;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.NOTICE_NOT_FOUND;

/**
 * Notice Service implementation class
 */
@Service
@RequiredArgsConstructor
public class DefaultNoticeService implements NoticeService {

        private final NoticeMapper noticeMapper;

    @Override
    public Long createNotice(NoticeSaveRequest createRequest) {
        NoticeEntity notice = BeanUtils.toBean(createRequest, NoticeEntity.class);
        noticeMapper.insert(notice);
        return notice.getId();
    }

    @Override
    public void updateNotice(NoticeSaveRequest updateRequest) {
        // Validate existence
        validateNoticeExists(updateRequest.getId());
        // Update notice
        NoticeEntity updateObj = BeanUtils.toBean(updateRequest, NoticeEntity.class);
        noticeMapper.updateById(updateObj);
    }

    @Override
    public void deleteNotice(Long id) {
        // Validate existence
        validateNoticeExists(id);
        // Delete notice
        noticeMapper.deleteById(id);
    }

    @Override
    public void deleteNoticeList(List<Long> ids) {
        noticeMapper.deleteByIds(ids);
    }

    @Override
    public PageResult<NoticeEntity> getNoticePage(NoticePageRequest request) {
        return noticeMapper.selectPage(request);
    }

    @Override
    public NoticeEntity getNotice(Long id) {
        return noticeMapper.selectById(id);
    }

    @VisibleForTesting
    public void validateNoticeExists(Long id) {
        if (id == null) {
            return;
        }
        NoticeEntity notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
    }

}
