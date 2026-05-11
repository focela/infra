package com.focela.platform.module.system.service.notice;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.notice.vo.NoticePageReqVO;
import com.focela.platform.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import com.focela.platform.module.system.repository.entity.notice.NoticeEntity;
import com.focela.platform.module.system.repository.mapper.notice.NoticeMapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.focela.platform.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.NOTICE_NOT_FOUND;

/**
 * 通知公告 Service 实现类
 *
 * @author 芋道源码
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    @Override
    public Long createNotice(NoticeSaveReqVO createReqVO) {
        NoticeEntity notice = BeanUtils.toBean(createReqVO, NoticeEntity.class);
        noticeMapper.insert(notice);
        return notice.getId();
    }

    @Override
    public void updateNotice(NoticeSaveReqVO updateReqVO) {
        // 校验是否存在
        validateNoticeExists(updateReqVO.getId());
        // 更新通知公告
        NoticeEntity updateObj = BeanUtils.toBean(updateReqVO, NoticeEntity.class);
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
    public PageResult<NoticeEntity> getNoticePage(NoticePageReqVO reqVO) {
        return noticeMapper.selectPage(reqVO);
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
