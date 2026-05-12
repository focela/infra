package com.focela.platform.module.system.service.mail;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.test.core.ut.BaseDbUnitTest;
import com.focela.platform.module.system.controller.admin.mail.dto.account.MailAccountPageRequest;
import com.focela.platform.module.system.controller.admin.mail.dto.account.MailAccountSaveRequest;
import com.focela.platform.module.system.repository.entity.mail.MailAccountEntity;
import com.focela.platform.module.system.repository.mapper.mail.MailAccountMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.focela.platform.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.util.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.util.RandomUtils.*;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.MAIL_ACCOUNT_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * {@link MailAccountServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(MailAccountServiceImpl.class)
public class MailAccountServiceImplTest extends BaseDbUnitTest {

    @Resource
    private MailAccountServiceImpl mailAccountService;

    @Resource
    private MailAccountMapper mailAccountMapper;

    @MockitoBean
    private MailTemplateService mailTemplateService;

    @Test
    public void testCreateMailAccount_success() {
        // 准备参数
        MailAccountSaveRequest reqVO = randomPojo(MailAccountSaveRequest.class, o -> o.setMail(randomEmail()))
                .setId(null); // 防止 id 被赋值

        // 调用
        Long mailAccountId = mailAccountService.createMailAccount(reqVO);
        // 断言
        assertNotNull(mailAccountId);
        // 校验记录的属性是否正确
        MailAccountEntity mailAccount = mailAccountMapper.selectById(mailAccountId);
        assertPojoEquals(reqVO, mailAccount, "id");
    }

    @Test
    public void testUpdateMailAccount_success() {
        // mock 数据
        MailAccountEntity dbMailAccount = randomPojo(MailAccountEntity.class);
        mailAccountMapper.insert(dbMailAccount);// @Sql: 先插入出一条存在的数据
        // 准备参数
        MailAccountSaveRequest reqVO = randomPojo(MailAccountSaveRequest.class, o -> {
            o.setId(dbMailAccount.getId()); // 设置更新的 ID
            o.setMail(randomEmail());
        });

        // 调用
        mailAccountService.updateMailAccount(reqVO);
        // 校验是否更新正确
        MailAccountEntity mailAccount = mailAccountMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, mailAccount);
    }

    @Test
    public void testUpdateMailAccount_notExists() {
        // 准备参数
        MailAccountSaveRequest reqVO = randomPojo(MailAccountSaveRequest.class);

        // 调用, 并断言异常
        assertServiceException(() -> mailAccountService.updateMailAccount(reqVO), MAIL_ACCOUNT_NOT_EXISTS);
    }

    @Test
    public void testDeleteMailAccount_success() {
        // mock 数据
        MailAccountEntity dbMailAccount = randomPojo(MailAccountEntity.class);
        mailAccountMapper.insert(dbMailAccount);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbMailAccount.getId();
        // mock 方法（无关联模版）
        when(mailTemplateService.getMailTemplateCountByAccountId(eq(id))).thenReturn(0L);

        // 调用
        mailAccountService.deleteMailAccount(id);
        // 校验数据不存在了
        assertNull(mailAccountMapper.selectById(id));
    }

    @Test
    public void testGetMailAccountFromCache() {
        // mock 数据
        MailAccountEntity dbMailAccount = randomPojo(MailAccountEntity.class);
        mailAccountMapper.insert(dbMailAccount);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbMailAccount.getId();

        // 调用
        MailAccountEntity mailAccount = mailAccountService.getMailAccountFromCache(id);
        // 断言
        assertPojoEquals(dbMailAccount, mailAccount);
    }

    @Test
    public void testDeleteMailAccount_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> mailAccountService.deleteMailAccount(id), MAIL_ACCOUNT_NOT_EXISTS);
    }

    @Test
    public void testGetMailAccountPage() {
        // mock 数据
        MailAccountEntity dbMailAccount = randomPojo(MailAccountEntity.class, o -> { // 等会查询到
            o.setMail("768@qq.com");
            o.setUsername("yunai");
        });
        mailAccountMapper.insert(dbMailAccount);
        // 测试 mail 不匹配
        mailAccountMapper.insert(cloneIgnoreId(dbMailAccount, o -> o.setMail("788@qq.com")));
        // 测试 username 不匹配
        mailAccountMapper.insert(cloneIgnoreId(dbMailAccount, o -> o.setUsername("tudou")));
        // 准备参数
        MailAccountPageRequest reqVO = new MailAccountPageRequest();
        reqVO.setMail("768");
        reqVO.setUsername("yu");

        // 调用
        PageResult<MailAccountEntity> pageResult = mailAccountService.getMailAccountPage(reqVO);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbMailAccount, pageResult.getList().get(0));
    }

    @Test
    public void testGetMailAccount() {
        // mock 数据
        MailAccountEntity dbMailAccount = randomPojo(MailAccountEntity.class);
        mailAccountMapper.insert(dbMailAccount);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbMailAccount.getId();

        // 调用
        MailAccountEntity mailAccount = mailAccountService.getMailAccount(id);
        // 断言
        assertPojoEquals(dbMailAccount, mailAccount);
    }

    @Test
    public void testGetMailAccountList() {
        // mock 数据
        MailAccountEntity dbMailAccount01 = randomPojo(MailAccountEntity.class);
        mailAccountMapper.insert(dbMailAccount01);
        MailAccountEntity dbMailAccount02 = randomPojo(MailAccountEntity.class);
        mailAccountMapper.insert(dbMailAccount02);
        // 准备参数

        // 调用
        List<MailAccountEntity> list = mailAccountService.getMailAccountList();
        // 断言
        assertEquals(2, list.size());
        assertPojoEquals(dbMailAccount01, list.get(0));
        assertPojoEquals(dbMailAccount02, list.get(1));
    }

}
