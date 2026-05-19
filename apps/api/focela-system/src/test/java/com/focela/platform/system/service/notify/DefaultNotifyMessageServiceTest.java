package com.focela.platform.system.service.notify;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.notify.request.message.NotifyMessageMyPageRequest;
import com.focela.platform.system.controller.admin.notify.request.message.NotifyMessagePageRequest;
import com.focela.platform.system.domain.entity.notify.NotifyMessageEntity;
import com.focela.platform.system.domain.entity.notify.NotifyTemplateEntity;
import com.focela.platform.system.repository.mapper.notify.NotifyMessageMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
* {@link DefaultNotifyMessageService}  unit test class
*/
@Import(DefaultNotifyMessageService.class)
public class DefaultNotifyMessageServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultNotifyMessageService notifyMessageService;

    @Resource
    private NotifyMessageMapper notifyMessageMapper;

    @Test
    public void testCreateNotifyMessage_success() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        NotifyTemplateEntity template = randomPojo(NotifyTemplateEntity.class);
        String templateContent = randomString();
        Map<String, Object> templateParams = randomTemplateParams();
        // mock the method

        // invoke
        Long messageId = notifyMessageService.createNotifyMessage(userId, userType,
                template, templateContent, templateParams);
        // assert
        NotifyMessageEntity message = notifyMessageMapper.selectById(messageId);
        assertNotNull(message);
        assertEquals(userId, message.getUserId());
        assertEquals(userType, message.getUserType());
        assertEquals(template.getId(), message.getTemplateId());
        assertEquals(template.getCode(), message.getTemplateCode());
        assertEquals(template.getType(), message.getTemplateType());
        assertEquals(template.getNickname(), message.getTemplateNickname());
        assertEquals(templateContent, message.getTemplateContent());
        assertEquals(templateParams, message.getTemplateParams());
        assertEquals(false, message.getReadStatus());
        assertNull(message.getReadTime());
    }

    @Test
    public void testGetNotifyMessagePage() {
       // mock data
       NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class, o -> { // will be queried later
           o.setUserId(1L);
           o.setUserType(UserTypeEnum.ADMIN.getValue());
           o.setTemplateCode("test_01");
           o.setTemplateType(10);
           o.setCreateTime(buildTime(2022, 1, 2));
           o.setTemplateParams(randomTemplateParams());
       });
       notifyMessageMapper.insert(dbNotifyMessage);
       // test userId mismatch
       notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserId(2L)));
       // test userType mismatch
       notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
       // test templateCode mismatch
       notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setTemplateCode("test_11")));
       // test templateType mismatch
       notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setTemplateType(20)));
       // test createTime mismatch
       notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setCreateTime(buildTime(2022, 2, 1))));
       // prepare parameters
       NotifyMessagePageRequest request = new NotifyMessagePageRequest();
       request.setUserId(1L);
       request.setUserType(UserTypeEnum.ADMIN.getValue());
       request.setTemplateCode("est_01");
       request.setTemplateType(10);
       request.setCreateTime(buildBetweenTime(2022, 1, 1, 2022, 1, 10));

       // invoke
       PageResult<NotifyMessageEntity> pageResult = notifyMessageService.getNotifyMessagePage(request);
       // assert
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbNotifyMessage, pageResult.getList().get(0));
    }

    @Test
    public void testGetNotifyMessage() {
        // mock data
        NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class,
                o -> o.setTemplateParams(randomTemplateParams()));
        notifyMessageMapper.insert(dbNotifyMessage);
        // prepare parameters
        Long id = dbNotifyMessage.getId();

        // invoke
        NotifyMessageEntity notifyMessage = notifyMessageService.getNotifyMessage(id);
        assertPojoEquals(dbNotifyMessage, notifyMessage);
    }

    @Test
    public void testGetMyNotifyMessagePage() {
        // mock data
        NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class, o -> { // will be queried later
            o.setUserId(1L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setReadStatus(true);
            o.setCreateTime(buildTime(2022, 1, 2));
            o.setTemplateParams(randomTemplateParams());
        });
        notifyMessageMapper.insert(dbNotifyMessage);
        // test userId mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserId(2L)));
        // test userType mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // test readStatus mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setReadStatus(false)));
        // test createTime mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setCreateTime(buildTime(2022, 2, 1))));
        // prepare parameters
        Long userId = 1L;
        Integer userType = UserTypeEnum.ADMIN.getValue();
        NotifyMessageMyPageRequest request = new NotifyMessageMyPageRequest();
        request.setReadStatus(true);
        request.setCreateTime(buildBetweenTime(2022, 1, 1, 2022, 1, 10));

        // invoke
        PageResult<NotifyMessageEntity> pageResult = notifyMessageService.getMyNotifyMessagePage(request, userId, userType);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbNotifyMessage, pageResult.getList().get(0));
    }

    @Test
    public void testGetUnreadNotifyMessageList() {
        // mock data
        NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class, o -> { // will be queried later
            o.setUserId(1L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setReadStatus(false);
            o.setTemplateParams(randomTemplateParams());
        });
        notifyMessageMapper.insert(dbNotifyMessage);
        // test userId mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserId(2L)));
        // test userType mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // test readStatus mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setReadStatus(true)));
        // prepare parameters
        Long userId = 1L;
        Integer userType = UserTypeEnum.ADMIN.getValue();
        Integer size = 10;

        // invoke
        List<NotifyMessageEntity> list = notifyMessageService.getUnreadNotifyMessageList(userId, userType, size);
        // assert
        assertEquals(1, list.size());
        assertPojoEquals(dbNotifyMessage, list.get(0));
    }

    @Test
    public void testGetUnreadNotifyMessageCount() {
        // mock data
        NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class, o -> { // will be queried later
            o.setUserId(1L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setReadStatus(false);
            o.setTemplateParams(randomTemplateParams());
        });
        notifyMessageMapper.insert(dbNotifyMessage);
        // test userId mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserId(2L)));
        // test userType mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // test readStatus mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setReadStatus(true)));
        // prepare parameters
        Long userId = 1L;
        Integer userType = UserTypeEnum.ADMIN.getValue();

        // invoke, and assert
        assertEquals(1, notifyMessageService.getUnreadNotifyMessageCount(userId, userType));
    }

    @Test
    public void testUpdateNotifyMessageRead() {
        // mock data
        NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class, o -> { // will be queried later
            o.setUserId(1L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setReadStatus(false);
            o.setReadTime(null);
            o.setTemplateParams(randomTemplateParams());
        });
        notifyMessageMapper.insert(dbNotifyMessage);
        // test userId mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserId(2L)));
        // test userType mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // test readStatus mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setReadStatus(true)));
        // prepare parameters
        Collection<Long> ids = Arrays.asList(dbNotifyMessage.getId(), dbNotifyMessage.getId() + 1,
                dbNotifyMessage.getId() + 2, dbNotifyMessage.getId() + 3);
        Long userId = 1L;
        Integer userType = UserTypeEnum.ADMIN.getValue();

        // invoke
        int updateCount = notifyMessageService.updateNotifyMessageRead(ids, userId, userType);
        // assert
        assertEquals(1, updateCount);
        NotifyMessageEntity notifyMessage = notifyMessageMapper.selectById(dbNotifyMessage.getId());
        assertTrue(notifyMessage.getReadStatus());
        assertNotNull(notifyMessage.getReadTime());
    }

    @Test
    public void testUpdateAllNotifyMessageRead() {
        // mock data
        NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class, o -> { // will be queried later
            o.setUserId(1L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setReadStatus(false);
            o.setReadTime(null);
            o.setTemplateParams(randomTemplateParams());
        });
        notifyMessageMapper.insert(dbNotifyMessage);
        // test userId mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserId(2L)));
        // test userType mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // test readStatus mismatch
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setReadStatus(true)));
        // prepare parameters
        Long userId = 1L;
        Integer userType = UserTypeEnum.ADMIN.getValue();

        // invoke
        int updateCount = notifyMessageService.updateAllNotifyMessageRead(userId, userType);
        // assert
        assertEquals(1, updateCount);
        NotifyMessageEntity notifyMessage = notifyMessageMapper.selectById(dbNotifyMessage.getId());
        assertTrue(notifyMessage.getReadStatus());
        assertNotNull(notifyMessage.getReadTime());
    }

    private static Map<String, Object> randomTemplateParams() {
        return MapUtil.<String, Object>builder().put(randomString(), randomString())
                .put(randomString(), randomString()).build();
    }

}
