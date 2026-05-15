package com.focela.platform.system.service.notice;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.notice.dto.NoticePageRequest;
import com.focela.platform.system.controller.admin.notice.dto.NoticeSaveRequest;
import com.focela.platform.system.entity.notice.NoticeEntity;
import com.focela.platform.system.repository.mapper.notice.NoticeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;

import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.randomLongId;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.system.constants.ErrorCodeConstants.NOTICE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;

@Import(DefaultNoticeService.class)
class DefaultNoticeServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultNoticeService noticeService;

    @Resource
    private NoticeMapper noticeMapper;

    @Test
    public void testGetNoticePage_success() {
        // 插入前置数据
        NoticeEntity dbNotice = randomPojo(NoticeEntity.class, o -> {
            o.setTitle("尼古拉斯赵四来啦！");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        noticeMapper.insert(dbNotice);
        // 测试 title 不匹配
        noticeMapper.insert(cloneIgnoreId(dbNotice, o -> o.setTitle("尼古拉斯凯奇也来啦！")));
        // 测试 status 不匹配
        noticeMapper.insert(cloneIgnoreId(dbNotice, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // 准备参数
        NoticePageRequest request = new NoticePageRequest();
        request.setTitle("尼古拉斯赵四来啦！");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // 调用
        PageResult<NoticeEntity> pageResult = noticeService.getNoticePage(request);
        // 验证查询结果经过筛选
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbNotice, pageResult.getList().get(0));
    }

    @Test
    public void testGetNotice_success() {
        // 插入前置数据
        NoticeEntity dbNotice = randomPojo(NoticeEntity.class);
        noticeMapper.insert(dbNotice);

        // 查询
        NoticeEntity notice = noticeService.getNotice(dbNotice.getId());

        // 验证插入与读取对象是否一致
        assertNotNull(notice);
        assertPojoEquals(dbNotice, notice);
    }

    @Test
    public void testCreateNotice_success() {
        // 准备参数
        NoticeSaveRequest request = randomPojo(NoticeSaveRequest.class)
                .setId(null); // 避免 id 被赋值

        // 调用
        Long noticeId = noticeService.createNotice(request);
        // 校验插入属性是否正确
        assertNotNull(noticeId);
        NoticeEntity notice = noticeMapper.selectById(noticeId);
        assertPojoEquals(request, notice, "id");
    }

    @Test
    public void testUpdateNotice_success() {
        // 插入前置数据
        NoticeEntity dbNoticeDO = randomPojo(NoticeEntity.class);
        noticeMapper.insert(dbNoticeDO);

        // 准备更新参数
        NoticeSaveRequest request = randomPojo(NoticeSaveRequest.class, o -> o.setId(dbNoticeDO.getId()));

        // 更新
        noticeService.updateNotice(request);
        // 检验是否更新成功
        NoticeEntity notice = noticeMapper.selectById(request.getId());
        assertPojoEquals(request, notice);
    }

    @Test
    public void testDeleteNotice_success() {
        // 插入前置数据
        NoticeEntity dbNotice = randomPojo(NoticeEntity.class);
        noticeMapper.insert(dbNotice);

        // 删除
        noticeService.deleteNotice(dbNotice.getId());

        // 检查是否删除成功
        assertNull(noticeMapper.selectById(dbNotice.getId()));
    }

    @Test
    public void testValidateNoticeExists_success() {
        // 插入前置数据
        NoticeEntity dbNotice = randomPojo(NoticeEntity.class);
        noticeMapper.insert(dbNotice);

        // 成功调用
        noticeService.validateNoticeExists(dbNotice.getId());
    }

    @Test
    public void testValidateNoticeExists_noExists() {
        assertServiceException(() ->
                noticeService.validateNoticeExists(randomLongId()), NOTICE_NOT_FOUND);
    }

}
