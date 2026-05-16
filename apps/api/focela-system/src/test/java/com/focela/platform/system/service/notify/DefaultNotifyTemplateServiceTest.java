package com.focela.platform.system.service.notify;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.notify.dto.template.NotifyTemplatePageRequest;
import com.focela.platform.system.controller.admin.notify.dto.template.NotifyTemplateSaveRequest;
import com.focela.platform.system.entity.notify.NotifyTemplateEntity;
import com.focela.platform.system.repository.mapper.notify.NotifyTemplateMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.ErrorCodeConstants.NOTIFY_TEMPLATE_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link DefaultNotifyTemplateService}  unit test class
 */
@Import(DefaultNotifyTemplateService.class)
public class DefaultNotifyTemplateServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultNotifyTemplateService notifyTemplateService;

    @Resource
    private NotifyTemplateMapper notifyTemplateMapper;

    @Test
    public void testCreateNotifyTemplate_success() {
        // prepare parameters
        NotifyTemplateSaveRequest request = randomPojo(NotifyTemplateSaveRequest.class,
                o -> o.setStatus(randomCommonStatus()))
                .setId(null); // prevent id from being assigned

        // invoke
        Long notifyTemplateId = notifyTemplateService.createNotifyTemplate(request);
        // assert
        assertNotNull(notifyTemplateId);
        // verify record properties are correct
        NotifyTemplateEntity notifyTemplate = notifyTemplateMapper.selectById(notifyTemplateId);
        assertPojoEquals(request, notifyTemplate, "id");
    }

    @Test
    public void testUpdateNotifyTemplate_success() {
        // mock data
        NotifyTemplateEntity dbNotifyTemplate = randomPojo(NotifyTemplateEntity.class);
        notifyTemplateMapper.insert(dbNotifyTemplate);// @Sql: first insert an existing record
        // prepare parameters
        NotifyTemplateSaveRequest request = randomPojo(NotifyTemplateSaveRequest.class, o -> {
            o.setId(dbNotifyTemplate.getId()); // set updated ID
            o.setStatus(randomCommonStatus());
        });

        // invoke
        notifyTemplateService.updateNotifyTemplate(request);
        // verify update is correct
        NotifyTemplateEntity notifyTemplate = notifyTemplateMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, notifyTemplate);
    }

    @Test
    public void testUpdateNotifyTemplate_notExists() {
        // prepare parameters
        NotifyTemplateSaveRequest request = randomPojo(NotifyTemplateSaveRequest.class);

        // invoke and assert exception
        assertServiceException(() -> notifyTemplateService.updateNotifyTemplate(request), NOTIFY_TEMPLATE_NOT_EXISTS);
    }

    @Test
    public void testDeleteNotifyTemplate_success() {
        // mock data
        NotifyTemplateEntity dbNotifyTemplate = randomPojo(NotifyTemplateEntity.class);
        notifyTemplateMapper.insert(dbNotifyTemplate);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbNotifyTemplate.getId();

        // invoke
        notifyTemplateService.deleteNotifyTemplate(id);
        // verify data no longer exists
        assertNull(notifyTemplateMapper.selectById(id));
    }

    @Test
    public void testDeleteNotifyTemplate_notExists() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> notifyTemplateService.deleteNotifyTemplate(id), NOTIFY_TEMPLATE_NOT_EXISTS);
    }

    @Test
    public void testGetNotifyTemplatePage() {
        // mock data
        NotifyTemplateEntity dbNotifyTemplate = randomPojo(NotifyTemplateEntity.class, o -> { // will be queried later
            o.setName("Focela");
            o.setCode("test_01");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCreateTime(buildTime(2022, 2, 3));
        });
        notifyTemplateMapper.insert(dbNotifyTemplate);
        // test name mismatch
        notifyTemplateMapper.insert(cloneIgnoreId(dbNotifyTemplate, o -> o.setName("vote")));
        // test code mismatch
        notifyTemplateMapper.insert(cloneIgnoreId(dbNotifyTemplate, o -> o.setCode("test_02")));
        // test status mismatch
        notifyTemplateMapper.insert(cloneIgnoreId(dbNotifyTemplate, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // test createTime mismatch
        notifyTemplateMapper.insert(cloneIgnoreId(dbNotifyTemplate, o -> o.setCreateTime(buildTime(2022, 1, 5))));
        // prepare parameters
        NotifyTemplatePageRequest request = new NotifyTemplatePageRequest();
        request.setName("Focela");
        request.setCode("est_01");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());
        request.setCreateTime(buildBetweenTime(2022, 2, 1, 2022, 2, 5));

        // invoke
        PageResult<NotifyTemplateEntity> pageResult = notifyTemplateService.getNotifyTemplatePage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbNotifyTemplate, pageResult.getList().get(0));
    }

    @Test
    public void testGetNotifyTemplate() {
        // mock data
        NotifyTemplateEntity dbNotifyTemplate = randomPojo(NotifyTemplateEntity.class);
        notifyTemplateMapper.insert(dbNotifyTemplate);
        // prepare parameters
        Long id = dbNotifyTemplate.getId();

        // invoke
        NotifyTemplateEntity notifyTemplate = notifyTemplateService.getNotifyTemplate(id);
        // assert
        assertPojoEquals(dbNotifyTemplate, notifyTemplate);
    }

    @Test
    public void testGetNotifyTemplateByCodeFromCache() {
        // mock data
        NotifyTemplateEntity dbNotifyTemplate = randomPojo(NotifyTemplateEntity.class);
        notifyTemplateMapper.insert(dbNotifyTemplate);
        // prepare parameters
        String code = dbNotifyTemplate.getCode();

        // invoke
        NotifyTemplateEntity notifyTemplate = notifyTemplateService.getNotifyTemplateByCodeFromCache(code);
        // assert
        assertPojoEquals(dbNotifyTemplate, notifyTemplate);
    }

    @Test
    public void testFormatNotifyTemplateContent() {
        // prepare parameters
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Xiaohong");
        params.put("what", "rice");

        // invoke and assert
        assertEquals("Xiaohong, hello, have you eaten rice?",
                notifyTemplateService.formatNotifyTemplateContent("{name}, hello, have you eaten {what}?", params));
    }
}
