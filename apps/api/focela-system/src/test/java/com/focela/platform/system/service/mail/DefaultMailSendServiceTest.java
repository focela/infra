package com.focela.platform.system.service.mail;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.test.core.support.BaseMockitoUnitTest;
import com.focela.platform.test.core.utils.RandomUtils;
import com.focela.platform.system.domain.entity.mail.MailAccountEntity;
import com.focela.platform.system.domain.entity.mail.MailTemplateEntity;
import com.focela.platform.system.domain.entity.user.UserEntity;
import com.focela.platform.system.mq.message.mail.MailSendMessage;
import com.focela.platform.system.mq.producer.mail.MailProducer;
import com.focela.platform.system.service.member.MemberService;
import com.focela.platform.system.service.user.UserService;
import org.assertj.core.util.Lists;
import org.dromara.hutool.extra.mail.MailAccount;
import org.dromara.hutool.extra.mail.MailUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DefaultMailSendServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DefaultMailSendService mailSendService;

    @Mock
    private UserService adminUserService;
    @Mock
    private MemberService memberService;
    @Mock
    private MailAccountService mailAccountService;
    @Mock
    private MailTemplateService mailTemplateService;
    @Mock
    private MailLogService mailLogService;
    @Mock
    private MailProducer mailProducer;

    /**
     * Used to quickly test whether your mail account works
     */
    @Test
    @Disabled
    public void sendMail_manualSmtpSmokeTest() {
        MailAccount mailAccount = new MailAccount()
                .setFrom(System.getProperty("focela.test.mail.from", "sender@example.com"))
                .setHost(System.getProperty("focela.test.mail.host", "smtp.example.com"))
                .setPort(Integer.getInteger("focela.test.mail.port", 465))
                .setSslEnable(Boolean.parseBoolean(System.getProperty("focela.test.mail.ssl", "true")))
                .setAuth(true)
                .setUser(System.getProperty("focela.test.mail.user", "sender@example.com"))
                .setPass(System.getProperty("focela.test.mail.password", "change-me").toCharArray());
        String messageId = MailUtil.send(mailAccount,
                System.getProperty("focela.test.mail.to", "recipient@example.com"),
                "subject", "content", false);
        System.out.println("send result: " + messageId);
    }

    @Test
    public void testSendSingleMail_success() {
        // prepare parameters
        Long userId = randomLongId();
        String templateCode = RandomUtils.randomString();
        Map<String, Object> templateParams = MapUtil.<String, Object>builder().put("code", "1234")
                .put("op", "login").build();
        Collection<String> toMails = Lists.newArrayList("admin@test.com");
        Collection<String> ccMails = Lists.newArrayList("cc@test.com");
        Collection<String> bccMails = Lists.newArrayList("bcc@test.com");

        // mock adminUserService  method
        UserEntity user = randomPojo(UserEntity.class, o -> o.setEmail("admin@example.com"));
        when(adminUserService.getUser(eq(userId))).thenReturn(user);

        // mock MailTemplateService  method
        MailTemplateEntity template = randomPojo(MailTemplateEntity.class, o -> {
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setContent("verification code is {code}, operation is{op}");
            o.setParams(Lists.newArrayList("code", "op"));
        });
        when(mailTemplateService.getMailTemplateByCodeFromCache(eq(templateCode))).thenReturn(template);
        String title = RandomUtils.randomString();
        when(mailTemplateService.formatMailTemplateContent(eq(template.getTitle()), eq(templateParams)))
                .thenReturn(title);
        String content = RandomUtils.randomString();
        when(mailTemplateService.formatMailTemplateContent(eq(template.getContent()), eq(templateParams)))
                .thenReturn(content);
        // mock MailAccountService  method
        MailAccountEntity account = randomPojo(MailAccountEntity.class);
        when(mailAccountService.getMailAccountFromCache(eq(template.getAccountId()))).thenReturn(account);
        // mock MailLogService  method
        Long mailLogId = randomLongId();
        when(mailLogService.createMailLog(eq(userId), eq(UserTypeEnum.ADMIN.getValue()),
                argThat(toMailSet -> toMailSet.contains(user.getEmail()) && toMailSet.contains("admin@test.com")),
                argThat(ccMailSet -> ccMailSet.contains("cc@test.com")),
                argThat(bccMailSet -> bccMailSet.contains("bcc@test.com")),
                eq(account), eq(template), eq(content), eq(templateParams), eq(true))).thenReturn(mailLogId);

        // invoke
        Long resultMailLogId = mailSendService.sendSingleMail(toMails, ccMails, bccMails, userId,
                UserTypeEnum.ADMIN.getValue(), templateCode, templateParams, (File[]) null);
        // assert
        assertEquals(mailLogId, resultMailLogId);
        // assert call
        verify(mailProducer).sendMailSendMessage(eq(mailLogId),
                argThat(toMailSet -> toMailSet.contains(user.getEmail()) && toMailSet.contains("admin@test.com")),
                argThat(ccMailSet -> ccMailSet.contains("cc@test.com")),
                argThat(bccMailSet -> bccMailSet.contains("bcc@test.com")),
                eq(account.getId()), eq(template.getNickname()), eq(title), eq(content), isNull());
    }

    /**
     * send succeeds when mail template is enabled
     */
    @Test
    public void testSendSingleMail_successWhenMailTemplateEnable() {
        // prepare parameters
        String mail = randomEmail();
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String templateCode = RandomUtils.randomString();
        Map<String, Object> templateParams = MapUtil.<String, Object>builder().put("code", "1234")
                .put("op", "login").build();
        Collection<String> toMails = Lists.newArrayList(mail);

        // mock MailTemplateService  method
        MailTemplateEntity template = randomPojo(MailTemplateEntity.class, o -> {
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setContent("verification code is {code}, operation is{op}");
            o.setParams(Lists.newArrayList("code", "op"));
        });
        when(mailTemplateService.getMailTemplateByCodeFromCache(eq(templateCode))).thenReturn(template);
        String title = RandomUtils.randomString();
        when(mailTemplateService.formatMailTemplateContent(eq(template.getTitle()), eq(templateParams)))
                .thenReturn(title);
        String content = RandomUtils.randomString();
        when(mailTemplateService.formatMailTemplateContent(eq(template.getContent()), eq(templateParams)))
                .thenReturn(content);
        // mock MailAccountService  method
        MailAccountEntity account = randomPojo(MailAccountEntity.class);
        when(mailAccountService.getMailAccountFromCache(eq(template.getAccountId()))).thenReturn(account);
        // mock MailLogService  method
        Long mailLogId = randomLongId();
        when(mailLogService.createMailLog(eq(userId), eq(userType),
                argThat(toMailSet -> toMailSet.contains(mail)),
                argThat(Collection::isEmpty),
                argThat(Collection::isEmpty),
                eq(account), eq(template), eq(content), eq(templateParams), eq(true))).thenReturn(mailLogId);

        // invoke
        Long resultMailLogId = mailSendService.sendSingleMail(toMails, null, null, userId, userType, templateCode, templateParams, (java.io.File[]) null);
        // assert
        assertEquals(mailLogId, resultMailLogId);
        // assert call
        verify(mailProducer).sendMailSendMessage(eq(mailLogId),
                argThat(toMailSet -> toMailSet.contains(mail)),
                argThat(Collection::isEmpty),
                argThat(Collection::isEmpty),
                eq(account.getId()), eq(template.getNickname()), eq(title), eq(content), isNull());
    }

    /**
     * send succeeds when mail template is disabled
     */
    @Test
    public void testSendSingleMail_successWhenMailTemplateDisable() {
        // prepare parameters
        String mail = randomEmail();
        Long userId = randomLongId();
        Integer userType = randomEle(UserTypeEnum.values()).getValue();
        String templateCode = RandomUtils.randomString();
        Map<String, Object> templateParams = MapUtil.<String, Object>builder().put("code", "1234")
                .put("op", "login").build();
        Collection<String> toMails = Lists.newArrayList(mail);

        // mock MailTemplateService  method
        MailTemplateEntity template = randomPojo(MailTemplateEntity.class, o -> {
            o.setStatus(CommonStatusEnum.DISABLE.getStatus());
            o.setContent("verification code is {code}, operation is{op}");
            o.setParams(Lists.newArrayList("code", "op"));
        });
        when(mailTemplateService.getMailTemplateByCodeFromCache(eq(templateCode))).thenReturn(template);
        String title = RandomUtils.randomString();
        when(mailTemplateService.formatMailTemplateContent(eq(template.getTitle()), eq(templateParams)))
                .thenReturn(title);
        String content = RandomUtils.randomString();
        when(mailTemplateService.formatMailTemplateContent(eq(template.getContent()), eq(templateParams)))
                .thenReturn(content);
        // mock MailAccountService  method
        MailAccountEntity account = randomPojo(MailAccountEntity.class);
        when(mailAccountService.getMailAccountFromCache(eq(template.getAccountId()))).thenReturn(account);
        // mock MailLogService  method
        Long mailLogId = randomLongId();
        when(mailLogService.createMailLog(eq(userId), eq(userType),
                argThat(toMailSet -> toMailSet.contains(mail)),
                argThat(Collection::isEmpty),
                argThat(Collection::isEmpty),
                eq(account), eq(template), eq(content), eq(templateParams), eq(false))).thenReturn(mailLogId);

        // invoke
        Long resultMailLogId = mailSendService.sendSingleMail(toMails, null, null, userId, userType, templateCode, templateParams, (java.io.File[]) null);
        // assert
        assertEquals(mailLogId, resultMailLogId);
        // assert call
        verify(mailProducer, times(0)).sendMailSendMessage(anyLong(), any(), any(), any(),
                anyLong(), anyString(), anyString(), anyString(), any());
    }

    @Test
    public void testValidateMailTemplateValid_notExists() {
        // prepare parameters
        String templateCode = RandomUtils.randomString();
        // mock the method

        // invoke, and assert exception
        assertServiceException(() -> mailSendService.validateMailTemplate(templateCode),
                MAIL_TEMPLATE_NOT_FOUND);
    }

    @Test
    public void testValidateTemplateParams_paramMiss() {
        // prepare parameters
        MailTemplateEntity template = randomPojo(MailTemplateEntity.class,
                o -> o.setParams(Lists.newArrayList("code")));
        Map<String, Object> templateParams = new HashMap<>();
        // mock the method

        // invoke, and assert exception
        assertServiceException(() -> mailSendService.validateTemplateParams(template, templateParams),
                MAIL_SEND_TEMPLATE_PARAM_MISS, "code");
    }

    @Test
    public void testSendSingleMail_noValidEmail() {
        // prepare parameters
        Long userId = randomLongId();
        String templateCode = RandomUtils.randomString();
        Map<String, Object> templateParams = MapUtil.<String, Object>builder().put("code", "1234")
                .put("op", "login").build();
        Collection<String> toMails = Lists.newArrayList("invalid-email"); // invalid email

        // mock MailTemplateService  method
        MailTemplateEntity template = randomPojo(MailTemplateEntity.class, o -> {
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setContent("verification code is {code}, operation is{op}");
            o.setParams(Lists.newArrayList("code", "op"));
        });
        when(mailTemplateService.getMailTemplateByCodeFromCache(eq(templateCode))).thenReturn(template);

        // mock MailAccountService  method
        MailAccountEntity account = randomPojo(MailAccountEntity.class);
        when(mailAccountService.getMailAccountFromCache(eq(template.getAccountId()))).thenReturn(account);

        // invoke, and assert exception
        assertServiceException(() -> mailSendService.sendSingleMail(toMails, null, null, userId,
                UserTypeEnum.ADMIN.getValue(), templateCode, templateParams, (java.io.File[]) null),
                MAIL_SEND_MAILBOX_NOT_FOUND);
    }

    @Test
    public void testDoSendMail_success() {
        try (final MockedStatic<MailUtil> mailUtilMock = mockStatic(MailUtil.class)) {
            // prepare parameters
            MailSendMessage message = randomPojo(MailSendMessage.class, o -> o.setNickname("Focela"));
            // mock the method（get mail account）
            MailAccountEntity account = randomPojo(MailAccountEntity.class, o -> o.setMail("7685@qq.com"));
            when(mailAccountService.getMailAccountFromCache(eq(message.getAccountId())))
                    .thenReturn(account);

            // mock the method（send mail）
            String messageId = randomString();
            mailUtilMock.when(() -> MailUtil.send(
                    argThat(mailAccount -> {
                        assertEquals("Focela <7685@qq.com>", mailAccount.getFrom());
                        assertTrue(mailAccount.isAuth());
                        assertEquals(account.getUsername(), mailAccount.getUser());
                        assertArrayEquals(account.getPassword().toCharArray(), mailAccount.getPass());
                        assertEquals(account.getHost(), mailAccount.getHost());
                        assertEquals(account.getPort(), mailAccount.getPort());
                        assertEquals(account.getSslEnable(), mailAccount.isSslEnable());
                        return true;
                    }), eq(message.getToMails()), eq(message.getCcMails()), eq(message.getBccMails()),
                    eq(message.getTitle()), eq(message.getContent()), eq(true), eq(message.getAttachments())))
                    .thenReturn(messageId);

            // invoke
            mailSendService.doSendMail(message);
            // assert
            verify(mailLogService).updateMailSendResult(eq(message.getLogId()), eq(messageId), isNull());
        }
    }

    @Test
    public void testDoSendMail_exception() {
        try (MockedStatic<MailUtil> mailUtilMock = mockStatic(MailUtil.class)) {
            // prepare parameters
            MailSendMessage message = randomPojo(MailSendMessage.class, o -> o.setNickname("Focela"));
            // mock the method（get mail account）
            MailAccountEntity account = randomPojo(MailAccountEntity.class, o -> o.setMail("7685@qq.com"));
            when(mailAccountService.getMailAccountFromCache(eq(message.getAccountId())))
                    .thenReturn(account);

            // mock the method（send mail）
            Exception e = new NullPointerException("lalala");
            mailUtilMock.when(() -> MailUtil.send(argThat(mailAccount -> {
                        assertEquals("Focela <7685@qq.com>", mailAccount.getFrom());
                        assertTrue(mailAccount.isAuth());
                        assertEquals(account.getUsername(), mailAccount.getUser());
                        assertArrayEquals(account.getPassword().toCharArray(), mailAccount.getPass());
                        assertEquals(account.getHost(), mailAccount.getHost());
                        assertEquals(account.getPort(), mailAccount.getPort());
                        assertEquals(account.getSslEnable(), mailAccount.isSslEnable());
                        return true;
                    }), eq(message.getToMails()), eq(message.getCcMails()), eq(message.getBccMails()),
                    eq(message.getTitle()), eq(message.getContent()), eq(true), same(message.getAttachments()))).thenThrow(e);

            // invoke
            mailSendService.doSendMail(message);
            // assert
            verify(mailLogService).updateMailSendResult(eq(message.getLogId()), isNull(), same(e));
        }
    }

}
