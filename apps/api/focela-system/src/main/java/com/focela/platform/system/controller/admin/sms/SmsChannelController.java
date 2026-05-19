package com.focela.platform.system.controller.admin.sms;

import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.sms.request.channel.SmsChannelPageRequest;
import com.focela.platform.system.controller.admin.sms.response.channel.SmsChannelResponse;
import com.focela.platform.system.controller.admin.sms.request.channel.SmsChannelSaveRequest;
import com.focela.platform.system.controller.admin.sms.response.channel.SmsChannelSimpleResponse;
import com.focela.platform.system.domain.entity.sms.SmsChannelEntity;
import com.focela.platform.system.service.sms.SmsChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - SMS channel")
@RestController
@RequestMapping("system/sms-channel")
@RequiredArgsConstructor
public class SmsChannelController {

        private final SmsChannelService smsChannelService;

    @PostMapping("/create")
    @Operation(summary = "create SMS channel")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:create')")
    public CommonResult<Long> createSmsChannel(@Valid @RequestBody SmsChannelSaveRequest createRequest) {
        return success(smsChannelService.createSmsChannel(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update SMS channel")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:update')")
    public CommonResult<Boolean> updateSmsChannel(@Valid @RequestBody SmsChannelSaveRequest updateRequest) {
        smsChannelService.updateSmsChannel(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete SMS channel")
    @Parameter(name = "id", description = "ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:sms-channel:delete')")
    public CommonResult<Boolean> deleteSmsChannel(@RequestParam("id") Long id) {
        smsChannelService.deleteSmsChannel(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "ID list", required = true)
    @Operation(summary = "batch delete SMS channel")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:delete')")
    public CommonResult<Boolean> deleteSmsChannelList(@RequestParam("ids") List<Long> ids) {
        smsChannelService.deleteSmsChannelList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get SMS channel")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:query')")
    public CommonResult<SmsChannelResponse> getSmsChannel(@RequestParam("id") Long id) {
        SmsChannelEntity channel = smsChannelService.getSmsChannel(id);
        return success(BeanUtils.toBean(channel, SmsChannelResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get SMS channel page")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:query')")
    public CommonResult<PageResult<SmsChannelResponse>> getSmsChannelPage(@Valid SmsChannelPageRequest pageRequest) {
        PageResult<SmsChannelEntity> pageResult = smsChannelService.getSmsChannelPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, SmsChannelResponse.class));
    }

    @GetMapping({"/list-all-simple", "/simple-list"})
    @Operation(summary = "get SMS channel simplified list", description = "contain disabled SMS channel")
    public CommonResult<List<SmsChannelSimpleResponse>> getSimpleSmsChannelList() {
        List<SmsChannelEntity> smsChannels = smsChannelService.getSmsChannelList();
        smsChannels.sort(Comparator.comparing(SmsChannelEntity::getId));
        return success(BeanUtils.toBean(smsChannels, SmsChannelSimpleResponse.class));
    }

}
