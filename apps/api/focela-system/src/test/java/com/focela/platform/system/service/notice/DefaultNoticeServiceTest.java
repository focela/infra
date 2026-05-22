package com.focela.platform.system.service.notice;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.notice.request.NoticePageRequest;
import com.focela.platform.system.controller.admin.notice.request.NoticeSaveRequest;
import com.focela.platform.system.domain.entity.notice.NoticeEntity;
import com.focela.platform.system.repository.mapper.notice.NoticeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;

import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.randomLongId;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.NOTICE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;

@Import(DefaultNoticeService.class)
class DefaultNoticeServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultNoticeService noticeService;

    @Resource
    private NoticeMapper noticeMapper;

    @Test
    public void getNoticePage_success() {
        // insert prerequisite data
        NoticeEntity dbNotice = randomPojo(NoticeEntity.class, o -> {
            o.setTitle("Nicolas Zhao Si is here!");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        noticeMapper.insert(dbNotice);
        // test title mismatch
        noticeMapper.insert(cloneIgnoreId(dbNotice, o -> o.setTitle("Nicolas Cage is here too!")));
        // test status mismatch
        noticeMapper.insert(cloneIgnoreId(dbNotice, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // prepare parameters
        NoticePageRequest request = new NoticePageRequest();
        request.setTitle("Nicolas Zhao Si is here!");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // invoke
        PageResult<NoticeEntity> pageResult = noticeService.getNoticePage(request);
        // verify query result is filtered
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbNotice, pageResult.getList().get(0));
    }

    @Test
    public void getNotice_success() {
        // insert prerequisite data
        NoticeEntity dbNotice = randomPojo(NoticeEntity.class);
        noticeMapper.insert(dbNotice);

        // query
        NoticeEntity notice = noticeService.getNotice(dbNotice.getId());

        // verify inserted and read objects match
        assertNotNull(notice);
        assertPojoEquals(dbNotice, notice);
    }

    @Test
    public void createNotice_success() {
        // prepare parameters
        NoticeSaveRequest request = randomPojo(NoticeSaveRequest.class)
                .setId(null); // avoid id being assigned

        // invoke
        Long noticeId = noticeService.createNotice(request);
        // verify inserted properties are correct
        assertNotNull(noticeId);
        NoticeEntity notice = noticeMapper.selectById(noticeId);
        assertPojoEquals(request, notice, "id");
    }

    @Test
    public void updateNotice_success() {
        // insert prerequisite data
        NoticeEntity dbNoticeEntity = randomPojo(NoticeEntity.class);
        noticeMapper.insert(dbNoticeEntity);

        // prepare update parameters
        NoticeSaveRequest request = randomPojo(NoticeSaveRequest.class, o -> o.setId(dbNoticeEntity.getId()));

        // update
        noticeService.updateNotice(request);
        // verify update succeeded
        NoticeEntity notice = noticeMapper.selectById(request.getId());
        assertPojoEquals(request, notice);
    }

    @Test
    public void deleteNotice_success() {
        // insert prerequisite data
        NoticeEntity dbNotice = randomPojo(NoticeEntity.class);
        noticeMapper.insert(dbNotice);

        // delete
        noticeService.deleteNotice(dbNotice.getId());

        // verify deletion succeeded
        assertNull(noticeMapper.selectById(dbNotice.getId()));
    }

    @Test
    public void validateNoticeExists_success() {
        // insert prerequisite data
        NoticeEntity dbNotice = randomPojo(NoticeEntity.class);
        noticeMapper.insert(dbNotice);

        // successful invoke
        noticeService.validateNoticeExists(dbNotice.getId());
    }

    @Test
    public void validateNoticeExists_missing() {
        assertServiceException(() ->
                noticeService.validateNoticeExists(randomLongId()), NOTICE_NOT_FOUND);
    }

}
