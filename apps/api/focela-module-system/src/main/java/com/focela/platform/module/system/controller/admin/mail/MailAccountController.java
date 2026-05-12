package com.focela.platform.module.system.controller.admin.mail;


import com.focela.platform.framework.common.pojo.CommonResult;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.mail.dto.account.MailAccountPageRequest;
import com.focela.platform.module.system.controller.admin.mail.dto.account.MailAccountResponse;
import com.focela.platform.module.system.controller.admin.mail.dto.account.MailAccountSaveRequest;
import com.focela.platform.module.system.controller.admin.mail.dto.account.MailAccountSimpleResponse;
import com.focela.platform.module.system.repository.entity.mail.MailAccountEntity;
import com.focela.platform.module.system.service.mail.MailAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.focela.platform.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 邮箱账号")
@RestController
@RequestMapping("/system/mail-account")
public class MailAccountController {

    @Resource
    private MailAccountService mailAccountService;

    @PostMapping("/create")
    @Operation(summary = "创建邮箱账号")
    @PreAuthorize("@ss.hasPermission('system:mail-account:create')")
    public CommonResult<Long> createMailAccount(@Valid @RequestBody MailAccountSaveRequest createRequest) {
        return success(mailAccountService.createMailAccount(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "修改邮箱账号")
    @PreAuthorize("@ss.hasPermission('system:mail-account:update')")
    public CommonResult<Boolean> updateMailAccount(@Valid @RequestBody MailAccountSaveRequest updateRequest) {
        mailAccountService.updateMailAccount(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除邮箱账号")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:mail-account:delete')")
    public CommonResult<Boolean> deleteMailAccount(@RequestParam Long id) {
        mailAccountService.deleteMailAccount(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除邮箱账号")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:mail-account:delete')")
    public CommonResult<Boolean> deleteMailAccountList(@RequestParam("ids") List<Long> ids) {
        mailAccountService.deleteMailAccountList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得邮箱账号")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:mail-account:query')")
    public CommonResult<MailAccountResponse> getMailAccount(@RequestParam("id") Long id) {
        MailAccountEntity account = mailAccountService.getMailAccount(id);
        return success(BeanUtils.toBean(account, MailAccountResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得邮箱账号分页")
    @PreAuthorize("@ss.hasPermission('system:mail-account:query')")
    public CommonResult<PageResult<MailAccountResponse>> getMailAccountPage(@Valid MailAccountPageRequest pageRequest) {
        PageResult<MailAccountEntity> pageResult = mailAccountService.getMailAccountPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, MailAccountResponse.class));
    }

    @GetMapping({"/list-all-simple", "simple-list"})
    @Operation(summary = "获得邮箱账号精简列表")
    public CommonResult<List<MailAccountSimpleResponse>> getSimpleMailAccountList() {
        List<MailAccountEntity> list = mailAccountService.getMailAccountList();
        return success(BeanUtils.toBean(list, MailAccountSimpleResponse.class));
    }

}
