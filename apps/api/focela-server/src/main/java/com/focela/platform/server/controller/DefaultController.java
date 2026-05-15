package com.focela.platform.server.controller;

import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.servlet.ServletUtils;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.focela.platform.common.exception.enums.GlobalErrorCodeConstants.NOT_IMPLEMENTED;

/**
 * Default Controller; handles 404 responses when certain modules are not enabled.
 * For example, the /bpm/** path for the workflow module.
 */
@RestController
@Slf4j
public class DefaultController {

    @RequestMapping("/admin-api/bpm/**")
    public CommonResult<Boolean> bpm404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[Workflow module - disabled][See https://www.example.com/bpm/ to enable]");
    }

    @RequestMapping("/admin-api/mp/**")
    public CommonResult<Boolean> mp404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[WeChat Official Account module - disabled][See https://www.example.com/mp/build/ to enable]");
    }

    @RequestMapping(value = { "/admin-api/product/**", // Product center
            "/admin-api/trade/**", // Trade center
            "/admin-api/promotion/**" }) // Promotion center
    public CommonResult<Boolean> mall404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[Mall system - disabled][See https://www.example.com/mall/build/ to enable]");
    }

    @RequestMapping("/admin-api/erp/**")
    public CommonResult<Boolean> erp404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[ERP module - disabled][See https://www.example.com/erp/build/ to enable]");
    }

    @RequestMapping("/admin-api/crm/**")
    public CommonResult<Boolean> crm404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[CRM module - disabled][See https://www.example.com/crm/build/ to enable]");
    }

    @RequestMapping(value = { "/admin-api/report/**"})
    public CommonResult<Boolean> report404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[Report module - disabled][See https://www.example.com/report/ to enable]");
    }

    @RequestMapping(value = { "/admin-api/pay/**"})
    public CommonResult<Boolean> pay404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[Pay module - disabled][See https://www.example.com/pay/build/ to enable]");
    }

    @RequestMapping(value = { "/admin-api/ai/**"})
    public CommonResult<Boolean> ai404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[AI module - disabled][See https://www.example.com/ai/build/ to enable]");
    }

    @RequestMapping(value = { "/admin-api/iot/**"})
    public CommonResult<Boolean> iot404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[IoT module - disabled][See https://www.example.com/iot/build/ to enable]");
    }

    /**
     * Test endpoint: prints query, header, and body.
     */
    @RequestMapping(value = { "/test" })
    @PermitAll
    public CommonResult<Boolean> test(HttpServletRequest request) {
        // Print query parameters
        log.info("Query: {}", ServletUtils.getParamMap(request));
        // Print request headers
        log.info("Header: {}", ServletUtils.getHeaderMap(request));
        // Print request body
        log.info("Body: {}", ServletUtils.getBody(request));
        return CommonResult.success(true);
    }

}
