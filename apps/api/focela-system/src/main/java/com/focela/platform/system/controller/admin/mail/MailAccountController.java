package com.focela.platform.system.controller.admin.mail;


import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.mail.request.account.MailAccountPageRequest;
import com.focela.platform.system.controller.admin.mail.response.account.MailAccountResponse;
import com.focela.platform.system.controller.admin.mail.request.account.MailAccountSaveRequest;
import com.focela.platform.system.controller.admin.mail.response.account.MailAccountSimpleResponse;
import com.focela.platform.system.domain.entity.mail.MailAccountEntity;
import com.focela.platform.system.service.mail.MailAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - Email account")
@RestController
@RequestMapping("/system/mail-account")
@RequiredArgsConstructor
public class MailAccountController {

    private final MailAccountService mailAccountService;

    @PostMapping("/create")
    @Operation(summary = "create email account")
    @PreAuthorize("@ss.hasPermission('system:mail-account:create')")
    public CommonResult<Long> createMailAccount(@Valid @RequestBody MailAccountSaveRequest createRequest) {
        return success(mailAccountService.createMailAccount(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update email account")
    @PreAuthorize("@ss.hasPermission('system:mail-account:update')")
    public CommonResult<Boolean> updateMailAccount(@Valid @RequestBody MailAccountSaveRequest updateRequest) {
        mailAccountService.updateMailAccount(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete email account")
    @Parameter(name = "id", description = "ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:mail-account:delete')")
    public CommonResult<Boolean> deleteMailAccount(@RequestParam Long id) {
        mailAccountService.deleteMailAccount(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete email account")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('system:mail-account:delete')")
    public CommonResult<Boolean> deleteMailAccountList(@RequestParam("ids") List<Long> ids) {
        mailAccountService.deleteMailAccountList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get email account")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:mail-account:query')")
    public CommonResult<MailAccountResponse> getMailAccount(@RequestParam("id") Long id) {
        MailAccountEntity account = mailAccountService.getMailAccount(id);
        return success(BeanUtils.toBean(account, MailAccountResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get email account page")
    @PreAuthorize("@ss.hasPermission('system:mail-account:query')")
    public CommonResult<PageResult<MailAccountResponse>> getMailAccountPage(@Valid MailAccountPageRequest pageRequest) {
        PageResult<MailAccountEntity> pageResult = mailAccountService.getMailAccountPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, MailAccountResponse.class));
    }

    @GetMapping({"/list-all-simple", "/simple-list"})
    @Operation(summary = "get email account simplified list")
    public CommonResult<List<MailAccountSimpleResponse>> getSimpleMailAccountList() {
        List<MailAccountEntity> mailAccounts = mailAccountService.getMailAccountList();
        return success(BeanUtils.toBean(mailAccounts, MailAccountSimpleResponse.class));
    }

}
