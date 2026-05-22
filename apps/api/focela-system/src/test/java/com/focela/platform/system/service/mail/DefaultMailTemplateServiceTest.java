package com.focela.platform.system.service.mail;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.mail.request.template.MailTemplateSaveRequest;
import com.focela.platform.system.controller.admin.mail.request.template.MailTemplatePageRequest;
import com.focela.platform.system.domain.entity.mail.MailTemplateEntity;
import com.focela.platform.system.repository.mapper.mail.MailTemplateMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.randomLongId;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.MAIL_TEMPLATE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link DefaultMailTemplateService}  unit test class
 */
@Import(DefaultMailTemplateService.class)
public class DefaultMailTemplateServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultMailTemplateService mailTemplateService;

    @Resource
    private MailTemplateMapper mailTemplateMapper;

    @Test
    public void createMailTemplate_success() {
        // prepare parameters
        MailTemplateSaveRequest request = randomPojo(MailTemplateSaveRequest.class)
                .setId(null); // prevent id from being assigned

        // invoke
        Long mailTemplateId = mailTemplateService.createMailTemplate(request);
        // assert
        assertNotNull(mailTemplateId);
        // verify record properties are correct
        MailTemplateEntity mailTemplate = mailTemplateMapper.selectById(mailTemplateId);
        assertPojoEquals(request, mailTemplate, "id");
    }

    @Test
    public void updateMailTemplate_success() {
        // mock data
        MailTemplateEntity dbMailTemplate = randomPojo(MailTemplateEntity.class);
        mailTemplateMapper.insert(dbMailTemplate);// @Sql: first insert an existing record
        // prepare parameters
        MailTemplateSaveRequest request = randomPojo(MailTemplateSaveRequest.class, o -> {
            o.setId(dbMailTemplate.getId()); // set updated ID
        });

        // invoke
        mailTemplateService.updateMailTemplate(request);
        // verify update is correct
        MailTemplateEntity mailTemplate = mailTemplateMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, mailTemplate);
    }

    @Test
    public void updateMailTemplate_missing() {
        // prepare parameters
        MailTemplateSaveRequest request = randomPojo(MailTemplateSaveRequest.class);

        // invoke and assert exception
        assertServiceException(() -> mailTemplateService.updateMailTemplate(request), MAIL_TEMPLATE_NOT_FOUND);
    }

    @Test
    public void deleteMailTemplate_success() {
        // mock data
        MailTemplateEntity dbMailTemplate = randomPojo(MailTemplateEntity.class);
        mailTemplateMapper.insert(dbMailTemplate);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbMailTemplate.getId();

        // invoke
        mailTemplateService.deleteMailTemplate(id);
        // verify data no longer exists
        assertNull(mailTemplateMapper.selectById(id));
    }

    @Test
    public void deleteMailTemplate_missing() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> mailTemplateService.deleteMailTemplate(id), MAIL_TEMPLATE_NOT_FOUND);
    }

    @Test
    public void getMailTemplatePage() {
        // mock data
        MailTemplateEntity dbMailTemplate = randomPojo(MailTemplateEntity.class, o -> { // will be queried later
            o.setName("source");
            o.setCode("test_01");
            o.setAccountId(1L);
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCreateTime(buildTime(2023, 2, 3));
        });
        mailTemplateMapper.insert(dbMailTemplate);
        // test name mismatch
        mailTemplateMapper.insert(cloneIgnoreId(dbMailTemplate, o -> o.setName("Focela")));
        // test code mismatch
        mailTemplateMapper.insert(cloneIgnoreId(dbMailTemplate, o -> o.setCode("test_02")));
        // test accountId mismatch
        mailTemplateMapper.insert(cloneIgnoreId(dbMailTemplate, o -> o.setAccountId(2L)));
        // test status mismatch
        mailTemplateMapper.insert(cloneIgnoreId(dbMailTemplate, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // test createTime mismatch
        mailTemplateMapper.insert(cloneIgnoreId(dbMailTemplate, o -> o.setCreateTime(buildTime(2023, 1, 5))));
        // prepare parameters
        MailTemplatePageRequest request = new MailTemplatePageRequest();
        request.setName("source");
        request.setCode("est_01");
        request.setAccountId(1L);
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());
        request.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 5));

        // invoke
        PageResult<MailTemplateEntity> pageResult = mailTemplateService.getMailTemplatePage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbMailTemplate, pageResult.getList().get(0));
    }

    @Test
    public void getMailTemplateList() {
        // mock data
        MailTemplateEntity dbMailTemplate01 = randomPojo(MailTemplateEntity.class);
        mailTemplateMapper.insert(dbMailTemplate01);
        MailTemplateEntity dbMailTemplate02 = randomPojo(MailTemplateEntity.class);
        mailTemplateMapper.insert(dbMailTemplate02);

        // invoke
        List<MailTemplateEntity> list = mailTemplateService.getMailTemplateList();
        // assert
        assertEquals(2, list.size());
        assertEquals(dbMailTemplate01, list.get(0));
        assertEquals(dbMailTemplate02, list.get(1));
    }

    @Test
    public void getMailTemplate() {
        // mock data
        MailTemplateEntity dbMailTemplate = randomPojo(MailTemplateEntity.class);
        mailTemplateMapper.insert(dbMailTemplate);
        // prepare parameters
        Long id = dbMailTemplate.getId();

        // invoke
        MailTemplateEntity mailTemplate = mailTemplateService.getMailTemplate(id);
        // assert
        assertPojoEquals(dbMailTemplate, mailTemplate);
    }

    @Test
    public void getMailTemplateByCodeFromCache() {
        // mock data
        MailTemplateEntity dbMailTemplate = randomPojo(MailTemplateEntity.class);
        mailTemplateMapper.insert(dbMailTemplate);
        // prepare parameters
        String code = dbMailTemplate.getCode();

        // invoke
        MailTemplateEntity mailTemplate = mailTemplateService.getMailTemplateByCodeFromCache(code);
        // assert
        assertPojoEquals(dbMailTemplate, mailTemplate);
    }

    @Test
    public void formatMailTemplateContent() {
        // prepare parameters
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Xiaohong");
        params.put("what", "rice");

        // invoke and assert
        assertEquals("Xiaohong, hello, have you eaten rice?",
                mailTemplateService.formatMailTemplateContent("{name}, hello, have you eaten {what}?", params));
    }

    @Test
    public void formatMailTemplateContent_htmlUnescape() {
        // prepare parameters
        Map<String, Object> params = new HashMap<>();
        params.put("title", "test title");

        // test HTML unescape
        String content = "<h1>{title}</h1>&lt;p&gt;this is a test&lt;/p&gt;&amp;nbsp;space";
        String expected = "<h1>test title</h1><p>this is a test</p> space";
        // invoke, and assert
        assertEquals(expected,
                mailTemplateService.formatMailTemplateContent(content, params));
    }

    @Test
    public void formatMailTemplateContent_codeBlockFormatting() {
        // prepare parameters
        Map<String, Object> params = new HashMap<>();
        params.put("name", "test");

        // test code block formatting
        String content = "<pre><code>public class Test {\n    public static void main(String[] args) {\n        System.out.println(\"Hello {name}\"));\n    }\n}</code></pre>";

        // invoke, and assert result
        String result = mailTemplateService.formatMailTemplateContent(content, params);
        // assert pre tag is replaced by div tag
        assertTrue(result.contains("<div><code>public class Test {"));
        assertTrue(result.contains("System.out.println(\"Hello test\""));
        assertTrue(result.contains("</code></div>"));
    }

    @Test
    public void formatMailTemplateContent_preToDiv() {
        // prepare parameters
        Map<String, Object> params = new HashMap<>();
        params.put("content", "test content");

        // test pre tag replacement to div tag
        String content = "<pre><code>{content}</code></pre>";
        String result = mailTemplateService.formatMailTemplateContent(content, params);
        // assert result contains div tags but not pre tags
        assertTrue(result.contains("<div><code>test content</code></div>"));
    }

    @Test
    public void formatMailTemplateContent_completeHtml() {
        // prepare parameters
        Map<String, Object> params = new HashMap<>();
        params.put("username", "testuser");
        params.put("company", "Test Company");

        // test full HTML email template
        String content = "<!DOCTYPE html>\n <html lang=\"en\">\n <head>\n  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n  <meta charset=\"UTF-8\">\n  <title>Title</title>\n </head>\n <body>\n <div>\n  <includetail>\n      <div>\n          <div class=\"open_email\" style=\"margin: 8px; \">\n              <div>\n                  <br>\n                  <span class=\"genEmailContent\">\n                      <div id=\"cTMail-Wrap\" style=\"word-break: break-all;box-sizing:border-box;text-align:left;min-width:320px; max-width:660px; border:1px solid #f6f6f6; background-color:#f7f8fa; margin:auto; padding:20px 0 30px; font-family:'helvetica neue',PingFangSC-Light,arial,'hiragino sans gb','microsoft yahei ui','microsoft yahei',simsun,sans-serif\">\n                          <div class=\"main-content\">\n                              <table style=\"width:100%;font-weight:300;margin-bottom:10px;border-collapse:collapse\">\n                                  <tbody>\n                                  <tr style=\"font-weight:300\">\n                                      <td style=\"width:3%;max-width:30px;\"></td>\n                                      <td style=\"max-width:600px;\">\n                                          <div id=\"cTMail-logo\" style=\"width:92px; height:36px;\">\n                                              <a href=\"\">\n                                                  <img border=\"0\" src=\"top-left logo image\" style=\"width:120px; height:36px;display:block\">\n                                              </a>\n                                          </div>\n                                          <div style=\"color: #C2C5C9;width: 260px;float: right;font-size: 12px;\">This email is sent by the system. Please do not reply directly or forward it to others.</div>\n                                          <p style=\"height:2px;background-color: #00a4ff;border: 0;font-size:0;padding:0;width:100%;margin-top:20px;\"></p>\n                                          <div id=\"cTMail-inner\" style=\"background-color:#fff; padding:23px 0 20px;box-shadow: 0px 1px 1px 0px rgba(122, 55, 55, 0.2);text-align:left;\">\n                                              <table style=\"width:100%;font-weight:300;margin-bottom:10px;border-collapse:collapse;\">\n                                                  <tbody>\n                                                  <tr style=\"font-weight:300\">\n                                                      <td style=\"width:3.2%;max-width:30px;\"></td>\n                                                      <td style=\"max-width:480px;\">\n                                                          <h1 id=\"cTMail-title\" style=\"font-size: 20px; line-height: 36px; margin: 0px 0px 22px;\">\n                                                              Dear {username}, \n                                                          </h1>\n                                                          <dl style=\"font-size: 14px; color: #595E65; line-height: 18px;\">\n                                                              <dd style=\"margin: 0px 0px 6px; padding: 0px; font-size: 14px; line-height: 22px;\">\n                                                                  <p id=\"cTMail-sender\" style=\"font-size: 14px; line-height: 26px; word-wrap: break-word; word-break: break-all; margin-top: 32px;\">\n                                                                     content<br>\n content<br>\n content123<br><br>\n\n If you encounter any issues or have suggestions during use, please feel free to contact our customer team at any time. We will be happy to serve you.\n\n\n\n\n <br>\n                                                                      <br>\n                                                                      {company}<br>\n                                                                      address: xxxxx<br>\n                                                                      email: lambc77@163.com\n                                                                  </p>\n                                                              </dd>\n                                                          </dl>\n                                                          <hr style=\"border: 0.1px solid #e5e5e5;\"/>\n                                                           <dl style=\"font-size: 14px; color: #595E65; line-height: 18px;\">\n                                                               <div style=\"color: #93979B;\">\n                                                                   <strong>Statement: This email contains confidential information and is intended only for the recipient. No one is allowed, without the sender's permission, in any form（including but not limited to partial disclosure, copying, or distribution）to improperly use the information in this email. If you received this email by mistake, please immediately notify the sender by phone or email and delete this email. Thank you!\n </strong><br>\n                                                               </div>\n                                                           </dl>\n                                                      </td>\n                                                      <td style=\"width:3.2%;max-width:30px;\"></td>\n                                                  </tr>\n                                                  </tbody>\n                                              </table>\n                                          </div>\n                                      </td>\n                                      <td style=\"width:3%;max-width:30px;\"></td>\n                                  </tr>\n                                  </tbody>\n                              </table>\n                          </div>\n                      </div>\n                  </span>\n              </div>\n          </div>\n      </div>\n  </includetail>\n </div>\n </body>\n <script>\n\n </script>\n </html>";

        // invoke, and assert successful handling
        String result = mailTemplateService.formatMailTemplateContent(content, params);
        // assert result contains substituted variables
        assertTrue(result.contains("Dear testuser"));
        assertTrue(result.contains("Test Company"));
        // assert result is valid HTML
        assertTrue(result.startsWith("<!DOCTYPE html>"));
    }

    @Test
    public void formatMailTemplateContent_emptyContent() {
        // prepare parameters
        Map<String, Object> params = new HashMap<>();

        // test empty content
        String result = mailTemplateService.formatMailTemplateContent("", params);
        assertEquals("", result);
    }

    @Test
    public void formatMailTemplateContent_noParams() {
        // prepare parameters
        Map<String, Object> params = new HashMap<>();

        // test case with no parameters to substitute
        String content = "<pre><code>System.out.println(\"Hello World\");</code></pre>";
        String result = mailTemplateService.formatMailTemplateContent(content, params);
        assertTrue(result.contains("<div><code>System.out.println(\"Hello World\");</code></div>"));
    }

    @Test
    public void formatMailTemplateContent_multiplePreTags() {
        // prepare parameters
        Map<String, Object> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");

        // test multiple pre tags case
        String content = "<pre><code>First code block: {param1}</code></pre>\n" +
                "<p>Some text between code blocks</p>\n" +
                "<pre><code>Second code block: {param2}</code></pre>";
        String result = mailTemplateService.formatMailTemplateContent(content, params);
        // assert both pre tags are replaced by div tags
        assertTrue(result.contains("<div><code>First code block: value1</code></div>"));
        assertTrue(result.contains("<div><code>Second code block: value2</code></div>"));
    }

    @Test
    public void formatMailTemplateContent_specialCharacters() {
        // prepare parameters
        Map<String, Object> params = new HashMap<>();

        // Simplified test, only basic HTML special characters
        String content = "&lt;div&gt;test &amp; special character&lt;/div&gt;";
        String result = mailTemplateService.formatMailTemplateContent(content, params);
        // assert special characters are correctly unescaped
        assertTrue(result.contains("<div>test & special character</div>"));
    }

    @Test
    public void countByAccountId() {
        // mock data
        MailTemplateEntity dbMailTemplate = randomPojo(MailTemplateEntity.class);
        mailTemplateMapper.insert(dbMailTemplate);
        // test accountId mismatch
        mailTemplateMapper.insert(cloneIgnoreId(dbMailTemplate, o -> o.setAccountId(2L)));
        // prepare parameters
        Long accountId = dbMailTemplate.getAccountId();

        // invoke
        long count = mailTemplateService.getMailTemplateCountByAccountId(accountId);
        // assert
        assertEquals(1, count);
    }

    @Test
    public void differenceWithHtmlContent() {
        // prepare template content containing HTML format
        String content = "<div style='font-family: Arial, sans-serif; color: #333;'>" +
                "<h1>Welcome, {username}!</h1>" +
                "<p>Your account has been created successfully.</p>" +
                "<div style='background-color: #f0f0f0; padding: 10px; border-radius: 5px;'>" +
                "<strong>Account Details:</strong><br>" +
                "Username: {username}<br>" +
                "Email: {email}<br>" +
                "Role: {role}<br>" +
                "</div>" +
                "<p>Please click <a href='{activationLink}'>here</a> to activate your account.</p>" +
                "<pre><code>public class WelcomeMessage {\n    public static void main(String[] args) {\n        System.out.println(\"Hello {username}!\");\n    }\n}</code></pre>" +
                "</div>";

        Map<String, Object> params = new HashMap<>();
        params.put("username", "testuser");
        params.put("email", "test@163.com");
        params.put("role", "admin");
        params.put("activationLink", "https://example.com/activate?code=12345");

        // 1. Use parseTemplateContentParams: extract only parameter names, ignoring HTML format
        List<String> parsedParams = mailTemplateService.parseTemplateContentParams(content);
        System.out.println("parseTemplateContentParamsresult: " + parsedParams);

        // assert: only plain parameter names are extracted, no HTML
        assertEquals(6, parsedParams.size());
        // check all parameter types
        assertEquals(3, parsedParams.stream().filter("username"::equals).count());
        assertEquals(1, parsedParams.stream().filter("email"::equals).count());
        assertEquals(1, parsedParams.stream().filter("role"::equals).count());
        assertEquals(1, parsedParams.stream().filter("activationLink"::equals).count());
        // assert: no HTML tags are present
        for (String param : parsedParams) {
            assertFalse(param.contains("<"));
            assertFalse(param.contains(">"));
        }

        // 2. Use formatMailTemplateContent: handle HTML format and generate final content
        String formattedContent = mailTemplateService.formatMailTemplateContent(content, params);
        System.out.println("formatMailTemplateContentresult: " + formattedContent);

        // assert: HTML format is preserved and processed
        assertTrue(formattedContent.contains("<div style='font-family: Arial, sans-serif; color: #333;'>"));
        assertTrue(formattedContent.contains("<h1>Welcome, testuser!</h1>"));
        assertTrue(formattedContent.contains("<a href='https://example.com/activate?code=12345'>here</a>"));
        assertTrue(formattedContent.contains("<div><code>public class WelcomeMessage {"));
        assertTrue(formattedContent.contains("</code></div>"));
        // assert: all parameters are correctly substituted
        assertFalse(formattedContent.contains("{username}"));
        assertFalse(formattedContent.contains("{email}"));
        assertFalse(formattedContent.contains("{role}"));
        assertFalse(formattedContent.contains("{activationLink}"));
    }

}
