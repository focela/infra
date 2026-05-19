package com.focela.platform.system.controller.app.tenant;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.system.controller.app.tenant.response.AppTenantResponse;
import com.focela.platform.system.domain.entity.tenant.TenantEntity;
import com.focela.platform.system.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "User App - Tenant")
@RestController
@RequestMapping("/system/tenant")
@Validated
@RequiredArgsConstructor
public class AppTenantController {

    private final TenantService tenantService;

    @GetMapping("/get-by-website")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "Get tenant info by domain", description = "by user domain, get tenant info")
    @Parameter(name = "website", description = "Domain", required = true, example = "www.example.com")
    public CommonResult<AppTenantResponse> getTenantByWebsite(
            @RequestParam("website") @Pattern(regexp = "^[a-zA-Z0-9.-]+$", message = "website domain format is invalid") String website) {
        TenantEntity tenant = tenantService.getTenantByWebsite(website);
        if (tenant == null || CommonStatusEnum.isDisable(tenant.getStatus())) {
            return success(null);
        }
        return success(BeanUtils.toBean(tenant, AppTenantResponse.class));
    }

}
