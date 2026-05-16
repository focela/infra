package com.focela.platform.system.service.mail;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.mail.dto.account.MailAccountPageRequest;
import com.focela.platform.system.controller.admin.mail.dto.account.MailAccountSaveRequest;
import com.focela.platform.system.entity.mail.MailAccountEntity;
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
import static com.focela.platform.system.constants.ErrorCodeConstants.MAIL_ACCOUNT_NOT_EXISTS;
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
    public void testCreateMailAccount_success() {
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
    public void testUpdateMailAccount_success() {
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
    public void testUpdateMailAccount_notExists() {
        // prepare parameters
        MailAccountSaveRequest request = randomPojo(MailAccountSaveRequest.class);

        // invoke and assert exception
        assertServiceException(() -> mailAccountService.updateMailAccount(request), MAIL_ACCOUNT_NOT_EXISTS);
    }

    @Test
    public void testDeleteMailAccount_success() {
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
    public void testGetMailAccountFromCache() {
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
    public void testDeleteMailAccount_notExists() {
        // prepare parameters
        Long id = randomLongId();

        // invoke and assert exception
        assertServiceException(() -> mailAccountService.deleteMailAccount(id), MAIL_ACCOUNT_NOT_EXISTS);
    }

    @Test
    public void testGetMailAccountPage() {
        // mock data
        MailAccountEntity dbMailAccount = randomPojo(MailAccountEntity.class, o -> { // will be queried later
            o.setMail("768@qq.com");
            o.setUsername("yunai");
        });
        mailAccountMapper.insert(dbMailAccount);
        // test mail mismatch
        mailAccountMapper.insert(cloneIgnoreId(dbMailAccount, o -> o.setMail("788@qq.com")));
        // test username mismatch
        mailAccountMapper.insert(cloneIgnoreId(dbMailAccount, o -> o.setUsername("tudou")));
        // prepare parameters
        MailAccountPageRequest request = new MailAccountPageRequest();
        request.setMail("768");
        request.setUsername("yu");

        // invoke
        PageResult<MailAccountEntity> pageResult = mailAccountService.getMailAccountPage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbMailAccount, pageResult.getList().get(0));
    }

    @Test
    public void testGetMailAccount() {
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
    public void testGetMailAccountList() {
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
