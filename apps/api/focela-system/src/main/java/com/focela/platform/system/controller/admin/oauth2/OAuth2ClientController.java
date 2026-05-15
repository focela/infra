package com.focela.platform.system.controller.admin.oauth2;

import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.oauth2.dto.client.OAuth2ClientPageRequest;
import com.focela.platform.system.controller.admin.oauth2.dto.client.OAuth2ClientResponse;
import com.focela.platform.system.controller.admin.oauth2.dto.client.OAuth2ClientSaveRequest;
import com.focela.platform.system.entity.oauth2.OAuth2ClientEntity;
import com.focela.platform.system.service.oauth2.OAuth2ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "Admin - OAuth2 client")
@RestController
@RequestMapping("/system/oauth2-client")
@Validated
public class OAuth2ClientController {

    @Resource
    private OAuth2ClientService oAuth2ClientService;

    @PostMapping("/create")
    @Operation(summary = "create OAuth2 client")
    @PreAuthorize("@ss.hasPermission('system:oauth2-client:create')")
    public CommonResult<Long> createOAuth2Client(@Valid @RequestBody OAuth2ClientSaveRequest createRequest) {
        return success(oAuth2ClientService.createOAuth2Client(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update OAuth2 client")
    @PreAuthorize("@ss.hasPermission('system:oauth2-client:update')")
    public CommonResult<Boolean> updateOAuth2Client(@Valid @RequestBody OAuth2ClientSaveRequest updateRequest) {
        oAuth2ClientService.updateOAuth2Client(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete OAuth2 client")
    @Parameter(name = "id", description = "ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:oauth2-client:delete')")
    public CommonResult<Boolean> deleteOAuth2Client(@RequestParam("id") Long id) {
        oAuth2ClientService.deleteOAuth2Client(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "ID list", required = true)
    @Operation(summary = "batch delete OAuth2 client")
    @PreAuthorize("@ss.hasPermission('system:oauth2-client:delete')")
    public CommonResult<Boolean> deleteOAuth2ClientList(@RequestParam("ids") List<Long> ids) {
        oAuth2ClientService.deleteOAuth2ClientList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get OAuth2 client")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:oauth2-client:query')")
    public CommonResult<OAuth2ClientResponse> getOAuth2Client(@RequestParam("id") Long id) {
        OAuth2ClientEntity client = oAuth2ClientService.getOAuth2Client(id);
        return success(BeanUtils.toBean(client, OAuth2ClientResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get OAuth2 client page")
    @PreAuthorize("@ss.hasPermission('system:oauth2-client:query')")
    public CommonResult<PageResult<OAuth2ClientResponse>> getOAuth2ClientPage(@Valid OAuth2ClientPageRequest pageVO) {
        PageResult<OAuth2ClientEntity> pageResult = oAuth2ClientService.getOAuth2ClientPage(pageVO);
        return success(BeanUtils.toBean(pageResult, OAuth2ClientResponse.class));
    }

}
