package com.focela.platform.system.service.notice;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.notice.dto.NoticePageRequest;
import com.focela.platform.system.controller.admin.notice.dto.NoticeSaveRequest;
import com.focela.platform.system.entity.notice.NoticeEntity;
import com.focela.platform.system.repository.mapper.notice.NoticeMapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.ErrorCodeConstants.NOTICE_NOT_FOUND;

/**
 * 通知公告 Service 实现类
 */
@Service
public class DefaultNoticeService implements NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    @Override
    public Long createNotice(NoticeSaveRequest createRequest) {
        NoticeEntity notice = BeanUtils.toBean(createRequest, NoticeEntity.class);
        noticeMapper.insert(notice);
        return notice.getId();
    }

    @Override
    public void updateNotice(NoticeSaveRequest updateRequest) {
        // 校验是否存在
        validateNoticeExists(updateRequest.getId());
        // 更新通知公告
        NoticeEntity updateObj = BeanUtils.toBean(updateRequest, NoticeEntity.class);
        noticeMapper.updateById(updateObj);
    }

    @Override
    public void deleteNotice(Long id) {
        // 校验是否存在
        validateNoticeExists(id);
        // 删除通知公告
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
