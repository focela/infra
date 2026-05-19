package com.focela.platform.system.service.mail;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.mail.dto.log.MailLogPageRequest;
import com.focela.platform.system.domain.entity.mail.MailAccountEntity;
import com.focela.platform.system.domain.entity.mail.MailLogEntity;
import com.focela.platform.system.domain.entity.mail.MailTemplateEntity;
import com.focela.platform.system.repository.mapper.mail.MailLogMapper;
import com.focela.platform.system.enums.mail.MailSendStatusEnum;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Map;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link DefaultMailLogService}  unit test class
 */
@Import(DefaultMailLogService.class)
public class DefaultMailLogServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultMailLogService mailLogService;

    @Resource
    private MailLogMapper mailLogMapper;

    @Test
    public void testCreateMailLog() {
        // prepare parameters
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        Collection<String> toMails = Lists.newArrayList(randomEmail(), randomEmail());
        Collection<String> ccMails = Lists.newArrayList(randomEmail());
        Collection<String> bccMails = Lists.newArrayList(randomEmail());
        MailAccountEntity account = randomPojo(MailAccountEntity.class);
        MailTemplateEntity template = randomPojo(MailTemplateEntity.class);
        String templateContent = randomString();
        Map<String, Object> templateParams = randomTemplateParams();
        Boolean isSend = true;
        // mock the method

        // invoke
        Long logId = mailLogService.createMailLog(userId, userType, toMails, ccMails, bccMails,
                account, template, templateContent, templateParams, isSend);
        // assert
        MailLogEntity log = mailLogMapper.selectById(logId);
        assertNotNull(log);
        assertEquals(MailSendStatusEnum.INIT.getStatus(), log.getSendStatus());
        assertEquals(userId, log.getUserId());
        assertEquals(userType, log.getUserType());
        assertEquals(toMails.size(), log.getToMails().size());
        assertTrue(log.getToMails().containsAll(toMails));
        assertEquals(ccMails.size(), log.getCcMails().size());
        assertTrue(log.getCcMails().containsAll(ccMails));
        assertEquals(bccMails.size(), log.getBccMails().size());
        assertTrue(log.getBccMails().containsAll(bccMails));
        assertEquals(account.getId(), log.getAccountId());
        assertEquals(account.getMail(), log.getFromMail());
        assertEquals(template.getId(), log.getTemplateId());
        assertEquals(template.getCode(), log.getTemplateCode());
        assertEquals(template.getNickname(), log.getTemplateNickname());
        assertEquals(template.getTitle(), log.getTemplateTitle());
        assertEquals(templateContent, log.getTemplateContent());
        assertEquals(templateParams, log.getTemplateParams());
    }

    @Test
    public void testUpdateMailSendResult_success() {
        // mock data
        MailLogEntity log = randomPojo(MailLogEntity.class, o -> {
            o.setSendStatus(MailSendStatusEnum.INIT.getStatus());
            o.setSendTime(null).setSendMessageId(null).setSendException(null)
                    .setTemplateParams(randomTemplateParams());
        });
        mailLogMapper.insert(log);
        // prepare parameters
        Long logId = log.getId();
        String messageId = randomString();

        // invoke
        mailLogService.updateMailSendResult(logId, messageId, null);
        // assert
        MailLogEntity dbLog = mailLogMapper.selectById(logId);
        assertEquals(MailSendStatusEnum.SUCCESS.getStatus(), dbLog.getSendStatus());
        assertNotNull(dbLog.getSendTime());
        assertEquals(messageId, dbLog.getSendMessageId());
        assertNull(dbLog.getSendException());
    }

    @Test
    public void testUpdateMailSendResult_exception() {
        // mock data
        MailLogEntity log = randomPojo(MailLogEntity.class, o -> {
            o.setSendStatus(MailSendStatusEnum.INIT.getStatus());
            o.setSendTime(null).setSendMessageId(null).setSendException(null)
                    .setTemplateParams(randomTemplateParams());
        });
        mailLogMapper.insert(log);
        // prepare parameters
        Long logId = log.getId();
        Exception exception = new NullPointerException("test exception");

        // invoke
        mailLogService.updateMailSendResult(logId, null, exception);
        // assert
        MailLogEntity dbLog = mailLogMapper.selectById(logId);
        assertEquals(MailSendStatusEnum.FAILURE.getStatus(), dbLog.getSendStatus());
        assertNotNull(dbLog.getSendTime());
        assertNull(dbLog.getSendMessageId());
        assertEquals("NullPointerException: test exception", dbLog.getSendException());
    }

    @Test
    public void testGetMailLog() {
        // mock data
        MailLogEntity dbMailLog = randomPojo(MailLogEntity.class, o -> o.setTemplateParams(randomTemplateParams()));
        mailLogMapper.insert(dbMailLog);
        // prepare parameters
        Long id = dbMailLog.getId();

        // invoke
        MailLogEntity mailLog = mailLogService.getMailLog(id);
        // assert
        assertPojoEquals(dbMailLog, mailLog);
    }

    @Test
    public void testGetMailLogPage() {
       // mock data
       MailLogEntity dbMailLog = randomPojo(MailLogEntity.class, o -> { // will be queried later
           o.setUserId(1L);
           o.setUserType(UserTypeEnum.ADMIN.getValue());
           o.setToMails(Lists.newArrayList("768@qq.com"));
           o.setCcMails(Lists.newArrayList());
           o.setBccMails(Lists.newArrayList());
           o.setAccountId(10L);
           o.setTemplateId(100L);
           o.setSendStatus(MailSendStatusEnum.INIT.getStatus());
           o.setSendTime(buildTime(2023, 2, 10));
           o.setTemplateParams(randomTemplateParams());
       });
       mailLogMapper.insert(dbMailLog);
       // test userId mismatch
       mailLogMapper.insert(cloneIgnoreId(dbMailLog, o -> o.setUserId(2L)));
       // test userType mismatch
       mailLogMapper.insert(cloneIgnoreId(dbMailLog, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
       // test toMails mismatch（Special note: find_in_set cannot be unit-tested）
//       mailLogMapper.insert(cloneIgnoreId(dbMailLog, o -> o.setToMails(Lists.newArrayList("788@qq.com"))));
       // test accountId mismatch
       mailLogMapper.insert(cloneIgnoreId(dbMailLog, o -> o.setAccountId(11L)));
       // test templateId mismatch
       mailLogMapper.insert(cloneIgnoreId(dbMailLog, o -> o.setTemplateId(101L)));
       // test sendStatus mismatch
       mailLogMapper.insert(cloneIgnoreId(dbMailLog, o -> o.setSendStatus(MailSendStatusEnum.SUCCESS.getStatus())));
       // test sendTime mismatch
       mailLogMapper.insert(cloneIgnoreId(dbMailLog, o -> o.setSendTime(buildTime(2023, 3, 10))));
       // prepare parameters
       MailLogPageRequest request = new MailLogPageRequest();
       request.setUserId(1L);
       request.setUserType(UserTypeEnum.ADMIN.getValue());
//       request.setToMail("768@qq.com");
       request.setAccountId(10L);
       request.setTemplateId(100L);
       request.setSendStatus(MailSendStatusEnum.INIT.getStatus());
       request.setSendTime((buildBetweenTime(2023, 2, 1, 2023, 2, 15)));

       // invoke
       PageResult<MailLogEntity> pageResult = mailLogService.getMailLogPage(request);
       // assert
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbMailLog, pageResult.getList().get(0));
    }

    private static Map<String, Object> randomTemplateParams() {
        return MapUtil.<String, Object>builder().put(randomString(), randomString())
                .put(randomString(), randomString()).build();
    }
}
