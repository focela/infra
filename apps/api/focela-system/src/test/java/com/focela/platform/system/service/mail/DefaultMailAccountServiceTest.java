package com.focela.platform.system.service.mail;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.mail.request.account.MailAccountPageRequest;
import com.focela.platform.system.controller.admin.mail.request.account.MailAccountSaveRequest;
import com.focela.platform.system.domain.entity.mail.MailAccountEntity;
import com.focela.platform.system.repository.mapper.mail.MailAccountMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.MAIL_ACCOUNT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * {@link DefaultMailAccountService}  unit test class
 */
@Import(DefaultMailAccountService.class)
public class DefaultMailAccountServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultMailAccountService mailAccountService;

    @Resource
    private MailAccountMapper mailAccountMapper;

    @MockitoBean
    private MailTemplateService mailTemplateService;

    @Test
    public void createMailAccount_success() {
        // prepare parameters
        MailAccountSaveRequest request = randomPojo(MailAccountSaveRequest.class, o -> o.setMail(randomEmail()))
                .setId(null); // prevent id from being assigned

        // invoke
        Long mailAccountId = mailAccountService.createMailAccount(request);
        // assert
        assertNotNull(mailAccountId);
        // verify record properties are correct
        MailAccountEntity mailAccount = mailAccountMapper.selectById(mailAccountId);
        assertPojoEquals(request, mailAccount, "id");
    }

    @Test
    public void updateMailAccount_success() {
        // mock data
        MailAccountEntity dbMailAccount = randomPojo(MailAccountEntity.class);
        mailAccountMapper.insert(dbMailAccount);// @Sql: first insert an existing record
        // prepare parameters
        MailAccountSaveRequest request = randomPojo(MailAccountSaveRequest.class, o -> {
            o.setId(dbMailAccount.getId()); // set updated ID
            o.setMail(randomEmail());
        });

        // invoke
        mailAccountService.updateMailAccount(request);
        // verify update is correct
        MailAccountEntity mailAccount = mailAccountMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, mailAccount);
    }

    @Test
    public void updateMailAccount_missing() {
        // prepare parameters
        MailAccountSaveRequest request = randomPojo(MailAccountSaveRequest.class);

        // invoke and assert exception
        assertServiceException(() -> mailAccountService.updateMailAccount(request), MAIL_ACCOUNT_NOT_FOUND);
    }

    @Test
    public void deleteMailAccount_success() {
        // mock data
        MailAccountEntity dbMailAccount = randomPojo(MailAccountEntity.class);
        mailAccountMapper.insert(dbMailAccount);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbMailAccount.getId();
        // mock the method（no associated template）
        when(mailTemplateService.getMailTemplateCountByAccountId(eq(id))).thenReturn(0L);

        // invoke
        mailAccountService.deleteMailAccount(id);
        // verify data no longer exists
        assertNull(mailAccountMapper.selectById(id));
    }

    @Test
    public void getMailAccountFromCache() {
        // mock data
        MailAccountEntity dbMailAccount = randomPojo(MailAccountEntity.class);
        mailAccountMapper.insert(dbMailAccount);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbMailAccount.getId();

        // invoke
        MailAccountEntity mailAccount = mailAccountService.getMailAccountFromCache(id);
        // assert
        assertPojoEquals(dbMailAccount, mailAccount);
    }

    @Test
    public void deleteMailAccount_missing() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> mailAccountService.deleteMailAccount(id), MAIL_ACCOUNT_NOT_FOUND);
    }

    @Test
    public void getMailAccountPage() {
        // mock data
        MailAccountEntity dbMailAccount = randomPojo(MailAccountEntity.class, o -> { // will be queried later
            o.setMail("768@qq.com");
            o.setUsername("focela_sample");
        });
        mailAccountMapper.insert(dbMailAccount);
        // test mail mismatch
        mailAccountMapper.insert(cloneIgnoreId(dbMailAccount, o -> o.setMail("788@qq.com")));
        // test username mismatch
        mailAccountMapper.insert(cloneIgnoreId(dbMailAccount, o -> o.setUsername("focela_alternate")));
        // prepare parameters
        MailAccountPageRequest request = new MailAccountPageRequest();
        request.setMail("768");
        request.setUsername("focela_sample");

        // invoke
        PageResult<MailAccountEntity> pageResult = mailAccountService.getMailAccountPage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbMailAccount, pageResult.getList().get(0));
    }

    @Test
    public void getMailAccount() {
        // mock data
        MailAccountEntity dbMailAccount = randomPojo(MailAccountEntity.class);
        mailAccountMapper.insert(dbMailAccount);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbMailAccount.getId();

        // invoke
        MailAccountEntity mailAccount = mailAccountService.getMailAccount(id);
        // assert
        assertPojoEquals(dbMailAccount, mailAccount);
    }

    @Test
    public void getMailAccountList() {
        // mock data
        MailAccountEntity dbMailAccount01 = randomPojo(MailAccountEntity.class);
        mailAccountMapper.insert(dbMailAccount01);
        MailAccountEntity dbMailAccount02 = randomPojo(MailAccountEntity.class);
        mailAccountMapper.insert(dbMailAccount02);
        // prepare parameters

        // invoke
        List<MailAccountEntity> list = mailAccountService.getMailAccountList();
        // assert
        assertEquals(2, list.size());
        assertPojoEquals(dbMailAccount01, list.get(0));
        assertPojoEquals(dbMailAccount02, list.get(1));
    }

}
