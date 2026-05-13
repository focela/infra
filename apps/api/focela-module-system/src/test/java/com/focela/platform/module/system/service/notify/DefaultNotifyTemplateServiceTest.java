package com.focela.platform.module.system.service.notify;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.module.system.controller.admin.notify.dto.template.NotifyTemplatePageRequest;
import com.focela.platform.module.system.controller.admin.notify.dto.template.NotifyTemplateSaveRequest;
import com.focela.platform.module.system.entity.notify.NotifyTemplateEntity;
import com.focela.platform.module.system.repository.mapper.notify.NotifyTemplateMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.framework.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.utils.RandomUtils.*;
import static com.focela.platform.module.system.constants.ErrorCodeConstants.NOTIFY_TEMPLATE_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link DefaultNotifyTemplateService} 的单元测试类
 */
@Import(DefaultNotifyTemplateService.class)
public class DefaultNotifyTemplateServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultNotifyTemplateService notifyTemplateService;

    @Resource
    private NotifyTemplateMapper notifyTemplateMapper;

    @Test
    public void testCreateNotifyTemplate_success() {
        // 准备参数
        NotifyTemplateSaveRequest request = randomPojo(NotifyTemplateSaveRequest.class,
                o -> o.setStatus(randomCommonStatus()))
                .setId(null); // 防止 id 被赋值

        // 调用
        Long notifyTemplateId = notifyTemplateService.createNotifyTemplate(request);
        // 断言
        assertNotNull(notifyTemplateId);
        // 校验记录的属性是否正确
        NotifyTemplateEntity notifyTemplate = notifyTemplateMapper.selectById(notifyTemplateId);
        assertPojoEquals(request, notifyTemplate, "id");
    }

    @Test
    public void testUpdateNotifyTemplate_success() {
        // mock 数据
        NotifyTemplateEntity dbNotifyTemplate = randomPojo(NotifyTemplateEntity.class);
        notifyTemplateMapper.insert(dbNotifyTemplate);// @Sql: 先插入出一条存在的数据
        // 准备参数
        NotifyTemplateSaveRequest request = randomPojo(NotifyTemplateSaveRequest.class, o -> {
            o.setId(dbNotifyTemplate.getId()); // 设置更新的 ID
            o.setStatus(randomCommonStatus());
        });

        // 调用
        notifyTemplateService.updateNotifyTemplate(request);
        // 校验是否更新正确
        NotifyTemplateEntity notifyTemplate = notifyTemplateMapper.selectById(request.getId()); // 获取最新的
        assertPojoEquals(request, notifyTemplate);
    }

    @Test
    public void testUpdateNotifyTemplate_notExists() {
        // 准备参数
        NotifyTemplateSaveRequest request = randomPojo(NotifyTemplateSaveRequest.class);

        // 调用, 并断言异常
        assertServiceException(() -> notifyTemplateService.updateNotifyTemplate(request), NOTIFY_TEMPLATE_NOT_EXISTS);
    }

    @Test
    public void testDeleteNotifyTemplate_success() {
        // mock 数据
        NotifyTemplateEntity dbNotifyTemplate = randomPojo(NotifyTemplateEntity.class);
        notifyTemplateMapper.insert(dbNotifyTemplate);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbNotifyTemplate.getId();

        // 调用
        notifyTemplateService.deleteNotifyTemplate(id);
        // 校验数据不存在了
        assertNull(notifyTemplateMapper.selectById(id));
    }

    @Test
    public void testDeleteNotifyTemplate_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> notifyTemplateService.deleteNotifyTemplate(id), NOTIFY_TEMPLATE_NOT_EXISTS);
    }

    @Test
    public void testGetNotifyTemplatePage() {
        // mock 数据
        NotifyTemplateEntity dbNotifyTemplate = randomPojo(NotifyTemplateEntity.class, o -> { // 等会查询到
            o.setName("芋头");
            o.setCode("test_01");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCreateTime(buildTime(2022, 2, 3));
        });
        notifyTemplateMapper.insert(dbNotifyTemplate);
        // 测试 name 不匹配
        notifyTemplateMapper.insert(cloneIgnoreId(dbNotifyTemplate, o -> o.setName("投")));
        // 测试 code 不匹配
        notifyTemplateMapper.insert(cloneIgnoreId(dbNotifyTemplate, o -> o.setCode("test_02")));
        // 测试 status 不匹配
        notifyTemplateMapper.insert(cloneIgnoreId(dbNotifyTemplate, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // 测试 createTime 不匹配
        notifyTemplateMapper.insert(cloneIgnoreId(dbNotifyTemplate, o -> o.setCreateTime(buildTime(2022, 1, 5))));
        // 准备参数
        NotifyTemplatePageRequest request = new NotifyTemplatePageRequest();
        request.setName("芋");
        request.setCode("est_01");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());
        request.setCreateTime(buildBetweenTime(2022, 2, 1, 2022, 2, 5));

        // 调用
        PageResult<NotifyTemplateEntity> pageResult = notifyTemplateService.getNotifyTemplatePage(request);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbNotifyTemplate, pageResult.getList().get(0));
    }

    @Test
    public void testGetNotifyTemplate() {
        // mock 数据
        NotifyTemplateEntity dbNotifyTemplate = randomPojo(NotifyTemplateEntity.class);
        notifyTemplateMapper.insert(dbNotifyTemplate);
        // 准备参数
        Long id = dbNotifyTemplate.getId();

        // 调用
        NotifyTemplateEntity notifyTemplate = notifyTemplateService.getNotifyTemplate(id);
        // 断言
        assertPojoEquals(dbNotifyTemplate, notifyTemplate);
    }

    @Test
    public void testGetNotifyTemplateByCodeFromCache() {
        // mock 数据
        NotifyTemplateEntity dbNotifyTemplate = randomPojo(NotifyTemplateEntity.class);
        notifyTemplateMapper.insert(dbNotifyTemplate);
        // 准备参数
        String code = dbNotifyTemplate.getCode();

        // 调用
        NotifyTemplateEntity notifyTemplate = notifyTemplateService.getNotifyTemplateByCodeFromCache(code);
        // 断言
        assertPojoEquals(dbNotifyTemplate, notifyTemplate);
    }

    @Test
    public void testFormatNotifyTemplateContent() {
        // 准备参数
        Map<String, Object> params = new HashMap<>();
        params.put("name", "小红");
        params.put("what", "饭");

        // 调用，并断言
        assertEquals("小红，你好，饭吃了吗？",
                notifyTemplateService.formatNotifyTemplateContent("{name}，你好，{what}吃了吗？", params));
    }
}
