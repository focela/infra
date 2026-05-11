package com.focela.platform.module.system.service.notify;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.test.core.ut.BaseDbUnitTest;
import com.focela.platform.module.system.controller.admin.notify.vo.message.NotifyMessageMyPageReqVO;
import com.focela.platform.module.system.controller.admin.notify.vo.message.NotifyMessagePageReqVO;
import com.focela.platform.module.system.repository.entity.notify.NotifyMessageEntity;
import com.focela.platform.module.system.repository.entity.notify.NotifyTemplateEntity;
import com.focela.platform.module.system.repository.mapper.notify.NotifyMessageMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.framework.common.util.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.framework.common.util.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.util.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
* {@link NotifyMessageServiceImpl} 的单元测试类
*
* @author 芋道源码
*/
@Import(NotifyMessageServiceImpl.class)
public class NotifyMessageServiceImplTest extends BaseDbUnitTest {

    @Resource
    private NotifyMessageServiceImpl notifyMessageService;

    @Resource
    private NotifyMessageMapper notifyMessageMapper;

    @Test
    public void testCreateNotifyMessage_success() {
        // 准备参数
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        NotifyTemplateEntity template = randomPojo(NotifyTemplateEntity.class);
        String templateContent = randomString();
        Map<String, Object> templateParams = randomTemplateParams();
        // mock 方法

        // 调用
        Long messageId = notifyMessageService.createNotifyMessage(userId, userType,
                template, templateContent, templateParams);
        // 断言
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
       // mock 数据
       NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class, o -> { // 等会查询到
           o.setUserId(1L);
           o.setUserType(UserTypeEnum.ADMIN.getValue());
           o.setTemplateCode("test_01");
           o.setTemplateType(10);
           o.setCreateTime(buildTime(2022, 1, 2));
           o.setTemplateParams(randomTemplateParams());
       });
       notifyMessageMapper.insert(dbNotifyMessage);
       // 测试 userId 不匹配
       notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserId(2L)));
       // 测试 userType 不匹配
       notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
       // 测试 templateCode 不匹配
       notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setTemplateCode("test_11")));
       // 测试 templateType 不匹配
       notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setTemplateType(20)));
       // 测试 createTime 不匹配
       notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setCreateTime(buildTime(2022, 2, 1))));
       // 准备参数
       NotifyMessagePageReqVO reqVO = new NotifyMessagePageReqVO();
       reqVO.setUserId(1L);
       reqVO.setUserType(UserTypeEnum.ADMIN.getValue());
       reqVO.setTemplateCode("est_01");
       reqVO.setTemplateType(10);
       reqVO.setCreateTime(buildBetweenTime(2022, 1, 1, 2022, 1, 10));

       // 调用
       PageResult<NotifyMessageEntity> pageResult = notifyMessageService.getNotifyMessagePage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbNotifyMessage, pageResult.getList().get(0));
    }

    @Test
    public void testGetNotifyMessage() {
        // mock 数据
        NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class,
                o -> o.setTemplateParams(randomTemplateParams()));
        notifyMessageMapper.insert(dbNotifyMessage);
        // 准备参数
        Long id = dbNotifyMessage.getId();

        // 调用
        NotifyMessageEntity notifyMessage = notifyMessageService.getNotifyMessage(id);
        assertPojoEquals(dbNotifyMessage, notifyMessage);
    }

    @Test
    public void testGetMyNotifyMessagePage() {
        // mock 数据
        NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class, o -> { // 等会查询到
            o.setUserId(1L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setReadStatus(true);
            o.setCreateTime(buildTime(2022, 1, 2));
            o.setTemplateParams(randomTemplateParams());
        });
        notifyMessageMapper.insert(dbNotifyMessage);
        // 测试 userId 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserId(2L)));
        // 测试 userType 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // 测试 readStatus 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setReadStatus(false)));
        // 测试 createTime 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setCreateTime(buildTime(2022, 2, 1))));
        // 准备参数
        Long userId = 1L;
        Integer userType = UserTypeEnum.ADMIN.getValue();
        NotifyMessageMyPageReqVO reqVO = new NotifyMessageMyPageReqVO();
        reqVO.setReadStatus(true);
        reqVO.setCreateTime(buildBetweenTime(2022, 1, 1, 2022, 1, 10));

        // 调用
        PageResult<NotifyMessageEntity> pageResult = notifyMessageService.getMyMyNotifyMessagePage(reqVO, userId, userType);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbNotifyMessage, pageResult.getList().get(0));
    }

    @Test
    public void testGetUnreadNotifyMessageList() {
        // mock 数据
        NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class, o -> { // 等会查询到
            o.setUserId(1L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setReadStatus(false);
            o.setTemplateParams(randomTemplateParams());
        });
        notifyMessageMapper.insert(dbNotifyMessage);
        // 测试 userId 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserId(2L)));
        // 测试 userType 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // 测试 readStatus 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setReadStatus(true)));
        // 准备参数
        Long userId = 1L;
        Integer userType = UserTypeEnum.ADMIN.getValue();
        Integer size = 10;

        // 调用
        List<NotifyMessageEntity> list = notifyMessageService.getUnreadNotifyMessageList(userId, userType, size);
        // 断言
        assertEquals(1, list.size());
        assertPojoEquals(dbNotifyMessage, list.get(0));
    }

    @Test
    public void testGetUnreadNotifyMessageCount() {
        // mock 数据
        NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class, o -> { // 等会查询到
            o.setUserId(1L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setReadStatus(false);
            o.setTemplateParams(randomTemplateParams());
        });
        notifyMessageMapper.insert(dbNotifyMessage);
        // 测试 userId 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserId(2L)));
        // 测试 userType 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // 测试 readStatus 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setReadStatus(true)));
        // 准备参数
        Long userId = 1L;
        Integer userType = UserTypeEnum.ADMIN.getValue();

        // 调用，并断言
        assertEquals(1, notifyMessageService.getUnreadNotifyMessageCount(userId, userType));
    }

    @Test
    public void testUpdateNotifyMessageRead() {
        // mock 数据
        NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class, o -> { // 等会查询到
            o.setUserId(1L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setReadStatus(false);
            o.setReadTime(null);
            o.setTemplateParams(randomTemplateParams());
        });
        notifyMessageMapper.insert(dbNotifyMessage);
        // 测试 userId 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserId(2L)));
        // 测试 userType 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // 测试 readStatus 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setReadStatus(true)));
        // 准备参数
        Collection<Long> ids = Arrays.asList(dbNotifyMessage.getId(), dbNotifyMessage.getId() + 1,
                dbNotifyMessage.getId() + 2, dbNotifyMessage.getId() + 3);
        Long userId = 1L;
        Integer userType = UserTypeEnum.ADMIN.getValue();

        // 调用
        int updateCount = notifyMessageService.updateNotifyMessageRead(ids, userId, userType);
        // 断言
        assertEquals(1, updateCount);
        NotifyMessageEntity notifyMessage = notifyMessageMapper.selectById(dbNotifyMessage.getId());
        assertTrue(notifyMessage.getReadStatus());
        assertNotNull(notifyMessage.getReadTime());
    }

    @Test
    public void testUpdateAllNotifyMessageRead() {
        // mock 数据
        NotifyMessageEntity dbNotifyMessage = randomPojo(NotifyMessageEntity.class, o -> { // 等会查询到
            o.setUserId(1L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setReadStatus(false);
            o.setReadTime(null);
            o.setTemplateParams(randomTemplateParams());
        });
        notifyMessageMapper.insert(dbNotifyMessage);
        // 测试 userId 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserId(2L)));
        // 测试 userType 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // 测试 readStatus 不匹配
        notifyMessageMapper.insert(cloneIgnoreId(dbNotifyMessage, o -> o.setReadStatus(true)));
        // 准备参数
        Long userId = 1L;
        Integer userType = UserTypeEnum.ADMIN.getValue();

        // 调用
        int updateCount = notifyMessageService.updateAllNotifyMessageRead(userId, userType);
        // 断言
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
