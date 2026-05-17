package com.focela.platform.system.controller.admin.social;

import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.social.dto.client.SocialClientPageRequest;
import com.focela.platform.system.controller.admin.social.dto.client.SocialClientResponse;
import com.focela.platform.system.controller.admin.social.dto.client.SocialClientSaveRequest;
import com.focela.platform.system.entity.social.SocialClientEntity;
import com.focela.platform.system.service.social.SocialClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - Social Client")
@RestController
@RequestMapping("/system/social-client")
@Validated
@RequiredArgsConstructor
public class SocialClientController {

    private final SocialClientService socialClientService;

    @PostMapping("/create")
    @Operation(summary = "create social client")
    @PreAuthorize("@ss.hasPermission('system:social-client:create')")
    public CommonResult<Long> createSocialClient(@Valid @RequestBody SocialClientSaveRequest createRequest) {
        return success(socialClientService.createSocialClient(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update social client")
    @PreAuthorize("@ss.hasPermission('system:social-client:update')")
    public CommonResult<Boolean> updateSocialClient(@Valid @RequestBody SocialClientSaveRequest updateRequest) {
        socialClientService.updateSocialClient(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete social client")
    @Parameter(name = "id", description = "ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:social-client:delete')")
    public CommonResult<Boolean> deleteSocialClient(@RequestParam("id") Long id) {
        socialClientService.deleteSocialClient(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "ID list", required = true)
    @Operation(summary = "batch delete social client")
    @PreAuthorize("@ss.hasPermission('system:social-client:delete')")
    public CommonResult<Boolean> deleteSocialClientList(@RequestParam("ids") List<Long> ids) {
        socialClientService.deleteSocialClientList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get social client")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:social-client:query')")
    public CommonResult<SocialClientResponse> getSocialClient(@RequestParam("id") Long id) {
        SocialClientEntity client = socialClientService.getSocialClient(id);
        return success(BeanUtils.toBean(client, SocialClientResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get social client page")
    @PreAuthorize("@ss.hasPermission('system:social-client:query')")
    public CommonResult<PageResult<SocialClientResponse>> getSocialClientPage(@Valid SocialClientPageRequest pageRequest) {
        PageResult<SocialClientEntity> pageResult = socialClientService.getSocialClientPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, SocialClientResponse.class));
    }

}
